package org.minjay.gamers.accounts.resource.server.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class VCodeAuthenticationToken extends AbstractAuthenticationToken {

    private String email;
    private String vcode;

    public VCodeAuthenticationToken(String email, String vcode) {
        super(null);
        this.email = email;
        this.vcode = vcode;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVcode() {
        return vcode;
    }
}
