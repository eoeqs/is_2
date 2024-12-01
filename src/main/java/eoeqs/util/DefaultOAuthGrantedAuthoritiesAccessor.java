package eoeqs.util;


import eoeqs.model.OAuthUser;
import eoeqs.model.Role;
import eoeqs.repository.OAuthUserRepository;
import eoeqs.repository.RoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class DefaultOAuthGrantedAuthoritiesAccessor implements OAuthGrantedAuthoritiesAccessor {

    private final RoleRepository roleRepository;
    private final OAuthUserRepository oAuthUserRepository;

    public DefaultOAuthGrantedAuthoritiesAccessor(RoleRepository roleRepository, OAuthUserRepository oAuthUserRepository) {
        this.roleRepository = roleRepository;
        this.oAuthUserRepository = oAuthUserRepository;
    }

    @Transactional
    public Collection<GrantedAuthority> access(String username, String provider) {
        Optional<OAuthUser> user = oAuthUserRepository.findByUsernameAndProvider(username, provider);
        if (user.isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Failed to find default role: ROLE_USER in the database"));
            // todo: replace with representative exception

            OAuthUser newUser = new OAuthUser(username, provider, Set.of(defaultRole));
            oAuthUserRepository.save(newUser);
            return newUser.getRoles().stream()
                    .map(Role::toString)
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
        }
        return user.get().getRoles().stream()
                .map(Role::toString)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
