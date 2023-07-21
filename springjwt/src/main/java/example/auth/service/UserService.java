package example.auth.service;

import example.auth.dto.SignUpDto;
import example.auth.dto.TokenDto;
import example.auth.entity.UserEntity;

import java.util.Collection;

public interface UserService { // 회원가입

    TokenDto create(SignUpDto signUpDto);
    UserEntity read();
    Collection<UserEntity> readAll();
    Boolean update();
    Boolean delete();

    UserEntity convertSignUpDtoToUser(SignUpDto signUpDto);

}
