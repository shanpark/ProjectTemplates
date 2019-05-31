package com.hansdesk.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginForm {

    @NotNull
    @Size(min = 1)
    @Email
    private String email;

    @NotNull
    @Size(min = 1)
    private String password;

    private Boolean saveId;

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

    public Boolean getSaveId() {
        return saveId;
    }

    public void setSaveId(Boolean saveId) {
        this.saveId = saveId;
    }
}
