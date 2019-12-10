package com.funnycode.blog.service;

import com.funnycode.blog.model.Comment;
import com.funnycode.blog.model.VO.CommentVO;

import java.util.List;

/**
 * @author CC
 * @date 2019-10-03 20:14
 */
public interface CommentService {
    boolean addComment(Comment comment);

    boolean removeAllComment(Integer entityType, Long entityId);

    Comment getCommentById(long id);

    List<CommentVO> selectCommentsByEntity(int entityType, long entityId, long id);

    int getCommentCount(int entityType, long entityId, int status);

    boolean updateCommentDeleted(Comment comment);
}
