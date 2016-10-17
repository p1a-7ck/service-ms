package com.epam.java.rt.lab.web.listener;

import com.epam.java.rt.lab.exception.AppException;
import com.epam.java.rt.lab.util.PropertyManager;
import com.epam.java.rt.lab.util.TimestampManager;
import com.epam.java.rt.lab.util.file.UploadManager;
import com.epam.java.rt.lab.web.access.RoleFactory;
import com.epam.java.rt.lab.web.action.ActionFactory;
import com.epam.java.rt.lab.web.validator.ValidatorFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            PropertyManager.initGlobalProperties();
            AppException.initExceptionBundle();
            TimestampManager.initDateList();
            TimestampManager.initTimeList();
            ValidatorFactory.getInstance().initValidatorMap();
            RoleFactory.getInstance().initRoleMap();
            ActionFactory.getInstance().initActionMap();
            UploadManager.initContentTypeListMap();
        } catch (AppException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
