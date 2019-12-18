package com.funnycode.blog.model.vo;

/**
 * @author CC
 * @date 2019-10-28 10:27
 */
public class EditableAccountUserVO {
    private Long userId;
    private String profilePath;
    private String nickname;
    private String email;
    private String motto;
    private Integer gender;

    public EditableAccountUserVO() {
    }

    public EditableAccountUserVO(Long userId, String profilePath, String nickname, String email, String motto, Integer gender) {
        this.userId = userId;
        this.profilePath = profilePath;
        this.nickname = nickname;
        this.email = email;
        this.motto = motto;
        this.gender = gender;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
}
