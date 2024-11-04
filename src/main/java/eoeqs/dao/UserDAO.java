package eoeqs.dao;

import eoeqs.model.User;
import eoeqs.security.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserDAO {

    @PersistenceContext
    private final EntityManager entityManager;

    public Optional<User> findByUsername(String username) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root).where(builder.equal(root.get("username"), username));
        User user = entityManager.createQuery(query).uniqueResult();
        return Optional.ofNullable(user);
    }

    public Optional<User> findById(Long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root).where(builder.equal(root.get("id"), id));
        User user = entityManager.createQuery(query).uniqueResult();
        return Optional.ofNullable(user);
    }

    public boolean existsByUsername(String username) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<User> root = query.from(User.class);
        query.select(builder.count(root)).where(builder.equal(root.get("username"), username));
        Long count = entityManager.createQuery(query).uniqueResult();
        return count != null && count > 0;
    }

    public void addUser(User user) {
        entityManager.persist(user);
    }

    public long countAdmins() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        Join<User, Role> roles = root.join("roles");
        query.select(builder.count(root))
                .where(builder.equal(roles, Role.ADMIN));

        return entityManager.createQuery(query).getSingleResult();
    }
}