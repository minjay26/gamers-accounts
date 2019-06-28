package org.minjay.gamers.accounts.data.domain;

import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
public class User extends AbstractEntityAuditable<Long> {

    private static final long serialVersionUID = -505861674989465314L;

    private String username;
    private String password;

    private String phone;
    private String email;

    private int loginCount;

    private DateTime lastLoginDate;
    private String lastLoginIp;

    private boolean locked;

    private long version;

    @Override
    @Id
    @GeneratedValue
    public Long getId() {
        return super.getId();
    }

    @Column(name = "username", nullable = false, length = 100, unique = true, updatable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", nullable = false, length = 100)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "phone", length = 255)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "email", length = 255)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "login_count", nullable = false)
    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    @Column(name = "last_login_date")
    public DateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(DateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Column(name = "last_login_ip", length = 100)
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    @Column(name = "locked", nullable = false)
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Version
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
