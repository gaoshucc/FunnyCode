package com.funnycode.blog.service;

import com.funnycode.blog.model.Note;
import com.funnycode.blog.model.NoteType;

import java.util.List;
import java.util.Set;

/**
 * @author CC
 * @date 2019-09-20 23:34
 */
public interface NoteService {

    /**
     * 发布手记
     * @param note 手记
     * @return int
     */
    int addNote(Note note);

    /**
     * 更新手记
     * @param note 手记
     * @return int
     */
    int updateNote(Note note);

    /**
     * 获取手记类型
     * @return List<NoteType> 类型列表
     */
    List<NoteType> getNoteType();

    /**
     * 获取手记类型
     * @return String 类型
     */
    String  getNoteTypeById(int typeId);

    /**
     * 查找手记
     * @param noteId 手记编号
     * @return Note 手记
     */
    Note getNote(long noteId);

    /**
     * 查找手记
     * @param offset 偏移量
     * @param limit 查询数据数
     * @return List<Note>
     */
    List<Note> findNotes(int offset, int limit);

    /**
     * 查找手记
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 查询数据数
     * @return List<Note>
     */
    List<Note> findUserAllNotes(Long userId, int offset, int limit);

    /**
     * 获得手记数
     * @param userId 用户ID
     * @return 手记数
     */
    int getNoteCount(long userId);

    /**
     * 获得手记总数
     * @return 手记数
     */
    int getNotesCount();

    /**
     * 增加手记评论数
     * @param noteId
     */
    boolean addNoteComentCnt(long noteId);

    /**
     * 减少手记评论数
     * @param noteId
     */
    boolean minusNoteComentCnt(long noteId);

    /**
     * 查找手记
     * @param userId 用户ID
     * @param status 手记状态
     * @param offset 偏移量
     * @param limit 查询数据数
     * @return List<Note>
     */
    List<Note> listUserNotesByStatus(Long userId, Integer status, int offset, int limit);

    /**
     * 更新手记状态
     * @param userId 用户Id
     * @param noteId 手记Id
     * @param status 状态
     * @param expect 预期状态
     * @return 影响行数
     */
    int updateNoteStatus(Long userId, Long noteId, Integer status, Integer expect);

    /**
     * 发布已保存的手记
     * @param note 手记
     * @return 影响行数
     */
    int publishSavedNote(Note note);

    /**
     * 获取热门手记
     * @param key 键
     * @param offset 偏移量
     * @param limit 数据条数
     * @return 手记ID列表
     */
    List<Long> getHotNotes(String key, int offset, int limit);

    List<Long> getIdsFromSet(Set<String> idset);
}
