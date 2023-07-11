package auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


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
    private Long userId;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @JsonIgnore //직렬화시 해당 필드 무시
    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @JsonIgnore
    @Column(name = "activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<AuthorityEntity> authorities;



    public UserEntity(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities.stream()
                .filter(authority -> authority instanceof AuthorityEntity)
                .map(authority -> (AuthorityEntity) authority)
                .collect(Collectors.toSet());
    }

}