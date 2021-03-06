package com.epam.java.rt.lab.web.action.profile;

import com.epam.java.rt.lab.entity.access.User;
import com.epam.java.rt.lab.exception.AppException;
import com.epam.java.rt.lab.util.UrlManager;
import com.epam.java.rt.lab.web.action.Action;
import com.epam.java.rt.lab.web.action.BaseAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.epam.java.rt.lab.util.PropertyManager.*;
import static com.epam.java.rt.lab.web.action.ActionExceptionCode.ACTION_FORWARD_TO_JSP_ERROR;

public class GetViewAction extends BaseAction implements Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp)
            throws AppException {
        User user = (User) req.getSession().getAttribute(USER_ATTR);
        req.setAttribute(USER_FIRST_NAME, user.getFirstName());
        req.setAttribute(USER_MIDDLE_NAME, user.getMiddleName());
        req.setAttribute(USER_LAST_NAME, user.getLastName());
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put(ID, String.valueOf(user.getAvatarId()));
        req.setAttribute(USER_AVATAR_DOWNLOAD,
                UrlManager.getUriWithContext(req,
                        FILE_DOWNLOAD_PATH + FILE_AVATAR_PREFIX, parameterMap));
        try {
            req.getRequestDispatcher(super.getJspName()).forward(req, resp);
        } catch (ServletException | IOException e) {
            throw new AppException(ACTION_FORWARD_TO_JSP_ERROR,
                    e.getMessage(), e.getCause());
        }
    }

}
