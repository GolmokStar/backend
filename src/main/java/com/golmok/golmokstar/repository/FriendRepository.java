
package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Friend;
import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByCurrentUserAndFriendUser(User currentUser, User friendUser);

    Optional<Friend> findByFriendId(Long friendId);
}
