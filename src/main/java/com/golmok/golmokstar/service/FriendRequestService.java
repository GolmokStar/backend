
package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.*;
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

import java.time.LocalDateTime;

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

    // 친구요청 생성 로직
    @Transactional
    public CreateFriendRequestResponseDto createFriendRequest(CreateFriendRequestDto dto) {
        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 입력 데이터입니다.(요청하는ID)"));
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException(("유효하지 않은 입력 데이터입니다. (요청받는ID).")));

        if (friendRequestRepository.existsByRequesterAndReceiver(requester, receiver)) {
            throw new IllegalArgumentException("이미 친구 요청이 존재합니다.");
        }

        FriendRequest friendRequest = new FriendRequest(requester, receiver, LocalDateTime.now());
        friendRequestRepository.save(friendRequest);

        return new CreateFriendRequestResponseDto(friendRequest.getRequestId());
    }

    // 친구 요청 상태 조회 로직
    @Transactional
    public GetFriendRequestStatusDetailResponseDto getFriendRequestStatusDetail(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findByRequestId(requestId)
                // requestId 가 없는 경우, 명세서에서 요구하는 404 에러를 던지기 위해 ResponseStatusException 사용
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 friendId를 찾을 수 없습니다"));

        return new GetFriendRequestStatusDetailResponseDto(
                friendRequest.getRequestId(),
                friendRequest.getRequester().getUserId(),
                friendRequest.getReceiver().getUserId(),
                friendRequest.getRequestStatus(),
                friendRequest.getRequestDate(),
                friendRequest.getResponseDate()
        );
    }

    // 친구 요청 수락&거절 로직
    @Transactional
    public RespondToFriendRequestResponseDto respondToFriendRequest(Long requestId, RespondToFriendRequestRequestDto dto) {
        FriendRequest friendRequest = friendRequestRepository.findByRequestId(requestId)
                // requestId 없는 경우 핸들링. ResourceNotFoundException 커스텀 예외 도입 예정
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 requestId를 찾을 수 없습니다"));

        // RequestStatus가 accepted 혹은 rejected 가 아닌 경우 핸들링(대소문자 유효성 검증은 ENUM에 있음)
        if (dto.getStatus() == RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 요청 상태입니다.\n(accepted/rejected) 만 허용)");
        }

        friendRequest.setRequestStatus(dto.getStatus());
        friendRequest.setResponseDate(LocalDateTime.now());
        friendRequestRepository.save(friendRequest);

        return new RespondToFriendRequestResponseDto(friendRequest.getRequestId(), friendRequest.getRequestStatus());
    }

    // 친구 요청 삭제
    @Transactional
    public DeleteFriendRequestResponseDto deleteFriendRequest(Long requestId) {
        if(!friendRequestRepository.existsById(requestId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 requestId를 찾을 수 없습니다");
        }

        friendRequestRepository.deleteById(requestId);

        return new DeleteFriendRequestResponseDto(requestId);
    }
}