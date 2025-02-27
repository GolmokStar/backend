
package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Friend;
import com.golmok.golmokstar.entity.FriendRequest;
import com.golmok.golmokstar.entity.RequestStatus;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.repository.FriendRepository;
import com.golmok.golmokstar.repository.FriendRequestRepository;
import com.golmok.golmokstar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendRequestService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendRequestService(FriendRepository friendRepository, UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    // 친구 요청 목록 조회
    @Transactional
    public List<FriendRequestListResponseDto> getFriendRequestsByFriendCode(String friendCode) {
        // 1. 입력받은 friendCode 를 가진 User 를 찾는다
        User user = userRepository.findByFriendCode(friendCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 friendCode 를 가진 User 를 찾을 수 없습니다."));

        // 그 유저에게 온 friendRequest 를 모두 찾아서 List 로 저장한다.
        List<FriendRequest> friendRequests = friendRequestRepository.findByReceiver(user);

        // friendRequests 가 비어 있다면 받은 친구 요청이 없는 것이므로 예외를 발생시킨다.
        if (friendRequests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청이 없습니다.");
        }

        // FriendRequest 객체로 구성된 리스트는 불필요한 정보가 많으므로, 필요한 정보만 담는 FriendRequestListResponseDto 객체로 매핑한 뒤 리스트를 만들어 반환한다.
        return friendRequests.stream()
                .map(friendRequest -> new FriendRequestListResponseDto(
                        friendRequest.getRequestId(),
                        friendRequest.getRequester().getNickname()))
                .collect(Collectors.toList());
    }

    // 친구요청 생성
    @Transactional
    public CreateFriendRequestResponseDto createFriendRequest(CreateFriendRequestDto dto) {
        // 입력받은 requesterFriendCode, receiverFriendCode 로 User 객체 찾기
        User requester = userRepository.findByFriendCode(dto.getRequesterFriendCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 입력 데이터입니다.(요청하는 코드)."));
        User receiver = userRepository.findByFriendCode(dto.getReceiverFriendCode())
                .orElseThrow(() -> new IllegalArgumentException(("유효하지 않은 입력 데이터입니다. (요청받는 코드).")));

        // 이미 친구인 경우 검증
        if (friendRequestRepository.existsByRequesterAndReceiver(requester, receiver)) {
            throw new IllegalArgumentException("이미 친구 요청이 존재합니다.");
        }

        // 모든 검증 통과. 친구 요청 생성
        FriendRequest friendRequest = new FriendRequest(requester, receiver, LocalDate.now());
        friendRequestRepository.save(friendRequest);

        return new CreateFriendRequestResponseDto(friendRequest.getRequestId());
    }

    // 친구 요청 수락 및 친구 추가
    @Transactional
    public AcceptFriendRequestResponseDto acceptFriendRequest(Long requestId) {
        // requestId로 friendRequest 조회
        FriendRequest friendRequest = friendRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 requestId 입니다."));

        // 해당 friendRequest 의 status = ACCEPTED, 날자는 오늘로 설정하고 저장
        friendRequest.setRequestStatus(RequestStatus.ACCEPTED);
        friendRequest.setResponseDate(LocalDate.now());
        friendRequestRepository.save(friendRequest);

        // 요청을 수락했으므로 실제 친구 관계도 저장
        Friend newFriend = new Friend(friendRequest.getRequester(), friendRequest.getReceiver(), LocalDate.now(), 0);
        friendRepository.save(newFriend);

        return new AcceptFriendRequestResponseDto(friendRequest.getRequestId(), friendRequest.getRequestStatus(), friendRequest.getResponseDate());
    }

    // 친구 요청 거절
    @Transactional
    public RejectFriendRequestResponse rejectFriendRequest(Long requestId) {
        // requestId로 friendRequest 조회
        FriendRequest friendRequest = friendRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 requestId 입니다."));

        // 해당 friendRequest 의 status = REJECTED, 날자는 오늘로 설정하고 반환
        friendRequest.setRequestStatus(RequestStatus.REJECTED);
        friendRequest.setResponseDate(LocalDate.now());

        friendRequestRepository.save(friendRequest);

        return new RejectFriendRequestResponse(friendRequest.getRequestId(), friendRequest.getRequestStatus(), friendRequest.getResponseDate());
    }

    // 친구 요청 삭제
    @Transactional
    public DeleteFriendRequestResponseDto deleteFriendRequest(Long requestId) {
        if(!friendRequestRepository.existsById(requestId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 requestId 입니다.");
        }

        friendRequestRepository.deleteById(requestId);

        return new DeleteFriendRequestResponseDto(requestId);
    }
}