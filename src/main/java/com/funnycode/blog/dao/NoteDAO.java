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

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " VALUES(#{title}, #{type}, #{userId}, #{createTime}, #{status}, #{content}, #{commentCnt})"})
    int addNote(Note note);

    @Update({"UPDATE ", TABLE_NAME,
            " SET title=#{title},type=#{type},create_time=#{createTime},content=#{content}",
        " WHERE id=#{id} AND user_id=#{userId}"})
    int updateNote(Note note);

    @Update({"UPDATE ", TABLE_NAME,
            " SET title=#{title},type=#{type},create_time=#{createTime},status=#{status},content=#{content}",
            " WHERE id=#{id} AND user_id=#{userId}"})
    int publishSavedNote(Note note);

    @Select({"SELECT type_id, type_name FROM note_type"})
    List<NoteType> getNoteType();

    @Select({"SELECT type_name FROM note_type WHERE type_id=#{typeId}"})
    String getNoteTypeById(int typeId);

    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " WHERE id = #{noteId}"})
    Note getNoteById(long noteId);

    List<Note> selectNotes(int offset, int limit);

    List<Note> selectUserNotes(Long userId, int offset, int limit);

    @Select({"SELECT COUNT(*) FROM ", TABLE_NAME, " WHERE user_id=#{userId} AND status=1"})
    int getNoteCount(long userId);

    @Select({"SELECT COUNT(*) FROM ", TABLE_NAME, " WHERE status=1"})
    int getNotesCount();

    @Update({"UPDATE ", TABLE_NAME, " SET comment_cnt=comment_cnt+1 WHERE id=#{noteId}"})
    int addNoteComentCnt(long noteId);

    @Update({"UPDATE ", TABLE_NAME, " SET comment_cnt=comment_cnt-1 WHERE id=#{noteId}"})
    int minusNoteComentCnt(long noteId);

    List<Note> listUserNotesByStatus(Long userId, Integer status, int offset, int limit);

    @Update({"UPDATE ", TABLE_NAME, " SET status=#{status} WHERE id=#{noteId} AND user_id=#{userId} AND status=#{expect}"})
    int updateNoteStatus(Long userId, Long noteId, Integer status, Integer expect);
}

