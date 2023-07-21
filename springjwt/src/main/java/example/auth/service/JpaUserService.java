package example.auth.service;


import example.auth.dto.SignUpDto;
import example.auth.dto.TokenDto;
import example.auth.entity.Authority;
import example.auth.entity.UserEntity;
import example.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import example.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class JpaUserService implements UserService{

    private final Logger logger = LoggerFactory.getLogger(JpaUserService.class);
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;


    /**
     * SignUpDto를 기반으로 회원을 생성하고, 토큰을 발급하는 메서드
     * @param signUpDto 회원 가입에 필요한 정보를 담은 SignUpDto 객체
     * @return 토큰에 대한 TokenDto 객체
     * @throws  RuntimeException 이미 존재하는 ID 또는 닉네임일 경우 발생하는 예외 //예외 타입 변경해야됨
     */
    @Transactional
    @Override
    public TokenDto create(SignUpDto signUpDto) { //password 보안 정책 추가

        if(userRepository.findByUsername(signUpDto.getUsername()) != null){
            throw new RuntimeException("이미 존재하는 ID 입니다."); //예외 변경해야됨
        }
        if(userRepository.findByNickname(signUpDto.getNickname()) != null){
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }


        Set<Authority> authorities = EnumSet.of(Authority.ROLE_USER);

        UserEntity user = convertSignUpDtoToUser(signUpDto);
        UserEntity encodeUser = passwordEncryption(user);
        encodeUser.setAuthorities(authorities);


        userRepository.save(encodeUser);

        String accessToken = tokenProvider.createAccessToken(encodeUser.getUsername());
        String refreshToken = tokenProvider.createRefreshToken(encodeUser.getUsername());

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        refreshTokenService.saveRefreshToken(tokenDto, encodeUser.getUsername());

        return tokenDto;

    }

    @Override
    public UserEntity read() {
        return null;
    }

    @Override
    public Collection<UserEntity> readAll() {
        return null;
    }

    @Override
    public Boolean update() {
        return null;
    }

    @Override
    public Boolean delete() {
        return null;
    }


    /**
     * SignUpDto를 UserEntity로 변환하는 메서드
     * @param signUpDto 변환할 SignUpDto 객체
     * @return 변환된 UserEntity 객체
     */
    @Override
    public UserEntity convertSignUpDtoToUser(SignUpDto signUpDto) {
        return UserEntity.builder()
                .username(signUpDto.getUsername())
                .password(signUpDto.getPassword())
                .nickname(signUpDto.getNickname())
                .build();
    }


    /**
     * 사용자의 비밀번호를 암호화하는 메서드
     * @param user 암호화할 UserEntity 객체
     * @return 암호화된 UserEntity 객체
     */
    public UserEntity passwordEncryption(UserEntity user){
        return UserEntity.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .nickname(user.getNickname())
                .build();
    }
}