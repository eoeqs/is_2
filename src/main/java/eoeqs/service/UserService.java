package eoeqs.service;

import eoeqs.dao.AdminDAO;
import eoeqs.dao.UserDAO;
import eoeqs.model.User;
import eoeqs.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {
    private final UserDAO userDao;
    private final AdminDAO adminDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
    }

    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }

    public boolean addUser(User user) {
        if (user.getRoles().contains(Role.ADMIN) && userDao.countAdmins() > 0) {
            adminDAO.addApplication(user);
            return false;
        } else {
            userDao.addUser(user);
            return true;
        }
    }
}