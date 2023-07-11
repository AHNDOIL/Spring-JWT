package security;

import auth.entity.UserEntity;
import auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    // UserDetailsService 인터페이스를 구현한 클래스는 사용자 정보를 데이터베이스나
    // 외부 시스템에서 가져와서 UserDetails 객체로 변환하는 로직을 구현

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserEntity user = userRepository.findByUsername(username);

        if(user != null){
            return new CustomUserDetails(user);
        } else {
            return (UserDetails) new UsernameNotFoundException("유저를 찾을수 없습니다 " + username);
        }
    }

}
