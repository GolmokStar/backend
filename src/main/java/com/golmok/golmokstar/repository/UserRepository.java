package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGoogleId(String googleId);

    //닉네임 중복 체크
    Optional<User> findByNickname(String nickname);
}
