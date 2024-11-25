package eoeqs.auth;

import eoeqs.dto.ResponseForUserProfileDto;
import eoeqs.model.Role;
import eoeqs.model.User;
import eoeqs.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class AuthorizationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthorizationSuccessHandlerImpl(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;

        var userInfoFromToken = extractInfoFromToken(auth);
        User savedUser = saveOrUpdateUser(userInfoFromToken);

        var generatedToken = jwtService.generateToken(userInfoFromToken);
        System.out.println("Generated JWT Token: " + generatedToken);

        response.addHeader("Authorization", "Bearer " + generatedToken);
        response.getWriter().write("JWT Token: " + generatedToken);
    }

    private ResponseForUserProfileDto extractInfoFromToken(OAuth2AuthenticationToken token) {
        if ("yandex".equals(token.getAuthorizedClientRegistrationId())) {
            var attributes = token.getPrincipal().getAttributes();
            String userId = (String) attributes.get("id");
            String userName = (String) attributes.get("display_name");
            String userEmail = (String) attributes.get("default_email");
            String userProfilePicture = attributes.containsKey("default_avatar_id")
                    ? "https://avatars.yandex.net/get-yapic/" + attributes.get("default_avatar_id") + "/islands-200"
                    : null;

            return new ResponseForUserProfileDto(
                    userId,
                    userName,
                    userEmail,
                    userProfilePicture);
        } else {
            throw new IllegalArgumentException("Unsupported client ID: " + token.getAuthorizedClientRegistrationId());
        }
    }

    private User saveOrUpdateUser(ResponseForUserProfileDto userInfo) {
        Optional<User> existingUser = userRepository.findByYandexIdOrEmail(userInfo.userId(), userInfo.userEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userInfo.userName());
            user.setEmail(userInfo.userEmail());
            user.setProfileImageUrl(userInfo.userProfilePicture());
            return userRepository.save(user);
        } else {
            User newUser = new User();
            newUser.setYandexId(userInfo.userId());
            newUser.setName(userInfo.userName());
            newUser.setEmail(userInfo.userEmail());
            newUser.setProfileImageUrl(userInfo.userProfilePicture());
            newUser.getRoles().add(Role.USER);
            return userRepository.save(newUser);
        }
    }
}
