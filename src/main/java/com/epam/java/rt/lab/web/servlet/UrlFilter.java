package com.epam.java.rt.lab.web.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Service Management System
 */
@WebFilter(urlPatterns = "/*", dispatcherTypes = DispatcherType.REQUEST)
public class UrlFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(UrlFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        servletRequest.setAttribute("debugMode", true);
        servletRequest.setCharacterEncoding("UTF-8");
        logger.debug("UriFilter:");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String requestURI = req.getRequestURI().substring((req).getContextPath().length()).toLowerCase();
        logger.debug(requestURI);
        if (requestURI.startsWith("/static/") || requestURI.startsWith("/webjars/")
                || requestURI.startsWith("/file/") || requestURI.equals("/favicon.ico")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            servletRequest.getRequestDispatcher("/servlet".concat(requestURI)).forward(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}