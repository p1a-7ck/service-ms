package com.epam.java.rt.lab.entity.rbac;

import com.epam.java.rt.lab.entity.BaseEntity;

/**
 * service-ms
 */
public class User extends BaseEntity {
    private String firstName;
    private String middleName;
    private String lastName;
    private Login login;
    private Role role;

    public User() {
    }

    public User(Long id, String firstName, String middleName, String lastName, Login login, Role role) {
        super(id);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.login = login;
        this.role = role;
    }

    public Long getId() {
        return super.getId();
    }

    public void setId(Long id) {
        super.setId(id);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return firstName.concat(" ").concat(lastName);
    }
}