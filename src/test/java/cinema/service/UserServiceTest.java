package cinema.service;

import java.util.Optional;
import java.util.Set;
import cinema.dao.UserDao;
import cinema.model.Role;
import cinema.model.User;
import cinema.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(passwordEncoder, userDao);
    }

    @Test
    void add_successAdd_ok() {
        Role roleUser = new Role(1L, Role.RoleName.USER);
        User requestUser = new User();
        requestUser.setEmail("bob@gmail.com");
        requestUser.setPassword("password");
        requestUser.setRoles(Set.of(roleUser));

        User expected = new User();
        expected.setId(1L);
        expected.setEmail("bob@gmail.com");
        expected.setPassword("difficultPassword");
        expected.setRoles(Set.of(roleUser));

        Mockito.when(userDao.add(requestUser)).thenReturn(expected);
        Mockito.when(passwordEncoder.encode(requestUser.getPassword()))
                .thenReturn("difficultPassword");
        User actual = userService.add(requestUser);
        Assertions.assertNotNull(actual,"User must be not null");
        Assertions.assertEquals(actual.getId(), expected.getId(),
                "IDs don't equals. Actual is " + actual.getId() + ". Expected "
                        + expected.getId());
        Assertions.assertEquals(actual.getEmail(), expected.getEmail(),
                "Emails don't equals. Actual is " + actual.getEmail()
                        + ". Expected " + expected.getEmail());
        Assertions.assertEquals(actual.getPassword(), expected.getPassword(),
                "Passwords don't equals Actual is " + actual.getPassword()
                        + ". Expected " + expected.getPassword());
        Assertions.assertTrue(actual.getRoles().contains(roleUser),"User doesn't have role USER");
    }

    @Test
    void get_successFind_ok() {
        Role userRole = new Role(1L, Role.RoleName.USER);
        User expected = new User();
        expected.setId(1L);
        expected.setEmail("bob@gmail.com");
        expected.setPassword("difficultPassword");
        expected.setRoles(Set.of(userRole));

        Mockito.when(userDao.get(1L)).thenReturn(Optional.of(expected));

        User userById = userService.get(1L);
        Assertions.assertNotNull(userById, "User must be not null");
        Assertions.assertEquals(userById.getId(), expected.getId(),
                "IDs don't equals. Actual is " + userById.getId() + ". Expected "
                        + expected.getId());
        Assertions.assertEquals(userById.getEmail(), expected.getEmail(),
                "Emails don't equals. Actual is " + userById.getEmail()
                        + ". Expected " + expected.getEmail());
        Assertions.assertEquals(userById.getPassword(), expected.getPassword(),
                "Passwords don't equals Actual is " + userById.getPassword()
                        + ". Expected " + expected.getPassword());
        Assertions.assertTrue(userById.getRoles().contains(userRole),"User doesn't have role USER");
    }

    @Test
    void get_noExistUser_ok() {
        Mockito.when(userDao.get(1L)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> userDao.get(1L),
                "Method should throws exception when no one user exists in DB");
    }

    @Test
    void findByEmail_successFind_ok() {
        User expected = new User();
        expected.setId(1L);
        expected.setEmail("bob@gmail.com");
        expected.setPassword("password");
        expected.setRoles(Set.of(new Role(1L, Role.RoleName.USER)));

        Mockito.when(userDao.findByEmail("bob@gmail.com")).thenReturn(Optional.of(expected));

        Optional<User> userByEmail = userService.findByEmail("bob@gmail.com");
        Assertions.assertNotNull(userByEmail, "User must be not null");
        Assertions.assertEquals(userByEmail, Optional.of(expected), "Optionals don't match. "
                + "Actual is " + userByEmail + ". Expected is " + Optional.of(expected));
    }

    @Test
    void findByEmail_noExistUser_ok() {
        Mockito.when(userDao.findByEmail("bob@gmail.com")).thenReturn(Optional.empty());

        Optional<User> userByEmail = userService.findByEmail("bob@gmail.com");
        Assertions.assertNotNull(userByEmail, "User must be not null");
        Assertions.assertEquals(userByEmail,Optional.empty(), "Optionals don't match. "
                + "Actual is " + userByEmail + ". Expected is " + Optional.empty());
    }

}