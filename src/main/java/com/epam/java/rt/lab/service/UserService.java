package com.epam.java.rt.lab.service;

import com.epam.java.rt.lab.dao.DaoParameter;
import com.epam.java.rt.lab.dao.sql.OrderBy;
import com.epam.java.rt.lab.dao.sql.Update.SetValue;
import com.epam.java.rt.lab.dao.sql.Where;
import com.epam.java.rt.lab.dao.sql.WherePredicateOperator;
import com.epam.java.rt.lab.entity.access.*;
import com.epam.java.rt.lab.exception.AppException;
import com.epam.java.rt.lab.util.*;
import com.epam.java.rt.lab.util.file.UploadManager;
import com.epam.java.rt.lab.web.access.Role;
import com.epam.java.rt.lab.web.access.RoleFactory;
import com.epam.java.rt.lab.web.component.FormControlValue;
import com.epam.java.rt.lab.web.component.Page;
import com.epam.java.rt.lab.web.validator.Validator;
import com.epam.java.rt.lab.web.validator.ValidatorFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epam.java.rt.lab.entity.access.Avatar.NULL_AVATAR;
import static com.epam.java.rt.lab.entity.access.Login.NULL_LOGIN;
import static com.epam.java.rt.lab.entity.access.User.NULL_USER;
import static com.epam.java.rt.lab.exception.AppExceptionCode.NULL_NOT_ALLOWED;
import static com.epam.java.rt.lab.service.ServiceExceptionCode.AVATAR_FILE_ACCESS_ERROR;
import static com.epam.java.rt.lab.service.ServiceExceptionCode.GET_USER_ERROR;
import static com.epam.java.rt.lab.util.CookieManager.getCookieValue;
import static com.epam.java.rt.lab.util.CookieManager.getUserAgentCookieName;
import static com.epam.java.rt.lab.util.PropertyManager.*;
import static com.epam.java.rt.lab.web.validator.ValidatorFactory.DIGITS;
import static com.epam.java.rt.lab.web.validator.ValidatorFactory.NAME;

public class UserService extends BaseService {

    /**
     *
     * @param login
     * @return
     * @throws AppException
     */
    public User getUser(Login login) throws AppException {
        if (login == null) {
            throw new AppException(NULL_NOT_ALLOWED);
        }
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setWherePredicate(Where.Predicate.
                get(UserProperty.LOGIN_ID, WherePredicateOperator.EQUAL,
                        login.getId()));
        List<User> userList = dao(User.class.getSimpleName()).
                read(daoParameter);
        return ((userList != null) && (userList.size() > 0))
                ? userList.get(0)
                : NULL_USER;
    }

    /**
     *
     * @param user
     * @return
     * @throws AppException
     */
    public int updateUser(User user) throws AppException {
        if (user == null) {
            throw new AppException(NULL_NOT_ALLOWED);
        }
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setSetValueArray(
                new SetValue(UserProperty.FIRST_NAME, user.getFirstName()),
                new SetValue(UserProperty.MIDDLE_NAME,
                        user.getMiddleName()),
                new SetValue(UserProperty.LAST_NAME, user.getLastName()),
                new SetValue(UserProperty.AVATAR_ID, user.getAvatarId()),
                new SetValue(UserProperty.ROLE_NAME,
                        user.getRole().getName()));
        daoParameter.setWherePredicate(Where.Predicate.
                get(UserProperty.ID,
                        WherePredicateOperator.EQUAL, user.getId()));
        return dao(User.class.getSimpleName()).update(daoParameter);
    }

    /**
     *
     * @param session
     * @param firstNameValue
     * @param middleNameValue
     * @param lastNameValue
     * @param avatarDownloadValue
     * @return
     * @throws AppException
     */
    public boolean updateUser(HttpSession session,
                              FormControlValue firstNameValue,
                              FormControlValue middleNameValue,
                              FormControlValue lastNameValue,
                              FormControlValue avatarDownloadValue)
            throws AppException {
        User user = (User) session.getAttribute(USER_ATTR);
        if (user == null) throw new AppException(GET_USER_ERROR);
        return updateUser(user, firstNameValue, middleNameValue,
                lastNameValue, avatarDownloadValue, null, null, null);
    }

