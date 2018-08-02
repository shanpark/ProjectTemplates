package com.hansdesk.user.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SignUpUser {

    @Id
    @Column(length = 512, nullable = false)
    private String email;

    @Column(length = 130, nullable = false)
    private String password;

    @Column(length = 128, nullable = false)
    private String firstName;

    @Column(length = 128, nullable = false)
    private String lastName;

    @Column(length = 128, nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
