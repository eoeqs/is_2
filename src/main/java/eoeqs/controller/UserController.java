package eoeqs.controller;

import eoeqs.dto.AuthenticationSucceedDto;
import eoeqs.dto.LoginUserDto;
import eoeqs.dto.RegisterUserDto;
import eoeqs.jwt.JwtService;
import eoeqs.jwt.JwtUtils;
import eoeqs.model.Role;
import eoeqs.model.User;
import eoeqs.service.AuthenticationService;
import eoeqs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public UserController(JwtUtils jwtUtils, UserService userService, JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterUserDto user) {

        if (user.password() == null || user.password().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
//            User createdUser = userService.registerUser(user.username(), user.password());
            User createdUser = authenticationService.signup(user);
//            return ResponseEntity.ok(jwtUtils.generateJwtToken(createdUser));
            String jwtToken = jwtService.generateToken(createdUser);
            AuthenticationSucceedDto succeedDto = new AuthenticationSucceedDto(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(succeedDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody User user) {
//
//        if (user.getPassword() == null || user.getPassword().isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is missing");
//        }
//
//        Optional<User> loggedInUser = userService.authenticate(user.getUsername(), user.getPassword());
//
//        if (loggedInUser.isPresent()) {
//            return ResponseEntity.ok(jwtUtils.generateJwtToken(user));
//        } else {
//            return ResponseEntity.status(401).body("Invalid username or password");
//        }
//    }


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

    @PostMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Map<String, String> role) {
        try {
            userService.updateUserRole(id, Role.valueOf(role.get("role")));
            return ResponseEntity.ok("Role updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating role");
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }
}
