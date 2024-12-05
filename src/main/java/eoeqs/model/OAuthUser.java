package eoeqs.model;

import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class OAuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "user_seq")
    private Long id;

    private String username;

    private String provider;

    @ManyToMany
    @JoinTable(name = "oauthuser_roles",
            joinColumns = @JoinColumn(name = "oauthuser_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    public OAuthUser() {}

    public OAuthUser(String username, String provider, Set<Role> roles) {
        this.username = username;
        this.provider = provider;
        this.roles = roles;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
    public String getProvider() {
        return provider;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public Set<Role> getRoles() {
        return roles;
    }

//    OAuthUser user = oauthUserRepository.findByUsernameWithRoles("username_here");
//    Set<Role> roles = user.getRoles();


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OAuthUser oAuthUser = (OAuthUser) o;
        return getId() != null && Objects.equals(getId(), oAuthUser.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
