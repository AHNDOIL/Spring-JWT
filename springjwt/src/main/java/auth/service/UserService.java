package auth.service;

import auth.dto.SignUpDto;
import auth.dto.TokenDto;
import auth.entity.UserEntity;

import java.util.Collection;

public interface UserService { // 회원가입

    TokenDto create(SignUpDto signUpDto);
    UserEntity read();
    Collection<UserEntity> readAll();
    Boolean update();
    Boolean delete();

    UserEntity convertSignUpDtoToUser(SignUpDto signUpDto);

}
