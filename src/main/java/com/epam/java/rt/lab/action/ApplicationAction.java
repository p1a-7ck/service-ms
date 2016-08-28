package com.epam.java.rt.lab.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Service Management System
 */
@WebAction
public class ApplicationAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationAction.class);

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ActionException {
        try {
            logger.debug("/WEB-INF/jsp/application.jsp");
            req.getSession().setAttribute("navbar", NavbarComponent.getNavArray());
            req.getSession().setAttribute("navbarActive", 0);
            req.getRequestDispatcher("/WEB-INF/jsp/application.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            throw new ActionException(e.getMessage());
        }
    }

}
