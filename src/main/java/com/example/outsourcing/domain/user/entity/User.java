package com.example.outsourcing.domain.user.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(String email, String password, String username, String address, UserRole role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.address = address;
        this.role = role;
    }

    public User() {}

    public void changePassword(String password){
        this.password = password;
    }

    public void changeUserRole(UserRole role){
        this.role = role;
    }
}
