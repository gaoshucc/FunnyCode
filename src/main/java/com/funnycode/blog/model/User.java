package com.funnycode.blog.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gaoshucc
 * @create 2018-11-11 15:49
 */
public class User implements Serializable {
    private Long userId;
    private String username;
    private String password;
    private String nickname;
    private String salt;
    private String profilePath;
    private Long experience;
    private String role;
    private Integer gender;
    private String motto;
    private Date regtime;
    private Date lastLogintime;
    private String email;
    private Integer status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastLogintime() {
        return lastLogintime;
    }

    public void setLastLogintime(Date lastLogintime) {
        this.lastLogintime = lastLogintime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public Date getRegtime() {
        return regtime;
    }

    public void setRegtime(Date regtime) {
        this.regtime = regtime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public User(String username, String password, String nickname, Date regtime) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.gender = null;
        this.motto = "学无止境，乐在其中";
        this.experience = 100L;
        this.role = "user";
        this.profilePath = "nologin.png";
        this.lastLogintime = null;
        this.regtime = regtime;
        this.status = 1;
    }*/

    public User() {
    }
}
