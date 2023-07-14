package auth.service;

import auth.dto.SignInDto;
import auth.dto.SignUpDto;
import auth.dto.TokenDto;

public interface AuthService { //로그인, 로그아웃

    TokenDto signIn(SignInDto signInDto);
    Boolean signOut();
}
