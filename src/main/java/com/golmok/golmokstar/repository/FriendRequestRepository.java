package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.FriendRequest;
import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsByRequesterAndReceiver(User requester, User receiver);

    Optional<FriendRequest> findByRequestId(Long requestId);

    // 입력받은 friendCode 를 가진 User 객체를 찾는다. 해당 User 객체를 receiver로 지정해 그 receiver를 가진 FriendRequest를 찾는다.
    List<FriendRequest> findByReceiver(User receiver);
}