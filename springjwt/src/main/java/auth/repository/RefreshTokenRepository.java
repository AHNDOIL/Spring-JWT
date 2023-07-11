package auth.repository;

import auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByUsername(String userEmail);

    Optional<RefreshToken> findByUsername(String username);
}