    /**
     * @param user
     * @param firstNameValue
     * @param middleNameValue
     * @param lastNameValue
     * @param avatarDownloadValue
     * @param roleNameValue
     * @param loginAttemptLeftValue
     * @param loginStatusValue
     * @return
     * @throws AppException
     */
    public boolean updateUser(User user,
                              FormControlValue firstNameValue,
                              FormControlValue middleNameValue,
                              FormControlValue lastNameValue,
                              FormControlValue avatarDownloadValue,
                              FormControlValue roleNameValue,
                              FormControlValue loginAttemptLeftValue,
                              FormControlValue loginStatusValue)
            throws AppException {
        if ((user == null || firstNameValue == null
                || middleNameValue == null || lastNameValue == null
                || avatarDownloadValue == null)
                || (!(roleNameValue == null && loginAttemptLeftValue == null
                && loginStatusValue == null) && !(roleNameValue != null
                && loginAttemptLeftValue != null && loginStatusValue != null))) {
            throw new AppException(NULL_NOT_ALLOWED);
        }
        boolean formValid = true;
        Validator nameValidator = ValidatorFactory.getInstance().create(NAME);
        String[] validationMessageArray = nameValidator.
                validate(firstNameValue.getValue());
        if (validationMessageArray.length > 0) {
            firstNameValue.setValidationMessageList(new ArrayList<>
                    (Arrays.asList(validationMessageArray)));
            formValid = false;
        }
        validationMessageArray = nameValidator.
                validate(middleNameValue.getValue());
        if (validationMessageArray.length > 0) {
            middleNameValue.setValidationMessageList(new ArrayList<>
                    (Arrays.asList(validationMessageArray)));
            formValid = false;
        }
        validationMessageArray = nameValidator.
                validate(lastNameValue.getValue());
        if (validationMessageArray.length > 0) {
            lastNameValue.setValidationMessageList(new ArrayList<>
                    (Arrays.asList(validationMessageArray)));
            formValid = false;
        }
        if (roleNameValue != null) {
            if (RoleFactory.getInstance().getRoleMap().
                    get(roleNameValue.getValue()) == null) {
                validationMessageArray = new String[]
                        {"message.profile.role-not-exist"};
                roleNameValue.setValidationMessageList(new ArrayList<>
                        (Arrays.asList(validationMessageArray)));
                formValid = false;
            }
            Validator digitValidator = ValidatorFactory.getInstance().
                    create(DIGITS);
            validationMessageArray = digitValidator.
                    validate(loginAttemptLeftValue.getValue());
            if (validationMessageArray.length > 0) {
                loginAttemptLeftValue.setValidationMessageList(new ArrayList<>
                        (Arrays.asList(validationMessageArray)));
                formValid = false;
            }
            if (!loginStatusValue.getValue().equals("0")
                    && !loginStatusValue.getValue().equals("1")) {
                validationMessageArray = new String[]
                        {"message.profile.status-not-exist"};
                loginStatusValue.setValidationMessageList(new ArrayList<>
                        (Arrays.asList(validationMessageArray)));
                formValid = false;
            }
        }
        if (!formValid) return false;
        user.setFirstName(firstNameValue.getValue());
        user.setMiddleName(middleNameValue.getValue());
        user.setLastName(lastNameValue.getValue());
        if (roleNameValue != null) {
            Role role = RoleFactory.getInstance().
                    create(roleNameValue.getValue());
            user.setRole(role);
            Integer attemptLeft =
                    Integer.valueOf(loginAttemptLeftValue.getValue());
            user.getLogin().setAttemptLeft(attemptLeft);
            Integer status =
                    Integer.valueOf(loginStatusValue.getValue());
            user.getLogin().setStatus(status);
            LoginService loginService = new LoginService();
            loginService.updateLogin(user.getLogin());
        }
        String[] pair = avatarDownloadValue.getValue().split(ESCAPED_QUESTION);
        if (pair.length == 2) {
            pair = pair[1].split(EQUAL);
            if (ID.equals(pair[0])) {
                // avatar not changed

            } else if (PATH.equals(pair[0])) {
                // avatar recently uploaded
                setAvatar(user, pair[1]);
            }
            updateUser(user);
            return true;
        }
        // avatar removed
        Long avatarId = user.getAvatarId();
        user.setAvatarId(null);
        updateUser(user);
        removeAvatar(avatarId);
        return true;
    }

    /**
     *
     * @param login
     * @return
     * @throws AppException
     */
    public User addUser(Login login) throws AppException {
        if (login == null || login == NULL_LOGIN) {
            throw new AppException(NULL_NOT_ALLOWED);
        }
        try (LoginService loginService = new LoginService()) {
            dao(User.class.getSimpleName()); // initiating daoFactory
            super.daoFactory.
                    beginTransaction(Connection.TRANSACTION_REPEATABLE_READ);
            User user = new User();
            login.setId(loginService.addLogin(login));
            user.setLogin(login);
            user.setRole(RoleFactory.getInstance().createAuthorized());
            DaoParameter daoParameter = new DaoParameter();
            daoParameter.setEntity(user);
            user.setId(dao(User.class.getSimpleName()).create(daoParameter));
            super.daoFactory.commitTransaction();
            return user;
        }
    }

