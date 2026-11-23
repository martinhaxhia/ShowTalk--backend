package com.viatech.showTalk.entites;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password_hash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(insertable = false)
    private LocalDateTime createdAt;


}
