package com.example.outsourcing.domain.refreshToken.repository;

import com.example.outsourcing.domain.refreshToken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("delete from RefreshToken r where r.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
