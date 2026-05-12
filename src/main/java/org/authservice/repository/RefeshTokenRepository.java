package org.authservice.repository;

import org.authservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefeshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken>findByToken(String token);

}
