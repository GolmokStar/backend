
package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.CreateFriendRequestDto;
import com.golmok.golmokstar.dto.CreateFriendRequestResponseDto;
import com.golmok.golmokstar.entity.FriendRequest;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.repository.FriendRepository;
import com.golmok.golmokstar.repository.FriendRequestRepository;
import com.golmok.golmokstar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
}