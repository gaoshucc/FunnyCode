package com.funnycode.blog.model.VO;

/**
 * @author CC
 * @date 2019-10-07 17:03
 */
public class PopupUserVO {
    private Long userId;
    private String nickname;
    private String profilePath;
    private Long experience;
    private Integer noteCnt;
    private Long followerCnt;
    private String motto;
    private Integer gender;

    public PopupUserVO() {
    }

    public PopupUserVO(Long userId, String nickname, String profilePath, Long experience, Integer noteCnt, Long followerCnt, String motto, Integer gender) {
        this.userId = userId;
        this.nickname = nickname;
        this.profilePath = profilePath;
        this.experience = experience;
        this.noteCnt = noteCnt;
        this.followerCnt = followerCnt;
        this.motto = motto;
        this.gender = gender;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }

    public Integer getNoteCnt() {
        return noteCnt;
    }

    public void setNoteCnt(Integer noteCnt) {
        this.noteCnt = noteCnt;
    }

    public Long getFollowerCnt() {
        return followerCnt;
    }

    public void setFollowerCnt(Long followerCnt) {
        this.followerCnt = followerCnt;
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
