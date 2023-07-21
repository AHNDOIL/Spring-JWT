package example.jwt;

import example.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import example.security.JpaUserDetailsService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

//추가된 라이브러리를 사용해서 JWT를 생성하고 검증하는 컴포넌트
@Component
public class TokenProvider{

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final String secretKey;
    private final long tokenExpirationTime; //토큰 생존 시간 millis
    private final String issuer; //토큰 발급자

    private final JpaUserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;


    public TokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.token-expiration-time}") long tokenExpirationTime,
            @Value("${jwt.issuer}") String issuer,
            RefreshTokenRepository refreshTokenRepository,
            JpaUserDetailsService userDetailsService) {
        this.secretKey = secretKey;
        this.tokenExpirationTime = tokenExpirationTime;
        this.issuer = issuer;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
    }


    /**
     * AccessToken을 발급하는 메서드
     * @param username AccessToken을 발급받을 사용자의 ID
     * @return 생성된 AccessToken 문자열
     */
    public String createAccessToken(String username){
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+tokenExpirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes()) //알고리즘, secret값 설정
                .compact();
    }

    /**
     * HttpServletRequest 에서 Authorization 헤더를 추출하여 토큰을 해결하는 메서드
     * @param request HttpServletRequest 객체
     * @return 추출된 토큰 문자열
     */
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization"); //http에서 "Authorization" : BEARER 형태로 통신
    }


    /**
     * 토큰을 사용하여 클레임을 생성하고, 이를 기반으로 사용자 객체를 만들어 인증(Authentication) 객체를 반환하는 메서드
     * @param token 토큰 문자열
     * @return 인증(Authentication) 객체
     */
    public Authentication getAuthentication(String token) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }

    /**
     * 주어진 토큰에서 사용자 이름을 추출하는 메서드
     * @param token 추출할 사용자 이름이 포함된 토큰 문자열
     * @return 추출된 사용자 이름
     */
    public String getUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 주어진 사용자 이름을 기반으로 RefreshToken을 생성하는 메서드
     * @param username RefreshToken을 발급받을 사용자의 이름
     * @return 생성된 Refresh Token 문자열
     */
    public String createRefreshToken(String username){
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(14, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }

    /**
     * 주어진 RefreshToken을 사용하여 AccessToken을 재발급하는 메서드
     * @param refreshToken  AccessToken 재발급에 사용될 RefreshToken
     * @return 재발급된 AccessToken 문자열
     * @throws IllegalArgumentException 잘못된 토큰인 경우 발생하는 예외
     */
    public String reCreateAccessToken(String refreshToken){

        String username = getUsername(refreshToken);
        String existRefreshToken = refreshTokenRepository.findByUsername(username).get().getRefreshToken();

        if(!refreshToken.equals(existRefreshToken)){
            logger.info("유효하지 않은 토큰입니다.");
            throw new IllegalArgumentException("유효하지 않은 토큰");
        }
        return createAccessToken(username);
    }


    /**
     * 주어진 토큰의 유효성을 검사하는 메서드
     * @param token 검사할 토큰 문자열
     * @return 토큰의 유효성 여부 (유효한 경우 true, 그렇지 않은 경우 false)
     */
    public boolean validateToken(String token) {
        try {
            if (!token.startsWith("Bearer ")) {
                return false;
            }
            token = token.substring("Bearer ".length()).trim();

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (SignatureException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
        }
        return false;
    }
}