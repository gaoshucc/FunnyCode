package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.CommentDAO;
import com.funnycode.blog.model.Code;
import com.funnycode.blog.model.Comment;
import com.funnycode.blog.model.EntityType;
import com.funnycode.blog.model.vo.CommentVO;
import com.funnycode.blog.service.CommentService;
import com.funnycode.blog.service.FeedService;
import com.funnycode.blog.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author CC
 * @date 2019-10-03 20:35
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private NoteService noteService;

    @Autowired
    private FeedService feedService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addComment(Comment comment) {
        //todo 对于不同类型的实体，需要进行不同的增加评论数
        if(commentDAO.add(comment) > 0){
            if(comment.getEntityType() == EntityType.ENTITY_NOTE){
                return noteService.addNoteComentCnt(comment.getEntityId());
            }else if (comment.getEntityType() == EntityType.ENTITY_FEED){
                return feedService.addFeedCommentCnt(comment.getEntityId());
            }
            throw new RuntimeException("增加评论错误");
        }

        throw new RuntimeException("增加评论错误");
    }

    @Override
    public boolean removeAllComment(Integer entityType, Long entityId) {
        return commentDAO.removeAllByEntity(entityType, entityId) > 0;
    }

    @Override
    public Comment getCommentById(long id) {
        return commentDAO.getById(id);
    }

    @Override
    public List<CommentVO> selectCommentsByEntity(int entityType, long entityId, long id) {
        return commentDAO.findAllByEntityAndParentId(entityType, entityId, id);
    }

    @Override
    public int getCommentCount(int entityType, long entityId, int status) {
        return commentDAO.getCommentCount(entityType, entityId, status);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateCommentDeleted(Comment comment) {
        //todo 对于不同类型的实体，需要进行不同的减少评论数
        if(commentDAO.updateStatusById(comment.getId(), Code.COMMENT_HASDELETE, Code.DELETE_COMMENT) > 0){
            if(comment.getEntityType() == EntityType.ENTITY_NOTE){
                return noteService.minusNoteComentCnt(comment.getEntityId());
            }else if(comment.getEntityType() == EntityType.ENTITY_FEED){
                return feedService.minusFeedCommentCnt(comment.getEntityId());
            }
            throw new RuntimeException("删除评论错误");
        }

        throw new RuntimeException("删除评论错误");
    }
}
