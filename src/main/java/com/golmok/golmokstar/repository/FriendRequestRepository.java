package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.FriendRequest;
import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsByRequesterAndReceiver(User requester, User receiver);
}