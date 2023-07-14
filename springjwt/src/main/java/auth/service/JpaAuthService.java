package auth.service;

import auth.dto.SignInDto;
import auth.dto.TokenDto;
import auth.entity.RefreshToken;
import auth.entity.UserEntity;
import auth.repository.RefreshTokenRepository;
import auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaAuthService implements AuthService{

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    @Override
    public TokenDto signIn(SignInDto signInDto) {

        UserEntity user = userRepository.findByUsername(signInDto.getUsername());
        if(user == null){
            throw new RuntimeException("존재하지 않은 유저입니다.");
        }
        if(!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenProvider.createAccessToken(user.getUsername());
        String refreshToken = tokenProvider.createRefreshToken(user.getUsername());

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        refreshTokenService.saveRefreshToken(tokenDto, signInDto.getUsername());

        return tokenDto;
    }

    @Override
    public Boolean signOut() {
        return null;
    }


}
