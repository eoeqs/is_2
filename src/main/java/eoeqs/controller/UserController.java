package eoeqs.controller;

import eoeqs.dto.AuthenticationSucceedDto;
import eoeqs.dto.LoginUserDto;
import eoeqs.dto.RegisterUserDto;
import eoeqs.dto.UserInfoDto;
import eoeqs.jwt.JwtService;
import eoeqs.model.Role;
import eoeqs.model.RoleChangeRequest;
import eoeqs.model.User;
import eoeqs.service.AuthenticationService;
import eoeqs.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    public UserController( UserService userService, JwtService jwtService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userService.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        throw new UsernameNotFoundException("User not authenticated");
    }
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterUserDto user) {

        if (user.password() == null || user.password().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            User createdUser = authenticationService.signup(user);
            String jwtToken = jwtService.generateToken(createdUser);
            AuthenticationSucceedDto succeedDto = new AuthenticationSucceedDto(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(succeedDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationSucceedDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        AuthenticationSucceedDto authenticationSucceedDto = new AuthenticationSucceedDto(jwtToken, jwtService.getExpirationTime());

        return ResponseEntity.ok(authenticationSucceedDto);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/get-users")
    public ResponseEntity<?> getAllUsers() {
        User user = getAuthenticatedUser();
        logger.info("Getting users for user: {}", user.getUsername());

        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/current-user-info")
    public ResponseEntity<UserInfoDto> getCurrentUserInfo() {
        User user = getAuthenticatedUser();
        logger.info("Fetching ID and role for authenticated user: {}", user.getUsername());

        String role = user.getPrimaryRole().stream()
                .findFirst()
                .map(Enum::name)
                .orElse("USER");

        UserInfoDto userInfo = new UserInfoDto(user.getId(), role);
        return ResponseEntity.ok(userInfo);
    }
    @PostMapping("/{id}/role-request")
    public ResponseEntity<?> requestRoleChange(@PathVariable Long id, @RequestBody Map<String, String> role) {
        User user = getAuthenticatedUser();
        logger.info("Requesting role change for user: {}", user.getUsername());
        try {
            Role requestedRole = Role.valueOf(role.get("role"));
            userService.requestRoleChange(id, requestedRole);
            return ResponseEntity.ok("Role change request submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting role change request");
        }
    }

    @GetMapping("/role-requests")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RoleChangeRequest>> getRoleChangeRequests() {
        User user = getAuthenticatedUser();
        logger.info("Getting role change requests for user: {}", user.getUsername());
        List<RoleChangeRequest> requests = userService.getRoleChangeRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/role-requests/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> approveRoleChange(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        logger.info("Approving role change for user: {}", user.getUsername());
        try {
            userService.approveRoleChange(id);
            return ResponseEntity.ok("Role change request approved");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving role change request");
        }
    }


    @PostMapping("/role-requests/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectRoleChange(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        logger.info("Rejecting role change for user: {}", user.getUsername());
        try {
            userService.rejectRoleChange(id);
            return ResponseEntity.ok("Role change request rejected");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error rejecting role change request");
        }
    }

}
