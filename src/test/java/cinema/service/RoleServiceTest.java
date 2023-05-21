package cinema.service;

import java.util.NoSuchElementException;
import cinema.dao.RoleDao;
import cinema.model.Role;
import cinema.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    private RoleDao roleDao;
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleDao);
    }

    @Test
    void add_successSave_ok() {
        Role role = new Role(Role.RoleName.USER);
        Mockito.when(roleDao.add(role)).thenReturn(new Role(1L, Role.RoleName.USER));
        Role actual = roleService.add(role);
        Assertions.assertNotNull(actual, "Role must be not null");
        Assertions.assertEquals(actual.getId(), 1L,
                "Id should be 1, but actual is " + actual.getId());
    }

    @Test
    void getByName_roleExist_ok() {
        Mockito.when(roleDao.getByName("USER"))
                .thenReturn(new Role(1L, Role.RoleName.USER));
        Role actual = roleService.getByName("USER");
        Assertions.assertNotNull(actual, "Role must be not null");
        Assertions.assertEquals(actual.getRoleName(), Role.RoleName.USER,
                "RoleNames don't match. Actual is " + actual.getRoleName().name()
                        + " but must be " + Role.RoleName.USER.name());
    }

    @Test
    void getByName_roleDontExist_ok() {
        Mockito.when(roleDao.getByName("ADMIN")).thenThrow(NoSuchElementException.class);
        Assertions.assertThrows(NoSuchElementException.class,
                () -> roleService.getByName("ADMIN"),
                "Method should throw NoSuchElementException");
    }
}