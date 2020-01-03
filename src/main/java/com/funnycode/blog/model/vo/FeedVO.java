package com.funnycode.blog.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author CC
 * @date 2019-11-03 18:07
 */
@Getter
@Setter
public class FeedVO {
    private Long id;
    private Integer attachmentType;
    private Integer type;
    private UserVO user;
    private Date createdDate;
    private Long forwordCnt;
    private Long commentCnt;
    private Long likeCnt;
    private Boolean forwordState;
    private Boolean likeState;
    private String data;
}
