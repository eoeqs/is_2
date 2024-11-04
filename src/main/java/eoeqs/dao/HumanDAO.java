package eoeqs.dao;

import eoeqs.model.Human;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HumanDAO extends JpaRepository<Human, Long> {
}