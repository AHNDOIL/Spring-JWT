package example.config;

import example.jwt.JwtAccessDeniedHandler;
import example.jwt.JwtAuthenticationEntryPoint;
import example.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenProvider tokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                    .authorizeHttpRequests()
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()

                .and()
                    .exceptionHandling()//예외 처리
                    .accessDeniedHandler(jwtAccessDeniedHandler) //권한 문제 발생
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint); //인증 문제 발생


        httpSecurity.apply(new JwtSecurityConfig(tokenProvider));

        return httpSecurity.build();
    }

}