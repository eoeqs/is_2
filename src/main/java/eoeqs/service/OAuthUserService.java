package eoeqs.service;

import eoeqs.model.OAuthUser;
import eoeqs.model.Role;
import eoeqs.model.RoleChangeRequest;
import eoeqs.model.Roles;
import eoeqs.repository.OAuthUserRepository;
import eoeqs.repository.RoleChangeRequestRepository;
import eoeqs.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OAuthUserService {

    private final RoleChangeRequestRepository roleChangeRequestRepository;
    private final OAuthUserRepository oAuthUserRepository;
    private final RoleRepository roleRepository;

    public OAuthUserService(RoleChangeRequestRepository roleChangeRequestRepository, OAuthUserRepository oAuthUserRepository, RoleRepository roleRepository) {
        this.roleChangeRequestRepository = roleChangeRequestRepository;
        this.oAuthUserRepository = oAuthUserRepository;
        this.roleRepository = roleRepository;
    }


    public List<OAuthUser> findAllUsers() {
        return oAuthUserRepository.findAll();
    }

    public Optional<OAuthUser> findById(Long id) {
        return oAuthUserRepository.findById(id);
    }

    public RoleChangeRequest requestRoleChange(Long userId, Roles requestedRole) {
        OAuthUser user = oAuthUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        RoleChangeRequest roleChangeRequest = new RoleChangeRequest();
        roleChangeRequest.setOAuthUser(user);
        roleChangeRequest.setRequestedRole(requestedRole);
        roleChangeRequest.setStatus(RoleChangeRequest.RequestStatus.PENDING);

        return roleChangeRequestRepository.save(roleChangeRequest);
    }


    public List<RoleChangeRequest> getRoleChangeRequests() {
        return roleChangeRequestRepository.findAllByStatus(RoleChangeRequest.RequestStatus.PENDING);
    }

    public void approveRoleChange(Long requestId) {
        RoleChangeRequest request = roleChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with id: " + requestId));

        if (request.getStatus() == RoleChangeRequest.RequestStatus.APPROVED) {
            throw new IllegalStateException("Role change request has already been approved.");
        }

        if (request.getStatus() == RoleChangeRequest.RequestStatus.REJECTED) {
            throw new IllegalStateException("Role change request has already been rejected.");
        }

        OAuthUser user = request.getOAuthUser();

        Roles requestedRole = request.getRequestedRole();

        user.getRoles().clear();
        Role newRole = roleRepository.findByName(requestedRole.name())
                .orElseThrow(() -> new RuntimeException("Role not found: " + requestedRole));

        user.getRoles().add(newRole);
        oAuthUserRepository.save(user);

        request.setStatus(RoleChangeRequest.RequestStatus.APPROVED);
        roleChangeRequestRepository.save(request);
    }

    public void rejectRoleChange(Long requestId) {
        RoleChangeRequest request = roleChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with id: " + requestId));

        request.setStatus(RoleChangeRequest.RequestStatus.REJECTED);
        roleChangeRequestRepository.save(request);
    }
}
