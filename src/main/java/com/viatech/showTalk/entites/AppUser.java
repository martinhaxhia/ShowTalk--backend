package com.viatech.showTalk.entites;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor  //you can use annotation @Data includes both getters and setters
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password_hash;   //no need for hash just password

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(insertable = false)
    private LocalDateTime createdAt;


}
