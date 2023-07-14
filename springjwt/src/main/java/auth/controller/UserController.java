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

    @PostMapping("/sign-up")
    public ResponseEntity<TokenDto> signUp(@RequestBody SignUpDto signUpDto){
        TokenDto tokenDto = userService.create(signUpDto);

        return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
    }


}