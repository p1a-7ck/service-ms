package com.epam.java.rt.lab.servlet;

import com.epam.java.rt.lab.component.NavigationComponent;
import com.epam.java.rt.lab.connection.ConnectionException;
import com.epam.java.rt.lab.dao.DaoException;
import com.epam.java.rt.lab.entity.rbac.Remember;
import com.epam.java.rt.lab.entity.rbac.User;
import com.epam.java.rt.lab.service.ServiceException;
import com.epam.java.rt.lab.service.UserService;
import com.epam.java.rt.lab.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

/**
 * Service Management System
 */
@WebFilter(urlPatterns = "/servlet/*", dispatcherTypes = DispatcherType.FORWARD)
public class RbacFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RbacFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        logger.debug("RbacFilter");
        try (UserService userService = new UserService();) {
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            Long userId = (Long) req.getSession().getAttribute("userId");
            User user = null;
            if (userId == null) {
                logger.debug("TRYING TO GET USER FROM COOKIE");
                String rememberCookieName = CookieManager.getRememberCookieName(req);
                String rememberCookieValue = CookieManager.getCookieValue(req, rememberCookieName);
                if (rememberCookieValue != null) {
                    Remember remember = userService.getRemember(rememberCookieName, rememberCookieValue);
                    if (remember != null) {
                        userId = remember.getUser().getId();
                        req.getSession().setAttribute("userId", userId);
                        req.getSession().setAttribute("userName", remember.getUser().getName());
                        req.getSession().setAttribute("navbarItemArray",
                                NavigationComponent.getNavbarItemArray(remember.getUser().getRole()));
                    } else {
                        CookieManager.removeCookie(req, (HttpServletResponse) servletResponse, rememberCookieName);
                    }
                }
            }
            if (userId == null) {
                logger.debug("ANONYMOUS URI = {}", userService.getAnonymous().getRole().getUriList());
                if (userService.getAnonymous().getRole().getUriList().contains(req.getPathInfo())) {
                    logger.debug("CONTAINS {}", req.getPathInfo());
                    filterChain.doFilter(servletRequest, servletResponse);
                    logger.debug("REDIRECT (SHOULD BE NULL) {}", req.getSession().getAttribute("redirect"));
                } else {
                    logger.debug("NEED TO REDIRECT");
                    HttpServletResponse resp = (HttpServletResponse) servletResponse;
                    Map<String, String> parameterMap = UrlManager.getRequestParameterMap(req.getQueryString());
                    parameterMap.put("redirect", req.getPathInfo());
                    resp.sendRedirect(UrlManager.getContextUri(req, "/profile/login", parameterMap));
                }
            } else {
                if (user == null) user = userService.getUser(userId);
                if (user.getRole().getUriList().contains(req.getPathInfo())) {
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    if (req.getPathInfo().equals("/profile/login")) {
                        ((HttpServletResponse) servletResponse).sendRedirect(UrlManager.getContextUri(req, "/profile/view"));
                    } else {
                        ((HttpServletResponse) servletResponse).sendError(403);
                    }
                }
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            throw new ServletException("exception.filter.rbac.do-filter.user-service", e.getCause());
        }
    }

    @Override
    public void destroy() {

    }

}
