package com.epam.java.rt.lab.entity.business;

import com.epam.java.rt.lab.entity.BaseEntity;
import com.epam.java.rt.lab.entity.File;

import java.io.InputStream;
import java.sql.Timestamp;

public class Photo extends BaseEntity implements File {

    private String name;
    private String type;
    private InputStream file;
    private Timestamp modified;

    public static final Photo NULL_PHOTO = new Photo();

    public Photo() {
    }

    public Photo(Long id, String name, String type, InputStream file,
                 Timestamp modified) {
        super(id);
        this.name = name;
        this.type = type;
        this.file = file;
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }
}
