package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.FriendRequest;
import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsByRequesterAndReceiver(User requester, User receiver);

    Optional<FriendRequest> findByRequestId(Long requestId);
}