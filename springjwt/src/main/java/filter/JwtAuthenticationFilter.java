package filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


//실제로 이 컴포넌트를 이용하는 것은 인증 작업을 진행하는 Filter
//이 필터는 검증이 끝난 JWT로부터 유저정보를 받아와서 UsernamePasswordAuthenticationFilter 로 전달
@Order(0)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //각 요청당 한번만 실행하기를 보장

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final TokenProvider tokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{
        //요청이 들어올 때마다 호출되며, JWT 토큰을 추출하고 유효성을 검사한 후, 인증 정보를 설정
        // FilterChain을 통해 다음 필터로 요청을 전달
        String token = tokenProvider.resolveToken(request); //token 가져오기

        if(token != null && tokenProvider.validateToken(token)){
            token = token.substring(7); //Bearer 제외
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

}