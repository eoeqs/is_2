package eoeqs.service;

import eoeqs.model.Role;
import eoeqs.model.RoleChangeRequest;
import eoeqs.model.User;
import eoeqs.repository.RoleChangeRequestRepository;
import eoeqs.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleChangeRequestRepository roleChangeRequestRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleChangeRequestRepository roleChangeRequestRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleChangeRequestRepository = roleChangeRequestRepository;
    }

    public User registerUser(String username, String password) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = new User(username, passwordEncoder.encode(password));
        user.getRoles().add(Role.USER);
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.getRoles().clear();
        user.getRoles().add(newRole);
        userRepository.save(user);
    }
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public RoleChangeRequest requestRoleChange(Long userId, Role requestedRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        RoleChangeRequest roleChangeRequest = new RoleChangeRequest();
        roleChangeRequest.setUser(user);
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

        User user = request.getUser();
        user.getRoles().clear();
        user.getRoles().add(request.getRequestedRole());
        userRepository.save(user);

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
