package auth.repository;


import auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    //CrudRepository를 상속받은 Jpa 특화 JpaRepository

    UserEntity findByUsername(String username);
    UserEntity findByNickname(String nickname);
}