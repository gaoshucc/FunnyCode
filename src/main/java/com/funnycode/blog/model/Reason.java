package com.funnycode.blog.model;

/**
 * @author CC
 * @date 2019-10-04 23:21
 */
public class Reason {
    /**
     * 1:色情
     * 2:政治
     * 3:抄袭
     * 4:广告
     * 5:辱骂他人
     */
    private int reasonId;
    private String reason;

    public int getReasonId() {
        return reasonId;
    }

    public void setReasonId(int reasonId) {
        this.reasonId = reasonId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
