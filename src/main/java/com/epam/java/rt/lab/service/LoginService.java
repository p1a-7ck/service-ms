package com.epam.java.rt.lab.service;

import com.epam.java.rt.lab.dao.DaoException;
import com.epam.java.rt.lab.dao.DaoParameter;
import com.epam.java.rt.lab.dao.sql.Where;
import com.epam.java.rt.lab.entity.rbac.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * service-ms
 */
public class LoginService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public LoginService()
            throws ServiceException {
    }

    public Login getLogin(String email)
            throws ServiceException {
        try {
            List<Login> loginList = dao("Login").read(new DaoParameter()
                    .setWherePredicate(Where.Predicate.get(
                            Login.Property.EMAIL,
                            Where.Predicate.PredicateOperator.EQUAL,
                            email
                    ))
            );
            return loginList != null && loginList.size() > 0 ? loginList.get(0) : null;
        } catch (DaoException e) {
            throw new ServiceException("exception.service.login.get-login.dao", e.getCause());
        }
    }

//    public int addLogin(Login login) throws DaoException {
//        Dao_ dao = daoFactory.createDao("Login");
//        return dao.create(login);
//    }
//
//    public int updatePassword(Login login)
//            throws DaoException, SQLException, ConnectionException {
//        logger.debug("updateLogin");
//        Dao_ dao = daoFactory.createDao("Login");
//        return dao.update(login, "id", "password.regex");
//    }
//
//    public int updateAttemptLeft(Login login)
//            throws DaoException, SQLException, ConnectionException {
//        logger.debug("updateLogin");
//        Dao_ dao = daoFactory.createDao("Login");
//        return dao.update(login, "id", "attemptLeft");
//    }
//
//    public boolean isLoginExists(String email) throws DaoException {
//        Login login = new Login();
//        login.setEmail(email);
//        Dao_ dao = daoFactory.createDao("Login");
//        if (dao.getFirst(login, "email.regex", null) != null) return true;
//        Map<String, Object> activationMap = (Map<String, Object>) dao.getRelEntity(login, "Activation");
//        return activationMap != null;
//    }
//
//    private <T> Map<String, Object> getRelEntity(T entity, String relEntityName) throws DaoException {
//        Dao_ dao = daoFactory.createDao("Login");
//        Map<String, Object> relEntityMap = (Map<String, Object>) dao.getRelEntity(entity, relEntityName);
//        return relEntityMap;
//    }
//
//    private <T> void setRelEntity(T entity, String relEntityName, Object relEntity) throws DaoException {
//        if (relEntity != null) {
//            Dao_ dao = daoFactory.createDao("Login");
//            dao.setRelEntity(entity, relEntityName, relEntity);
//        }
//    }
//
//    private <T> void removeRelEntity(T entity, String relEntityName) throws DaoException {
//        Dao_ dao = daoFactory.createDao("Login");
//        dao.removeRelEntity(entity, relEntityName);
//    }
//
//    public String createActivationCode(String email, String password) throws DaoException {
//        try {
//            String activationCode = HashGenerator.hashString(email.concat(password));
//            Login login = new Login();
//            login.setEmail(email);
//            login.setPassword(password);
//            setRelEntity(login, "Activation", activationCode);
//            return activationCode;
//        } catch (DaoException e) {
//            e.printStackTrace();
//            throw e;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public Login confirmActivationCode(String activationEmail, String activationCode) throws DaoException {
//        if (activationEmail == null || activationCode == null) return null;
//        Login login = new Login();
//        login.setEmail(activationEmail);
//        Map<String, Object> activationMap = getRelEntity(login, "Activation");
//        removeRelEntity(login, "Activation");
//        if (activationMap == null || !activationCode.equals(activationMap.get("code")) ||
//                TimestampCompare.secondsBetweenTimestamps(TimestampCompare.getCurrentTimestamp(),
//                (Timestamp) activationMap.get("valid")) <= 0) return null;
//        login.setPassword((String) activationMap.get("password.regex"));
//        login.setAttemptLeft(Integer.valueOf(GlobalProperties.getProperty("login.attempt.max")));
//        login.setStatus(0);
//        return login;
//    }
//
//    public void setForgotCode(String email, String code) throws DaoException {
//        try {
//            Login login = new Login();
//            login.setEmail(email);
//            setRelEntity(login, "Forgot", code);
//        } catch (DaoException e) {
//            e.printStackTrace();
//            throw e;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Login confirmForgotCode(String forgotEmail, String forgotCode) throws DaoException {
//        if (forgotEmail == null || forgotCode == null) return null;
//        Login login = new Login();
//        login.setEmail(forgotEmail);
//        Map<String, Object> forgotMap = getRelEntity(login, "Forgot");
//        if (forgotMap == null || !forgotCode.equals(forgotMap.get("code")) ||
//                TimestampCompare.secondsBetweenTimestamps(TimestampCompare.getCurrentTimestamp(),
//                        (Timestamp) forgotMap.get("valid")) <= 0) return null;
//        return getLogin(forgotEmail);
//    }
//
//    public void removeForgotCode(String forgotEmail) throws DaoException {
//        if (forgotEmail != null) {
//            Login login = new Login();
//            login.setEmail(forgotEmail);
//            removeRelEntity(login, "Forgot");
//        }
//    }
}
