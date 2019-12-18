package com.funnycode.blog.dao;

import com.funnycode.blog.model.Comment;
import com.funnycode.blog.model.vo.CommentVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author CC
 * @date 2019-10-03 15:37
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, entity_type, entity_id, content, create_time, status, parent_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 创建评论
     * @param comment 评论
     * @return 影响行数
     */
    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " VALUES(#{userId}, #{entityType}, #{entityId}, #{content}, #{createTime}, #{status}, #{parentId})"})
    int add(Comment comment);

    /**
     * 获取评论
     * @param id 评论编号
     * @return 评论
     */
    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " WHERE id = #{id}"})
    Comment getById(long id);

    /**
     * 获取评论列表
     * @param entityType 实体类型
     * @param entityId 实体编号
     * @param id 父评论编号
     * @return 评论列表
     */
    List<CommentVO> findAllByEntityAndParentId(@Param("entityType") int entityType, @Param("entityId") long entityId, @Param("id")long id);

    /**
     * 获取评论数
     * @param entityType 实体类型
     * @param entityId 实体编号
     * @param status 评论状态
     * @return 评论数
     */
    @Select({"SELECT COUNT(*) FROM ", TABLE_NAME, " WHERE entity_type=#{entityType} AND entity_id=#{entityId} AND status=#{status}"})
    int getCommentCount(@Param("entityType") int entityType, @Param("entityId") long entityId, int status);

    /**
     * 更新评论为已删除
     * @param id 评论编号
     * @param content 评论已删除内容提示
     * @param status 评论状态
     * @return 影响行数
     */
    @Update({"UPDATE ", TABLE_NAME, " SET content=#{content}, status=#{status} WHERE id=#{id} "})
    int updateStatusById(@Param("id") long id, @Param("content")String content, int status);

    /**
     * 删除某个实体的所有评论
     * @param entityType 实体类型
     * @param entityId 实体编号
     * @return 影响行数
     */
    @Delete({"delete from ", TABLE_NAME, " where entity_type=#{entityType} AND entity_id=#{entityId}"})
    int removeAllByEntity(Integer entityType, Long entityId);
}
