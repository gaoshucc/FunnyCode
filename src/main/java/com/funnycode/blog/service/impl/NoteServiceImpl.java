package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.NoteDAO;
import com.funnycode.blog.model.Note;
import com.funnycode.blog.model.NoteType;
import com.funnycode.blog.service.NoteService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.JedisAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author CC
 * @date 2019-09-20 23:39
 */
@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteDAO noteDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public int addNote(Note note) {
        userService.updateUserExperience(note.getUserId(), 100);
        return noteDAO.add(note);
    }

    @Override
    public int updateNote(Note note) {
        return noteDAO.updateByNote(note);
    }

    @Override
    public List<NoteType> getNoteType() {
        return noteDAO.findAllType();
    }

    @Override
    public String getNoteTypeById(int typeId){
        return noteDAO.getTypeByTypeId(typeId);
    }

    @Override
    public Note getNote(long noteId) {
        return noteDAO.getById(noteId);
    }

    @Override
    public List<Note> findNotes(int offset, int limit) {
        return noteDAO.findLimit(offset, limit);
    }

    @Override
    public List<Note> findUserAllNotes(Long userId, int offset, int limit) {
        return noteDAO.findLimitByUserId(userId, offset, limit);
    }

    @Override
    public int getNoteCount(long userId) {
        return noteDAO.countByUserId(userId);
    }

    @Override
    public int getNotesCount() {
        return noteDAO.countAll();
    }

    @Override
    public boolean addNoteComentCnt(long noteId) {
        return noteDAO.updateCommentCntById(noteId, 1L) > 0;
    }

    @Override
    public boolean minusNoteComentCnt(long noteId) {
        return noteDAO.updateCommentCntById(noteId, -1L) > 0;
    }

    @Override
    public List<Note> listUserNotesByStatus(Long userId, Integer status, int offset, int limit) {
        return noteDAO.findAllByUserIdAndStatus(userId, status, offset, limit);
    }

    @Override
    public int updateNoteStatus(Long userId, Long noteId, Integer status, Integer expect){
        return noteDAO.updateStatus(userId, noteId, status, expect);
    }

    @Override
    public int publishSavedNote(Note note) {
        userService.updateUserExperience(note.getUserId(), 100);
        return noteDAO.updateByNote(note);
    }

    @Override
    public List<Long> getHotNotes(String key, int offset, int limit) {
        return getIdsFromSet(jedisAdapter.zrevrange(key, offset, limit-1));
    }

    @Override
    public List<Long> getIdsFromSet(Set<String> idset) {
        if(idset == null || idset.size() <= 0){
            return null;
        }
        List<Long> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Long.parseLong(str));
        }
        return ids;
    }

}
