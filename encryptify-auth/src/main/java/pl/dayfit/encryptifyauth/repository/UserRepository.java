package pl.dayfit.encryptifyauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<EncryptifyUser, Long> {
    Optional<EncryptifyUser> findByUsername(String username);
    Optional<EncryptifyUser> findById(long id);
    boolean existsByUsername(String username);
    boolean existsByEmailHashLookup(String lookup);
    Optional<List<EncryptifyUser>> findAllByEmailHashLookup(String lookup);
}