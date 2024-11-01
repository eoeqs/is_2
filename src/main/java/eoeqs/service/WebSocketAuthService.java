package eoeqs.service;

import eoeqs.model.User;
import eoeqs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class WebSocketAuthService {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    public Authentication authenticateUser(StompHeaderAccessor accessor) {
        String username = accessor.getFirstNativeHeader("username");
        String password = accessor.getFirstNativeHeader("password");

        if (username != null && password != null) {
            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (user != null && user.getPassword().equals(password)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                return auth;
            }
        }
        return null;
    }
}