    /**
     *
     * @param id
     * @return
     * @throws AppException
     */
    public User getUser(Long id) throws AppException {
        if (id == null) throw new AppException(NULL_NOT_ALLOWED);
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setWherePredicate(Where.Predicate.
                get(UserProperty.ID, WherePredicateOperator.EQUAL, id));
        List<User> userList = dao(User.class.getSimpleName()).
                read(daoParameter);
        return ((userList != null) && (userList.size() > 0))
                ? userList.get(0)
                : NULL_USER;
    }

    /**
     *
     * @return
     * @throws AppException
     */
    public User getAnonymous() throws AppException {
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setWherePredicate(Where.Predicate.
                get(LoginProperty.EMAIL, WherePredicateOperator.EQUAL, ""));
        List<User> userList = dao(User.class.getSimpleName()).
                read(daoParameter);
        return ((userList != null) && (userList.size() > 0))
                ? userList.get(0)
                : NULL_USER;
    }

    /**
     *
     * @param page
     * @return
     * @throws AppException
     */
    public List<User> getUserList(Page page) throws AppException {
        if (page == null) throw new AppException(NULL_NOT_ALLOWED);
        DaoParameter daoParameter = new DaoParameter();
        Long count = dao(User.class.getSimpleName()).count(daoParameter);
        page.setCountItems(count);
        daoParameter.setOrderByCriteriaArray(OrderBy.Criteria.
                asc(UserProperty.FIRST_NAME));
        daoParameter.setLimit((page.getCurrentPage() - 1)
                * page.getItemsOnPage(), page.getItemsOnPage());
        return dao(User.class.getSimpleName()).read(daoParameter);
    }

    /**
     *
     * @param req
     * @param resp
     * @return
     * @throws AppException
     */
    public User getUserRemember(HttpServletRequest req,
                                HttpServletResponse resp)
            throws AppException {
        if (req == null || resp == null) {
            throw new AppException(NULL_NOT_ALLOWED);
        }
        String rememberCookieName = getUserAgentCookieName(req);
        String rememberCookieValue = getCookieValue(req, rememberCookieName);
        if (rememberCookieValue == null) return NULL_USER;
        List<Remember> rememberList =
                getRememberList(rememberCookieName, rememberCookieValue);
        if (rememberList == null
                || rememberList.size() == 0) return NULL_USER;
        User user = rememberList.get(0).getUser();
        if (user == null) return NULL_USER;
        Timestamp currentTimestamp = TimestampManager.getCurrentTimestamp();
        Timestamp validTimestamp = rememberList.get(0).getValid();
        if (TimestampManager.secondsBetweenTimestamps(currentTimestamp,
                validTimestamp) < 0) return NULL_USER;
        if (user.getLogin().getAttemptLeft() == 0
                || user.getLogin().getStatus() < 0) return NULL_USER;
        removeUserRemember(user);
        CookieManager.removeCookie(resp, rememberCookieName,
                UrlManager.getUriWithContext(req, ""));
        rememberCookieValue = HashGenerator.hashString();
        Remember remember = addUserRemember(user,
                rememberCookieName, rememberCookieValue);
        currentTimestamp = TimestampManager.getCurrentTimestamp();
        int maxAge = TimestampManager.
                secondsBetweenTimestamps(currentTimestamp, remember.getValid());
        CookieManager.setCookie(resp, rememberCookieName, rememberCookieValue,
                maxAge, UrlManager.getUriWithContext(req, SLASH));
        return user;
    }

    /**
     *
     * @param user
     * @return
     * @throws AppException
     */
    public int removeUserRemember(User user)
            throws AppException {
        if (user == null) throw new AppException(NULL_NOT_ALLOWED);
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setWherePredicate(Where.Predicate.
                get(RememberProperty.USER_ID, WherePredicateOperator.EQUAL,
                        user.getId()));
        return dao(Remember.class.getSimpleName()).delete(daoParameter);
    }

