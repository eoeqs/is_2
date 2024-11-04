package eoeqs.dao;

import eoeqs.model.Admin;
import eoeqs.model.User;
import eoeqs.security.Role;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class AdminDAO {
    private final SessionFactory sessionFactory;

    public void addApplication(User user) {
        Session session = sessionFactory.getCurrentSession();
        Admin adminApplication = new Admin();
        adminApplication.setUsername(user.getUsername());
        adminApplication.setRoles(Set.of(Role.ADMIN));

        session.persist(adminApplication);        session.flush();
    }

    public List<Admin> getAllApplications() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Admin> query = builder.createQuery(Admin.class);
        Root<Admin> root = query.from(Admin.class);
        query.select(root);

        return session.createQuery(query)
                .getResultList();
    }

    public Optional<Admin> getApplicationById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Admin> query = builder.createQuery(Admin.class);
        Root<Admin> root = query.from(Admin.class);

        query.select(root).where(builder.equal(root.get("id"), id));

        List<Admin> result = session.createQuery(query).getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));

    }

    public void deleteApplication(Admin adminApp) {
        sessionFactory.getCurrentSession().remove(adminApp);
    }
}