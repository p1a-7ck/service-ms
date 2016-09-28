package com.epam.java.rt.lab.service;

import com.epam.java.rt.lab.dao.DaoException;
import com.epam.java.rt.lab.dao.DaoParameter;
import com.epam.java.rt.lab.dao.Parameter_;
import com.epam.java.rt.lab.entity.EntityProperty;
import com.epam.java.rt.lab.entity.rbac.Role;
import com.epam.java.rt.lab.util.GlobalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * service-ms
 */
public class RoleService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    public RoleService()
            throws ServiceException {
    }

    public Role getRole(Long id)
            throws ServiceException {
//        try {
            return null;
//            return dao("Role").getFirst(new Parameter_()
//                    .filter(Parameter_.Field.set(
//                            Role.Property.ID, id
//                    ))
//            );
//        } catch (DaoException e) {
//            throw new ServiceException("exception.service.role.get-role.dao", e.getCause());
//        }
    }

    public Role getRoleAuthorized()
            throws ServiceException {
//        try {
//            return dao("Role").getFirst(new Parameter_()
//                    .filter(Parameter_.Field.set(
//                            Role.Property.NAME,
//                            GlobalProperties.getProperty("role.authorized")
//                    ))
//            );
//        } catch (DaoException e) {
//            throw new ServiceException("exception.service.role.get-role-authorized.dao", e.getCause());
//        }
        return null;
    }

    public List<Role> getRoleList()
            throws ServiceException {
        try {
            return dao("Role").read(new DaoParameter());
        } catch (DaoException e) {
            throw new ServiceException("exception.service.role.get-role-list.dao", e.getCause());
        }
    }

}
