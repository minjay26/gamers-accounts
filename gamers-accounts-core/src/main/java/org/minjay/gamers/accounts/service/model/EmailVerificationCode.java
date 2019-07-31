package org.minjay.gamers.accounts.service.model;

import org.hibernate.validator.constraints.Email;

public class EmailVerificationCode {

    @Email
    private String email;
    private String type;

    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
