package auth.service;

import auth.dto.SignInDto;
import auth.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaAuthService implements AuthService{


    @Override
    public TokenDto signIn(SignInDto signInDto) {
        return null;
    }

    @Override
    public Boolean signOut() {
        return null;
    }


}
