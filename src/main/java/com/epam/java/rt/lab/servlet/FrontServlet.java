package com.epam.java.rt.lab.servlet;

import com.epam.java.rt.lab.action.Action;
import com.epam.java.rt.lab.action.ActionException;
import com.epam.java.rt.lab.action.ActionFactory;
import com.epam.java.rt.lab.util.CookieManager;
import com.epam.java.rt.lab.util.UrlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Service Management System
 */
@WebServlet(urlPatterns = "/servlet/*")
public class FrontServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FrontServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("FrontServlet: {}", CookieManager.getCookie(req, "REQUEST_PARAMETER_REDIRECT") != null ?
                CookieManager.getCookie(req, "REQUEST_PARAMETER_REDIRECT").getValue() : "REDIRECT_COOKIE_EMPTY");
        logger.debug("PARAMETERS: {}", UrlManager.getRequestParameterString(req));
        try {
            if (req.getPathInfo().equals("/")) {
                ActionFactory.getAction("/home").execute(req, resp);
            } else {
                ActionFactory.getAction(req.getPathInfo()).execute(req, resp);
            }
        } catch (ActionException e) {
            logger.error("ActionException", e);
            throw new ServletException(e.getMessage());
        }
    }

}
