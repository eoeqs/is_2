package eoeqs.repository;

import eoeqs.model.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

    Optional<OAuthUser> findByUsernameAndProvider(String username, String provider);
    Optional<OAuthUser> findByUsername(String username);


}

