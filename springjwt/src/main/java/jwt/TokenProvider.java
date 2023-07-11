package jwt;

import auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import security.JpaUserDetailsService;



import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

//추가된 라이브러리를 사용해서 JWT를 생성하고 검증하는 컴포넌트
@Component
public class TokenProvider{

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String secretKey;
    private final long tokenExpirationTime; //토큰 생존 시간 millis
    private final String issuer;

    private final JpaUserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.token-expiration-time}") long tokenExpirationTime,
            @Value("${issuer}") String issuer,
            RefreshTokenRepository refreshTokenRepository,
            JpaUserDetailsService userDetailsService) {
        this.secretKey = secretKey;
        this.tokenExpirationTime = tokenExpirationTime;
        this.issuer = issuer;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
    }


    public String createAccessToken(String username){
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+tokenExpirationTime))
                .signWith(SignatureAlgorithm.ES512, secretKey.getBytes()) //알고리즘, secret값 설정
                .compact();
    }


    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization"); //http에서 "Authorization" : BEARER 형태로 통신
    }


    // 토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//        Claims claims = Jwts
//                .parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
//
//        UserEntity principal = new UserEntity(claims.getSubject(), "", authorities);
//
//        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String getUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public String createRefreshToken(String username){ //refreshToken 발급
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(14, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.ES512, secretKey.getBytes())
                .compact();
    }

    public String reCreateAccessToken(String refreshToken){ //accessToken 재발급

        String username = getUsername(refreshToken);
        String existRefreshToken = refreshTokenRepository.findByUsername(username).get().getRefreshToken();

        if(!refreshToken.equals(existRefreshToken)){
            logger.info("유효하지 않은 토큰입니다.");
            throw new IllegalArgumentException("유효하지 않은 토큰");
        }
        return createAccessToken(username);
    }

    // 토큰의 유효성 검증을 수행
    public boolean validateToken(String token) {
        try {

            if(!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")){
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {

            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {

            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {

            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {

            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}