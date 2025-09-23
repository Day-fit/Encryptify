package pl.dayfit.encryptifyauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<EncryptifyUser, UUID> {
    Optional<EncryptifyUser> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmailHashLookup(String lookup);
    Optional<List<EncryptifyUser>> findAllByEmailHashLookup(String lookup);

    void deleteByUsername(String username);
}