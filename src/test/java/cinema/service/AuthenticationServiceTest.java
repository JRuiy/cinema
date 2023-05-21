package cinema.service;

import java.util.Set;
import cinema.model.Role;
import cinema.model.User;
import cinema.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private AuthenticationService authenticationService;
    @Mock
    private UserService userService;
    @Mock
    private RoleService roleService;
    @Mock
    private ShoppingCartService shoppingCartService;

    @BeforeEach
    void setUp() {
        authenticationService =
                new AuthenticationServiceImpl(userService, roleService, shoppingCartService);
    }

    @Test
    void register_validValue_ok() {
        Role roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setRoleName(Role.RoleName.USER);

        User user = new User();
        user.setId(1L);
        user.setEmail("bob@gmail.com");
        user.setPassword("12345678");
        user.setRoles(Set.of(roleUser));

        Mockito.when(roleService.getByName("USER")).thenReturn(roleUser);
        Mockito.when(userService.add(Mockito.any())).thenReturn(user);
        User actual = authenticationService.register("bob@gmail.com", "12345678");
        Assertions.assertNotNull(actual, "User must be not null");
        Assertions.assertEquals(user.getEmail(), actual.getEmail(), "Emails don't match");
        Assertions.assertEquals(user.getPassword(), actual.getPassword(), "Password don't match");
        Assertions.assertEquals(actual.getRoles().size(), 1, "Size set of roles don't match");
    }
}