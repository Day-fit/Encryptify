package pl.dayfit.encryptifydata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dayfit.encryptifydata.entity.EncryptifyUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<EncryptifyUser, Long> {
    Optional<EncryptifyUser> findByUsername(String username);

    Optional<EncryptifyUser> findById(long id);
    
    @Query("SELECT u FROM EncryptifyUser u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<EncryptifyUser> findByIdentifier(String identifier);
}