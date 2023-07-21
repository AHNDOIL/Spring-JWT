package example.auth.service;

import example.auth.dto.SignInDto;
import example.auth.dto.TokenDto;

public interface AuthService { //로그인, 로그아웃

    TokenDto signIn(SignInDto signInDto);
    Boolean signOut();
}
