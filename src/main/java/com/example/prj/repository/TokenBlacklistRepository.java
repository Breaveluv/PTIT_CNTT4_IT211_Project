package com.example.prj.repository;

import com.example.prj.model.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    Optional<TokenBlacklist> findByTokenString(String tokenString);

    boolean existsByTokenString(String tokenString);
}
