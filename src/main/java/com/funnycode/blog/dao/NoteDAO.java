package com.funnycode.blog.dao;

import com.funnycode.blog.model.Note;
import com.funnycode.blog.model.NoteType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author CC
 * @date 2019-09-20 23:39
 */
@Mapper
public interface NoteDAO {

    String TABLE_NAME = "note";
    String INSERT_FIELDS = " title, type, user_id, create_time, status, content, comment_cnt ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 创建手记
     * @param note 手记
     * @return 影响行数
     */
    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " VALUES(#{title}, #{type}, #{userId}, #{createTime}, #{status}, #{content}, #{commentCnt})"})
    int add(Note note);

    /**
     * 更新手记
     * @param note 手记
     * @return 影响行数
     */
    int updateByNote(Note note);

    /**
     * 获取手记类型
     * @return 手记类型列表
     */
    @Select({"SELECT type_id, type_name FROM note_type"})
    List<NoteType> findAllType();

    /**
     * 获取某一手记类型
     * @param typeId 类型id
     * @return 手记类型
     */
    @Select({"SELECT type_name FROM note_type WHERE type_id=#{typeId}"})
    String getTypeByTypeId(int typeId);

    /**
     * 获取手记
     * @param noteId 手记id
     * @return 手记
     */
    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " WHERE id = #{noteId}"})
    Note getById(long noteId);

    /**
     * 获取部分手记
     * @param offset 偏移量
     * @param limit 手记数
     * @return 手记列表
     */
    List<Note> findLimit(int offset, int limit);

    /**
     * 获取用户部分手记
     * @param userId 用户id
     * @param offset 偏移量
     * @param limit 手记数
     * @return 手记列表
     */
    List<Note> findLimitByUserId(Long userId, int offset, int limit);

    /**
     * 获取用户已发布手记数
     * @param userId 用户id
     * @return 手记数
     */
    @Select({"SELECT COUNT(*) FROM ", TABLE_NAME, " WHERE user_id=#{userId} AND status=1"})
    int countByUserId(long userId);

    /**
     * 获取已发布手记数
     * @return 手记数
     */
    @Select({"SELECT COUNT(*) FROM ", TABLE_NAME, " WHERE status=1"})
    int countAll();

    /**
     * 更新手记评论数
     * @param noteId 手记id
     * @param offset 更新数
     * @return 影响行数
     */
    @Update({"UPDATE ", TABLE_NAME, " SET comment_cnt=comment_cnt+#{offset} WHERE id=#{noteId}"})
    int updateCommentCntById(long noteId, long offset);

    /**
     * 获取用户某一状态的所有手记
     * @param userId 用户id
     * @param status 状态
     * @param offset 偏移量
     * @param limit 手记数
     * @return 手记列表
     */
    List<Note> findAllByUserIdAndStatus(Long userId, Integer status, int offset, int limit);

    /**
     * 更新手记状态
     * @param userId 用户id
     * @param noteId 手记id
     * @param status 状态
     * @param expect 期望状态
     * @return 影响行数
     */
    @Update({"UPDATE ", TABLE_NAME, " SET status=#{status} WHERE id=#{noteId} AND user_id=#{userId} AND status=#{expect}"})
    int updateStatus(Long userId, Long noteId, Integer status, Integer expect);
}

