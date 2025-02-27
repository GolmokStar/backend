
package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Friend;
import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByFriendId(Long friendId);

    // 친구 목록 조회시 사용한다.
    // 친구 등록 시 내 friendCode가 requester 일수도, receiver 일수도 있으므로 OR로 가져온다.
    List<Friend> findByCurrentUserOrFriendUser(User currentUser, User friendUser);
}
