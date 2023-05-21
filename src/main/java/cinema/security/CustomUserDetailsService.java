package cinema.security;

import cinema.model.User;
import cinema.service.UserService;
import java.util.Optional;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userService.findByEmail(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserBuilder builder =
                    org.springframework.security.core.userdetails.User.withUsername(username);
            builder.password(user.getPassword());
            builder.authorities(user.getRoles().stream()
                    .map(r -> r.getRoleName().toString())
                    .toArray(String[]::new));
            return builder.build();
        }
        throw new UsernameNotFoundException("User with email" + username + " not found");
    }
}
