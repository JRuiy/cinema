package cinema.dao;

import cinema.dao.impl.RoleDaoImpl;
import cinema.exception.DataProcessingException;
import cinema.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleDaoTest extends AbstractTest {
    private RoleDao roleDao;

    @BeforeEach
    void setUp() {
        roleDao = new RoleDaoImpl(getSessionFactory());
    }

    @Override
    protected Class<?>[] entities() {
        return new Class[] {Role.class};
    }

    @Test
    void add_successADd_ok() {
        Role expected = new Role(Role.RoleName.USER);
        Role actual = roleDao.add(expected);

        Assertions.assertNotNull(actual, "There was no one role added to DB");
        Assertions.assertEquals(1L, actual.getId(), "ID doesn't equal");
        Assertions.assertEquals(expected.getRoleName(), actual.getRoleName(), "RoleName doesn't equal");
    }

    @Test
    void add_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> roleDao.add(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void getRoleByName_successGetRole_ok() {
        Role expected = new Role(Role.RoleName.USER);
        roleDao.add(expected);
        Role role = roleDao.getByName("USER");
        Assertions.assertEquals(expected.getRoleName(), role.getRoleName(),
                "Roles don't equal");
    }

    @Test
    void getByName_lowerCaseRole_notOk() {
        Role expected = new Role(Role.RoleName.USER);
        roleDao.add(expected);
        Assertions.assertThrows(DataProcessingException.class,
                () -> roleDao.getByName("user"),
                "Method should throw DataProcessingException for the value user");
    }

    @Test
    void getByName_nullValue_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> roleDao.getByName(null),
                "Method should throw DataProcessingException for null value");
    }

    @Test
    void getByName_noRolePresentInDB_notOk() {
        Assertions.assertThrows(DataProcessingException.class, () -> roleDao.getByName("USER"),
                "Method should throw DataProcessingException when no one role exist");
    }

    @Test
    void getByName_noPresentRoleNameEnum_notOk() {
        Assertions.assertThrows(DataProcessingException.class,
                () -> roleDao.getByName("MANAGER"),
                "Method should throw DataProcessingException when no present RoleName");
    }
}