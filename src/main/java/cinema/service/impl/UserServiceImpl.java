package cinema.service.impl;

import cinema.dao.UserDao;
import cinema.model.User;
import cinema.service.UserService;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final PasswordEncoder encoder;
    private final UserDao userDao;

    public UserServiceImpl(PasswordEncoder encoder, UserDao userDao) {
        this.encoder = encoder;
        this.userDao = userDao;
    }

    @Override
    public User add(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userDao.add(user);
    }

    @Override
    public User get(Long id) {
        logger.info("Get user method was called. Params: userId = {}", id);
        return userDao.get(id).orElseThrow(() -> {
            logger.warn("Can't find user with id {}", id);
            return new RuntimeException("User with id " + id + " not found");
        });
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.info("Find user by email method was called. Params: email = {}", email);
        return userDao.findByEmail(email);
    }
}
