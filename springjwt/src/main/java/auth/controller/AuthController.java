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

    @PostMapping("/sign-in")
    public ResponseEntity<TokenDto> signIn(@RequestBody SignInDto signInDto){
        TokenDto tokenDto = authService.signIn(signInDto);

        return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
    }

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