    /**
     *
     * @param user
     * @param rememberCookieName
     * @param rememberCookieValue
     * @return
     * @throws AppException
     */
    public Remember addUserRemember(User user, String rememberCookieName,
                                     String rememberCookieValue)
            throws AppException {
        String rememberValidString = PropertyManager.
                getProperty(REMEMBER_DAYS_VALID_KEY);
        if (rememberValidString == null
                || ValidatorFactory.getInstance().create(DIGITS).
                validate(rememberValidString).length > 0) {
            throw new AppException(NULL_NOT_ALLOWED);
        }
        Remember remember = new Remember();
        remember.setUser(user);
        remember.setCookieName(rememberCookieName);
        remember.setCookieValue(rememberCookieValue);
        Timestamp currentTimestamp = TimestampManager.getCurrentTimestamp();
        remember.setValid(TimestampManager.daysToTimestamp(currentTimestamp,
                Integer.valueOf(rememberValidString)));
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setEntity(remember);
        dao(Remember.class.getSimpleName()).create(daoParameter);
        return remember;
    }

    /**
     *
     * @param cookieName
     * @param cookieValue
     * @return
     * @throws AppException
     */
    private List<Remember> getRememberList(String cookieName,
                                           String cookieValue)
            throws AppException {
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setWherePredicate(Where.Predicate.
                get(Where.Predicate.
                                get(RememberProperty.COOKIE_NAME,
                                        WherePredicateOperator.EQUAL, cookieName),
                        WherePredicateOperator.AND,
                        Where.Predicate.get(RememberProperty.COOKIE_VALUE,
                                WherePredicateOperator.EQUAL, cookieValue)));
        return dao(Remember.class.getSimpleName()).read(daoParameter);
    }

    /**
     *
     * @param id
     * @return
     * @throws AppException
     */
    public Avatar getAvatar(String id) throws AppException {
        if (id == null) throw new AppException(NULL_NOT_ALLOWED);
        if (ValidatorFactory.getInstance().create(DIGITS).
                validate(id).length > 0) return NULL_AVATAR;
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setWherePredicate(Where.Predicate.
                get(AvatarProperty.ID, WherePredicateOperator.EQUAL, id));
        List<Avatar> avatarList = dao(Avatar.class.getSimpleName()).
                read(daoParameter);
        if (avatarList == null || avatarList.size() == 0) {
            return NULL_AVATAR;
        }
        return avatarList.get(0);
    }

    /**
     *
     * @param user
     * @param filePath
     * @throws AppException
     */
    public void setAvatar(User user, String filePath) throws AppException {
        if (user == null || filePath == null) {
            throw new AppException(NULL_NOT_ALLOWED);
        }
        int backslashLastIndex = filePath.lastIndexOf(ESCAPED_BACKSLASH);
        String fileName = filePath.substring(backslashLastIndex + 1);
        String[] metaInfo = UploadManager.
                getMetaInfoFromPrefix(fileName, AVATAR_UPLOAD_TYPE);
        String avatarName = metaInfo[0];
        String contentType = metaInfo[1];
        File file = new File(filePath);
        try (InputStream inputStream = new FileInputStream(file)) {
            Avatar avatar = new Avatar();
            avatar.setId(user.getAvatarId());
            avatar.setName(avatarName);
            avatar.setType(contentType);
            avatar.setFile(inputStream);
            avatar.setModified(TimestampManager.getCurrentTimestamp());
            if (avatar.getId() == null) {
                DaoParameter daoParameter = new DaoParameter();
                daoParameter.setEntity(avatar);
                Long avatarId = dao(Avatar.class.getSimpleName()).
                        create(daoParameter);
                user.setAvatarId(avatarId);
            } else {
                DaoParameter daoParameter = new DaoParameter();
                daoParameter.setSetValueArray(
                        new SetValue(AvatarProperty.NAME, avatar.getName()),
                        new SetValue(AvatarProperty.TYPE, avatar.getType()),
                        new SetValue(AvatarProperty.FILE, avatar.getFile()),
                        new SetValue(AvatarProperty.MODIFIED,
                                avatar.getModified()));
                daoParameter.setWherePredicate(Where.Predicate.
                        get(AvatarProperty.ID, WherePredicateOperator.EQUAL,
                                avatar.getId()));
                dao(Avatar.class.getSimpleName()).update(daoParameter);
            }
        } catch (IOException e) {
            String[] detailArray = {filePath};
            throw new AppException(AVATAR_FILE_ACCESS_ERROR,
                    e.getMessage(), e.getCause(), detailArray);
        }
    }

    /**
     *
     * @param avatarId
     * @return
     * @throws AppException
     */
    public int removeAvatar(Long avatarId) throws AppException {
        DaoParameter daoParameter = new DaoParameter();
        daoParameter.setWherePredicate(Where.Predicate.
                get(AvatarProperty.ID, WherePredicateOperator.EQUAL, avatarId));
        return dao(Avatar.class.getSimpleName()).delete(daoParameter);
    }

}