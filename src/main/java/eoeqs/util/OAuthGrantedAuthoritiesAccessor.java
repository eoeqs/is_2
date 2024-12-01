package eoeqs.util;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface OAuthGrantedAuthoritiesAccessor {

    Collection<GrantedAuthority> access(String username, String provider);
}
