package com.example.prj.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Keep tokenString for compatibility/migration; prefer storing tokenHash instead of plaintext
    @Column(length = 512, unique = true)
    private String tokenString;

    // Store SHA-256(token) to avoid keeping JWT plaintext in the database
    @Column(length = 64, unique = true)
    private String tokenHash;

    private LocalDateTime revokedAt;
}
