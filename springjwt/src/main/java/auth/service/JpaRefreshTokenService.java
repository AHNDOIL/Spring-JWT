package auth.service;

import auth.dto.TokenDto;
import auth.entity.RefreshToken;
import auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaRefreshTokenService implements RefreshTokenService{

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void saveRefreshToken(TokenDto tokenDto, String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(tokenDto.getRefreshToken())
                .username(username)
                .build();

        if(refreshTokenRepository.findByUsername(username).isPresent()){//이미 존재하면 삭제
            refreshTokenRepository.deleteByUsername(username);
        }
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public String reCreateAccessTokenByRefreshToken(String refreshToken) {
        if(tokenProvider.validateToken(refreshToken)){
            refreshToken = refreshToken.substring(7);
            return tokenProvider.reCreateAccessToken(refreshToken);
        }
        return null;
    }

    @Override
    public String reCreateRefreshTokenByRefreshToken(String refreshToken) {
        if(tokenProvider.validateToken(refreshToken)){
            refreshToken = refreshToken.substring(7);
            return tokenProvider.createRefreshToken(tokenProvider.getUsername(refreshToken));
        }
        return null;
    }
}
