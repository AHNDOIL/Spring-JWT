package auth.service;

import auth.dto.TokenDto;

public interface RefreshTokenService {

    void saveRefreshToken(TokenDto tokenDto, String username);

    String reCreateAccessTokenByRefreshToken(String refreshToken);
    String reCreateRefreshTokenByRefreshToken(String refreshToken);
}
