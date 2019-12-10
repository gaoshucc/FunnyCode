package com.funnycode.blog.model;

/**
 * @author CC
 * @date 2019-10-26 19:09
 */
public class AccountBinding {
    private Long userId;
    private String thirdId;
    private Integer type;

    public AccountBinding() {
    }

    public AccountBinding(Long userId, String thirdId, Integer type) {
        this.userId = userId;
        this.thirdId = thirdId;
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getThirdId() {
        return thirdId;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
