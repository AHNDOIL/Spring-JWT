package example.auth.controller;


import example.auth.dto.SignUpDto;
import example.auth.dto.TokenDto;
import example.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
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