package com.funnycode.blog.model.VO;

/**
 * @author CC
 * @date 2019-10-24 15:07
 */
public class AccountUserVO {
    private Long userId;
    private String username;
    private String nickname;
    private String profilePath;
    private String motto;
    private String email;
    private Integer gender;
    private Boolean qqBind;
    private Boolean wechatBind;

    private Long active;
    private Integer noteCnt;
    private Long followeeCnt;
    private Long followerCnt;
    private Long experience;
    private String level;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Long getActive() {
        return active;
    }

    public void setActive(Long active) {
        this.active = active;
    }

    public Integer getNoteCnt() {
        return noteCnt;
    }

    public void setNoteCnt(Integer noteCnt) {
        this.noteCnt = noteCnt;
    }

    public Long getFolloweeCnt() {
        return followeeCnt;
    }

    public void setFolloweeCnt(Long followeeCnt) {
        this.followeeCnt = followeeCnt;
    }

    public Long getFollowerCnt() {
        return followerCnt;
    }

    public void setFollowerCnt(Long followerCnt) {
        this.followerCnt = followerCnt;
    }

    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getQqBind() {
        return qqBind;
    }

    public void setQqBind(Boolean qqBind) {
        this.qqBind = qqBind;
    }

    public Boolean getWechatBind() {
        return wechatBind;
    }

    public void setWechatBind(Boolean wechatBind) {
        this.wechatBind = wechatBind;
    }
}
