package com.example.outsourcing.domain.user.repository;

import com.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.outsourcing.common.exception.ErrorCode.USER_NOT_FOUND;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.id = :userId and u.deletedAt is null")
    Optional<User> findUserById(@Param("userId") Long userId);

    default User findUserByIdOrElseThrow(Long userId) {
        return findUserById(userId).orElseThrow(
                () -> new ResponseStatusException(
                        USER_NOT_FOUND.getStatus(),
                        USER_NOT_FOUND.getMessage()
                )
        );
    }

    boolean existsByEmail(String email);

    Optional<User> findUserByEmail(String email);

    default void deleteUserById(Long userId){
        User user = findUserByIdOrElseThrow(userId);
        user.setDeletedAt(LocalDateTime.now());
        save(user);
    }
}
