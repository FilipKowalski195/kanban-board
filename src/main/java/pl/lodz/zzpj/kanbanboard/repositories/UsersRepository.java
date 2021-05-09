package pl.lodz.zzpj.kanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.zzpj.kanbanboard.entity.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    public User findUserByEmail(String email);
    public boolean existsUserByEmail(String email);
}
