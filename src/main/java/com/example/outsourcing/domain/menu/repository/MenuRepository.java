package com.example.outsourcing.domain.menu.repository;

import com.example.outsourcing.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("select m from Menu m where m.id = :menuId and m.deletedAt is null")
    Optional<Menu> findMenuById(@Param("menuId") Long menuId);
}
