package eoeqs.controller;

import eoeqs.model.User;
import eoeqs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("Received registration request for username: {}", user.getUsername());
        logger.info("received pwd {}", user.getPassword());
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            logger.error("Password is missing or empty for username: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            User createdUser = userService.registerUser(user.getUsername(), user.getPassword());
            logger.info("User {} created successfully", createdUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("Error while creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        logger.info("Received login attempt for username: {}", user.getUsername());

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            logger.error("Password is missing for login attempt by username: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is missing");
        }

        Optional<User> loggedInUser = userService.authenticate(user.getUsername(), user.getPassword());

        if (loggedInUser.isPresent()) {
            logger.info("User {} logged in successfully", user.getUsername());
            return ResponseEntity.ok("Login successful");
        } else {
            logger.warn("Failed login attempt for username: {}", user.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
    }
}
