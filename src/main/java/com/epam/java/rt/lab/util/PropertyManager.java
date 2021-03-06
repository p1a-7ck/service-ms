package com.epam.java.rt.lab.util;

import com.epam.java.rt.lab.exception.AppException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.epam.java.rt.lab.util.UtilExceptionCode.PROPERTY_READ_ERROR;

public final class PropertyManager {

    public static final String UNDERSCORE = "_";
    public static final String SPACE = " ";
    public static final String SLASH = "/";
    public static final String COMMA = ",";
    public static final String COMMA_WITH_SPACE = ", ";
    public static final String POINT = ".";
    public static final String EQUAL = "=";
    public static final String HYPHEN = "-";
    public static final String COLON = ":";
    public static final String QUESTION = "?";
    public static final String AMPERSAND = "&";
    public static final String ASTERISK = "*";
    public static final String LEFT_PARENTHESIS = "(";
    public static final String RIGHT_PARENTHESIS = ")";

    public static final String ESCAPED_QUESTION = "\\?";
    public static final String ESCAPED_BACKSLASH = "\\";
    public static final String ESCAPED_POINT = "\\.";

    public static final String DEF_LOCALE_LANG_KEY = "def.locale.lang";
    public static final String DEF_LOCALE_COUNTRY_KEY = "def.locale.country";
    public static final String REMEMBER_DAYS_VALID_KEY = "remember.days.valid";
    public static final String RESTORE_SECONDS_VALID_KEY = "restore.seconds.valid";

    public static final long UPLOAD_FILE_MAX_SIZE = 3145728;

    public static final String ANONYMOUS = "anonymous";
    public static final String AUTHORIZED = "authorized";

    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final String USER_ATTR = "user";
    public static final String LOGIN_ATTR = "login";

    public static final String USER_LIST = "userList";
    public static final String CATEGORY_LIST = "categoryList";
    public static final String APPLICATION_LIST = "applicationList";
    public static final String COMMENT_LIST = "commentList";

    public static final String ID = "id";
    public static final String CREATED = "created";
    public static final String PATH = "path";
    public static final String FILE = "file";
    public static final String REDIRECT = "redirect";
    public static final String PAGE = "page";
    public static final String ITEMS = "items";

    public static final String USER_FIRST_NAME = "firstName";
    public static final String USER_MIDDLE_NAME = "middleName";
    public static final String USER_LAST_NAME = "lastName";
    public static final String USER_AVATAR_DOWNLOAD = "avatarDownload";
    public static final String USER_LOGIN_EMAIL = "loginEmail";
    public static final String USER_ROLE_NAME = "roleName";
    public static final String USER_LOGIN_ATTEMPT_LEFT = "loginAttemptLeft";
    public static final String USER_LOGIN_STATUS = "loginStatus";

    public static final String CATEGORY_NAME = "categoryName";
    public static final String CATEGORY_PARENT = "parentCategory";
    public static final String APPLICATION_USER_NAME = "userName";
    public static final String APPLICATION_MESSAGE = "applicationMessage";
    public static final String COMMENT_PHOTO = "photoDownload";
    public static final String COMMENT_MESSAGE = "commentMessage";

    public static final String RESTORE_EMAIL_ATTR = "restoreEmail";
    public static final String RESTORE_REF_ATTR = "restoreRef";
    public static final String ACTIVATION_EMAIL_ATTR = "activationEmail";
    public static final String ACTIVATION_REF_ATTR = "activationRef";
    public static final String MESSAGE_ATTR = "message";

    public static final String LOGIN_EMAIL = "email";
    public static final String CODE = "code";
    public static final String LOGIN_PASSWORD = "password";
    public static final String LOGIN_NEW_PASSWORD = "newPassword";
    public static final String LOGIN_REPEAT_PASSWORD = "repeatPassword";
    public static final String REMEMBER_ME = "rememberMe";
    public static final String FORM_MESSAGE_LIST = "formMessageList";
    public static final String SUBMIT_LOGIN = "submitLogin";
    public static final String SUBMIT_RESTORE = "submitRestore";
    public static final String SUBMIT_SAVE_PROFILE = "submitSaveProfile";
    public static final String SUBMIT_REMOVE_AVATAR = "submitRemoveAvatar";

    public static final String HOME_PATH = "/home";
    public static final String LOGIN_PATH = "/profile/login";
    public static final String PROFILE_VIEW_PATH = "/profile/view";
    public static final String PROFILE_EDIT_PATH = "/profile/edit";
    public static final String PROFILE_RESTORE_PATH = "/profile/restore";
    public static final String PROFILE_ACTIVATE_PATH = "/profile/activate";
    public static final String USER_LIST_PATH = "/user/list";
    public static final String USER_VIEW_PATH = "/user/view";
    public static final String CATEGORY_LIST_PATH = "/category/list";
    public static final String CATEGORY_VIEW_PATH = "/category/view";
    public static final String APPLICATION_LIST_PATH = "/application/list";
    public static final String APPLICATION_VIEW_PATH = "/application/view";
    public static final String JSP_BASE_PATH = "/WEB-INF/jsp";

    public static final String SERVLET_PATH = "/servlet";
    public static final String STATIC_PATH_WITH_SLASH = "/static/";
    public static final String WEBJARS_PATH_WITH_SLASH = "/webjars/";
    public static final String FILE_PATH_WITH_SLASH = "/file/";
    public static final String FAVICON_PATH = "/favicon.ico";
    public static final String FILE_UPLOAD_PATH = "/file/upload";
    public static final String FILE_DOWNLOAD_PATH = "/file/download";
    public static final String FILE_AVATAR_PREFIX = "/avatar";
    public static final String FILE_PHOTO_PREFIX = "/photo";

    public static final String AVATAR_UPLOAD_TYPE = "avatar";
    public static final String PHOTO_UPLOAD_TYPE = "photo";

    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_NO_CACHE_VALUE = "no-cache, no-store, must-revalidate";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String HEADER_PRAGMA_VALUE = "no-cache";
    public static final String HEADER_DATE_EXPIRES = "Expires";

    private static final String GLOBAL_PROPERTY_FILE = "global.properties";

    private static final Properties properties = new Properties();

    private PropertyManager() {
    }

    public static void initGlobalProperties() throws AppException {
        ClassLoader classLoader = PropertyManager.class.getClassLoader();
        InputStream inputStream = classLoader.
                getResourceAsStream(GLOBAL_PROPERTY_FILE);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            String[] detailArray = {GLOBAL_PROPERTY_FILE};
            throw new AppException(PROPERTY_READ_ERROR,
                    e.getMessage(), e.getCause(), detailArray);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}