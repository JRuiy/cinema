package cinema.service.impl;

import cinema.dao.RoleDao;
import cinema.model.Role;
import cinema.service.RoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LogManager.getLogger(RoleServiceImpl.class);
    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public Role add(Role role) {
        logger.info("Add role method was called. Params: role = {}", role);
        return roleDao.add(role);
    }

    @Override
    public Role getByName(String roleName) {
        logger.info("Get role by name method was called. Params: roleName = {}", roleName);
        return roleDao.getByName(roleName);
    }
}
