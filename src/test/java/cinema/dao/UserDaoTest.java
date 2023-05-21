package cinema.dao;

import java.util.Optional;
import java.util.Set;
import cinema.dao.impl.RoleDaoImpl;
import cinema.dao.impl.UserDaoImpl;
import cinema.exception.DataProcessingException;
import cinema.model.Role;
import cinema.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDaoTest extends AbstractTest {
    private static final String EMAIL = "bob@gmail.com";
    private static final String PASSWORD = "12345678";

    private UserDao userDao;
    private Role userRole;

    @Override
    protected Class<?>[] entities() {
        return new Class[] {User.class, Role.class};
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(getSessionFactory());
        RoleDao roleDao = new RoleDaoImpl(getSessionFactory());
        userRole = roleDao.add(new Role(Role.RoleName.USER));
    }

    @Test
    void save_successSave_ok() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(userRole));

        User saved = userDao.add(user);
        Assertions.assertNotNull(saved, "There was no one user added to DB");
        Assertions.assertEquals(1L, saved.getId(), "ID doesn't equal");
        Assertions.assertEquals(EMAIL, saved.getEmail(), "Email doesn't equal");
        Assertions.assertEquals(PASSWORD, saved.getPassword(), "Password doesn't equal");
        Assertions.assertTrue(saved.getRoles().contains(userRole), "Role doesn't contain");
    }

    @Test
    void save_emailPresentInDb_notOk() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(userRole));
        userDao.add(user);

        Assertions.assertThrows(DataProcessingException.class, () -> userDao.add(user),
                "Method should throw DataProcessingException when user with email "
                        + EMAIL + " exists in DB");
    }

    @Test
    void save_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> userDao.add(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void findByEmail_successFind_ok() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(userRole));
        userDao.add(user);

        Optional<User> userByEmail = userDao.findByEmail(EMAIL);
        if (userByEmail.isEmpty()) {
            Assertions.fail("DB should has role with name " + user.getEmail());
        }
        Assertions.assertEquals(1L, user.getId(), "ID doesn't equal");
        Assertions.assertEquals(EMAIL, user.getEmail(), "Email doesn't equal");
        Assertions.assertEquals(PASSWORD, user.getPassword(), "Password doesn't equal");
        Assertions.assertTrue(user.getRoles().contains(userRole), "Role doesn't contain");
    }

    @Test
    void findByEmail_nullValue_notOk() {
        Optional<User> userByEmail = userDao.findByEmail(null);
        Assertions.assertEquals(userByEmail, Optional.empty(),
                "Method should return Optional.empty()");
    }

    @Test
    void findByEmail_noRolePresentInDB_notOk() {
        Optional<User> userByEmail = userDao.findByEmail("alice@gmail.com");
        Assertions.assertEquals(userByEmail, Optional.empty(),
                "Method should return Optional.empty()");
    }

    @Test
    void get_successFindById_ok() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(userRole));
        userDao.add(user);
        Optional<User> userById = userDao.get(1L);
        if (userById.isEmpty()) {
            Assertions.fail("No one user present in DB. You have some problem with save() method");
        }
        Assertions.assertEquals(userById.get().getId(), 1L,
                "IDs don't equal. Actual id = " + userById.get().getId() + " but must be 1");
    }

    @Test
    void get_noUserPresentInDbById_notOk() {
        Optional<User> userById = userDao.get(1L);
        Assertions.assertEquals(userById, Optional.empty(),
                "Method should return Optional.empty()");
    }

    @Test
    void get_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> userDao.get(null),
                "Method should throw DataProcessingException for null value");
    }
}