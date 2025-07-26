package pl.dayfit.encryptifydata.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifydata.entities.EncryptifyUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<EncryptifyUser, Long> {
    Optional<EncryptifyUser> findByUsername(String username);
}