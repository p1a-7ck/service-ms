package com.epam.java.rt.lab.web.tag;

import com.epam.java.rt.lab.util.TimestampManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * category-ms
 */
public class DateValue extends SimpleTagSupport {

    private String stringValue;
    private Locale locale;

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void doTag() throws JspException, IOException {
        DateFormat dateFormat = DateFormat.
                getDateInstance(DateFormat.MEDIUM, locale);
        Date dateValue = TimestampManager.getDate(stringValue);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        JspWriter out = getJspContext().getOut();
        out.print("<small>" + dateFormat.format(dateValue)
                  + "</small> " + timeFormat.format(dateValue));
    }

}
