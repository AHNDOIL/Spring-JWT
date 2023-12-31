package example.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;

import java.util.Set;


@Entity // DB의 테이블과 1:1 매핑되는 객체
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {


    @JsonIgnore
    @Id // primary key
    @Column(name = "user_id")
    // 자동 증가 되는
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @JsonIgnore //직렬화시 해당 필드 무시
    @Column(name = "password", length = 100,  nullable = false)
    private String password;

    @Column(name = "nickname", length = 50,  nullable = false)
    private String nickname;

    @JsonIgnore
    @Column(name = "activated")
    private boolean activated;


    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Authority.class)
    @Column(name = "authority")
    private Set<Authority> authorities;
}