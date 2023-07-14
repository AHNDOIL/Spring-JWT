package auth.service;


import auth.dto.SignUpDto;
import auth.dto.TokenDto;
import auth.entity.AuthorityEntity;
import auth.entity.UserEntity;
import auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;


@Service
@RequiredArgsConstructor
public class JpaUserService implements UserService{

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    @Override
    public TokenDto create(SignUpDto signUpDto) { //password 보안 정책 추가
        if(userRepository.findByUsername(signUpDto.getUsername()) != null){
            throw new RuntimeException("이미 존재하는 ID 입니다.");
        }
        if(userRepository.findByNickname(signUpDto.getNickname()) != null){
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        AuthorityEntity authority = AuthorityEntity.builder()
                .authorityName("ROLE_USER")
                .build();

        UserEntity user = convertSignUpDtoToUser(signUpDto);
        UserEntity encodeUser = passwordEncryption(user);
        encodeUser.setAuthorities(Collections.singleton(authority));

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

    @Override
    public UserEntity convertSignUpDtoToUser(SignUpDto signUpDto) {
        return UserEntity.builder()
                .username(signUpDto.getUsername())
                .password(signUpDto.getPassword())
                .nickname(signUpDto.getNickname())
                .build();
    }

    public UserEntity passwordEncryption(UserEntity user){
        return UserEntity.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .nickname(user.getNickname())
                .build();
    }
}