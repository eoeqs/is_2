package eoeqs.repository;

import eoeqs.model.RoleChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleChangeRequestRepository extends JpaRepository<RoleChangeRequest, Long> {
    List<RoleChangeRequest> findAllByStatus(RoleChangeRequest.RequestStatus status);
}