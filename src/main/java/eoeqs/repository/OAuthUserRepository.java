package eoeqs.repository;

import eoeqs.model.OAuthUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

    @Query("SELECT a FROM OAuthUser a LEFT JOIN FETCH a.roles WHERE a.username = :username")
    Optional<OAuthUser> findByUsernameWithRoles(@Param("username") String username);
    @EntityGraph(attributePaths = {"roles"})
    Optional<OAuthUser> findByUsernameAndProvider(String username, String provider);
    @EntityGraph(attributePaths = {"roles"})
    Optional<OAuthUser> findByUsername(String username);


}

