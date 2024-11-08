package eoeqs.service;

import eoeqs.model.User;
import eoeqs.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Метод для регистрации пользователя
    public User registerUser(String username, String password) {
        // Проверяем, не существует ли уже пользователь с таким именем
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Создаём нового пользователя и хэшируем его пароль
        User user = new User(username, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    // Поиск пользователя по ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Поиск пользователя по имени
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Удаление пользователя по ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Аутентификация пользователя
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Проверяем, совпадает ли введённый пароль с хэшированным
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);  // Возвращаем пользователя, если пароль совпал
            }
        }
        return Optional.empty();  // Если пользователь не найден или пароль не совпал
    }
}
