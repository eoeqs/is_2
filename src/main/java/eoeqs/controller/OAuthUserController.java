package eoeqs.controller;

import eoeqs.dto.UserInfoDto;
import eoeqs.exception.InternalServerErrorException;
import eoeqs.exception.UnauthorizedAccessException;
import eoeqs.model.OAuthUser;
import eoeqs.model.RoleChangeRequest;
import eoeqs.model.Roles;
import eoeqs.repository.OAuthUserRepository;
import eoeqs.service.OAuthUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class OAuthUserController {


    private final OAuthUserService oAuthUserService;
    private final OAuthUserRepository oAuthUserRepository;
    private static final Logger logger = LoggerFactory.getLogger(OAuthUserController.class);

    public OAuthUserController(OAuthUserService oAuthUserService, OAuthUserRepository oAuthUserRepository) {
        this.oAuthUserService = oAuthUserService;
        this.oAuthUserRepository = oAuthUserRepository;
    }

    @GetMapping("/current-user-info")
    public ResponseEntity<UserInfoDto> getCurrentUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }

        String username = authentication.getName();
        OAuthUser oAuthUser = oAuthUserRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("User not found in the database"));

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");

        UserInfoDto userInfo = new UserInfoDto(oAuthUser.getId(), role);
        return ResponseEntity.ok(userInfo);

    }


    @GetMapping("/{id}")
    public Optional<OAuthUser> getUserById(@PathVariable Long id) {
        return oAuthUserService.findById(id);
    }


    @GetMapping("/get-users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(oAuthUserService.findAllUsers());
    }

    @PostMapping("/{id}/role-request")
    public ResponseEntity<?> requestRoleChange(Authentication authentication, @PathVariable Long id, @RequestBody Map<String, String> role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Requesting role change for user: {}", authentication.getName());
        try {
            Roles requestedRole = Roles.valueOf(role.get("role"));
            oAuthUserService.requestRoleChange(id, requestedRole);
            return ResponseEntity.ok("Role change request submitted successfully");
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong during requesting a new role.");
        }
    }


    @GetMapping("/role-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleChangeRequest>> getRoleChangeRequests(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Getting role change requests for user: {}", authentication.getName());
        List<RoleChangeRequest> requests = oAuthUserService.getRoleChangeRequests();
        return ResponseEntity.ok(requests);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role-requests/{id}/approve")
    public ResponseEntity<?> approveRoleChange(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Approving role change for user: {}", authentication.getName());
        try {
            oAuthUserService.approveRoleChange(id);
            return ResponseEntity.ok("Role change request approved");
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong during approving request.");
        }
    }

    @PostMapping("/role-requests/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectRoleChange(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Rejecting role change for user: {}", authentication.getName());
        try {
            oAuthUserService.rejectRoleChange(id);
            return ResponseEntity.ok("Role change request rejected");
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong during rejecting request.");
        }
    }
}
