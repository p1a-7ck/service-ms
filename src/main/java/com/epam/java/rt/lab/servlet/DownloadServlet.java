package com.epam.java.rt.lab.servlet;

import com.epam.java.rt.lab.connection.ConnectionException;
import com.epam.java.rt.lab.dao.DaoException;
import com.epam.java.rt.lab.service.UserService;
import com.epam.java.rt.lab.util.FormManager;
import org.h2.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * service-ms
 */
@WebServlet(urlPatterns = "/file/download/*")
public class DownloadServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DownloadServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("userId") != null) {
            if (req.getMethod().equals("GET")) {
                logger.debug("DOWNLOAD REQUESTED: {}", req.getPathInfo());
                InputStream inputStream = null;
                switch (req.getPathInfo()) {
                    case "/avatar":
                        String avatarId = req.getParameter("id");
                        String avatarPath = req.getParameter("path");
                        if (avatarId != null) {
                            logger.debug("AVATAR BY ID: {}", avatarId);
                            if (FormManager.isOnlyDigits(avatarId)) {
                                try {
                                    Map<String, Object> avatarMap = (new UserService()).getAvatar(Long.valueOf(avatarId));
                                    resp.setContentType((String) avatarMap.get("type"));
                                    inputStream = (InputStream) avatarMap.get("file");
                                } catch (DaoException | ConnectionException e) {
                                    e.printStackTrace();
                                    throw new ServletException(e.getMessage(), e.getCause());
                                }
                            }
                        } else if (avatarPath != null) {
                            logger.debug("AVATAR BY PATH: {}", avatarPath);
                            resp.setContentType(avatarPath.substring(avatarPath.lastIndexOf(".") + 1).replaceAll("_", "/"));
                            inputStream = new FileInputStream(new File(avatarPath));
                        }
                }
                if (inputStream != null) {
                    logger.debug("READY TO DOWNLOAD");
                    IOUtils.copy(inputStream, resp.getOutputStream());
                    inputStream.close();
                    resp.getOutputStream().close();
                    logger.debug("DOWNLOAD COMPLETE");
                }
            }
        }
    }

}
