package com.funnycode.blog.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author CC
 * @date 2019-11-03 10:26
 */
@Getter
@Setter
public class Feed {
    private Long id;
    private Long userId;
    private Date createdDate;
    private String content;
    private String attachment;
    private Integer type;
    private Integer attachmentType;
    private Long bindId;
    private Long forwordCnt;
    private Long commentCnt;
}
