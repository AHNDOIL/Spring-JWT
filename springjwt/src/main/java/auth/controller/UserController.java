package auth.controller;


import auth.dto.SignUpDto;
import auth.dto.TokenDto;
import auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    /**
     * 회원 가입을 처리하는 메서드
     * @param signUpDto 회원 가입에 필요한 정보를 담은 SignUpDto 객체
     * @return 회원 가입 결과에 대한 TokenDto 객체
     */
    @PostMapping("/sign-up")
    public ResponseEntity<TokenDto> signUp(@RequestBody SignUpDto signUpDto){
        TokenDto tokenDto = userService.create(signUpDto);

        return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
    }


}