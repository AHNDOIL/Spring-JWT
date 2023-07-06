package entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "authority")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityEntity{

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;
}