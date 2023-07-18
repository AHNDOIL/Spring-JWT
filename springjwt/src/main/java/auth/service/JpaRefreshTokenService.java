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


    /**
     * 새로운 RefreshToken을 저장하는 메서드
     * @param tokenDto 저장할 RefreshToken 정보를 담은 TokenDto 객체
     * @param username 저장할 RefreshToken에 연결된 사용자 이름
     */
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


    /**
     * 주어진 RefreshToken을 사용하여 새로운 AccessToken을 재발급하는 메서드
     * @param refreshToken 재발급에 사용될 RefreshToken
     * @return 재발급된 AccessToken 문자열
     */
    @Override
    public String reCreateAccessTokenByRefreshToken(String refreshToken) {
        if(tokenProvider.validateToken(refreshToken)){
            refreshToken = refreshToken.substring(7);
            return tokenProvider.reCreateAccessToken(refreshToken);
        }
        return null;
    }


    /**
     * 주어진 RefreshToken을 사용하여 새로운 RefreshToken을 발급하는 메서드
     * @param refreshToken 재발급에 사용될 RefreshToken
     * @return 재발급된 RefreshToken 문자열
     */
    @Override
    public String reCreateRefreshTokenByRefreshToken(String refreshToken) {
        if(tokenProvider.validateToken(refreshToken)){
            refreshToken = refreshToken.substring(7);
            return tokenProvider.createRefreshToken(tokenProvider.getUsername(refreshToken));
        }
        return null;
    }
}
