package example.auth.service;

import example.auth.dto.SignInDto;
import example.auth.dto.TokenDto;
import example.auth.entity.UserEntity;
import example.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import example.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JpaAuthService implements AuthService{

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    /**
     * 로그인을 처리하고, 토큰을 발급하는 메서드
     * @param signInDto 로그인에 필요한 정보를 담은 SignInDto 객체
     * @return 토큰에 대한 TokenDto 객체
     * @throws RuntimeException 존재하지 않는 유저 또는 비밀번호 불일치 시 발생하는 예외 //예외 타입 변경해야됨
     */
    @Transactional
    @Override
    public TokenDto signIn(SignInDto signInDto) {

        UserEntity user = userRepository.findByUsername(signInDto.getUsername());
        if(user == null){
            throw new RuntimeException("존재하지 않은 유저입니다."); //예외 타입 변경해야됨
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

    @Transactional
    @Override
    public Boolean signOut() {
        return null;
    }


}
