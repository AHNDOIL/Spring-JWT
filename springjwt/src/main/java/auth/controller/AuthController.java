package auth.controller;



import auth.dto.SignInDto;
import auth.dto.TokenDto;
import auth.service.AuthService;
import auth.service.RefreshTokenService;
import auth.service.UserService;
import jwt.TokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;

    /**
     * 로그인을 처리하는 메서드
     * @param signInDto 로그인에 필요한 정보를 담은 SignInDto 객체
     * @return 로그인 결과에 대한 TokenDto 객체
     */
    @PostMapping("/sign-in")
    public ResponseEntity<TokenDto> signIn(@RequestBody SignInDto signInDto){
        TokenDto tokenDto = authService.signIn(signInDto);

        return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
    }


    /**
     * RefreshToken을 사용하여 새로운 AccessToken과 RefreshToken을 발급하는 메서드
     * @param refreshToken 사용할 RefreshToken
     * @return 발급된 새로운 AccessToken과 RefreshToken에 대한 TokenDto 객체
     */
    @PostMapping("/recreate-token")
    public ResponseEntity<TokenDto> reCreateToken(@RequestHeader("Authorization") String refreshToken){
        String newAccessToken = refreshTokenService.reCreateAccessTokenByRefreshToken(refreshToken);
        String newRefreshToken = refreshTokenService.reCreateRefreshTokenByRefreshToken(refreshToken);

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        refreshToken = refreshToken.substring(7);
        refreshTokenService.saveRefreshToken(tokenDto, tokenProvider.getUsername(refreshToken));

        return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
    }





}