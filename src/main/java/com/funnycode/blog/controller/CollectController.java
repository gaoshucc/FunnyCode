package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventProducer;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.*;
import com.funnycode.blog.model.VO.BriefNoteVO;
import com.funnycode.blog.model.VO.FavoriteNoteVO;
import com.funnycode.blog.service.CollectService;
import com.funnycode.blog.service.NoteService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.*;

/**
 * @author CC
 * @date 2019-10-02 16:24
 */
@Valid
@Controller
public class CollectController {

    @Autowired
    private CollectService collectService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @GetMapping("/user/favorite")
    public String toFavorites(){
        return "favorites";
    }

    @GetMapping("/user/favorite/count")
    @ResponseBody
    public String getFavoritesCount(){
        long count = collectService.getCollectCount(hostHolder.getUser().getUserId(), EntityType.ENTITY_NOTE);
        Map<String, Object> map = new HashMap<>();
        map.put("count", count);

        return JSON.toJSONString(ResultUtil.success(map));
    }

    @GetMapping("/user/favorite/brief")
    @ResponseBody
    public String getFavoriteBrief(@RequestParam(value = "offset", defaultValue = "0")Integer offset,
                                   @RequestParam(value = "limit", defaultValue = "6")Integer limit){
        List<Long> noteIds = collectService.getCollectBrief(hostHolder.getUser().getUserId(), EntityType.ENTITY_NOTE, offset, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("notes", transferToBriefNoteVO(noteIds));

        return JSON.toJSONString(ResultUtil.success(map), SerializerFeature.DisableCircularReferenceDetect);
    }

    private List<BriefNoteVO> transferToBriefNoteVO(List<Long> noteIds){
        List<Note> notes = new ArrayList<>();
        for(Long noteId: noteIds){
            notes.add(noteService.getNote(noteId));
        }
        List<BriefNoteVO> briefNoteVOS = new ArrayList<>();
        for(Note note: notes){
            BriefNoteVO briefNoteVO = new BriefNoteVO(note.getId(), note.getTitle(),
                    userService.getNicknameById(note.getUserId()));
            briefNoteVOS.add(briefNoteVO);
        }

        return briefNoteVOS;
    }

    @GetMapping("/user/favorite/list")
    @ResponseBody
    public String getFavoriteList(@RequestParam(value = "offset", defaultValue = "0")Integer offset,
                                  @RequestParam(value = "limit", defaultValue = "8")Integer limit){
        Map<Long, Double> noteIds = collectService.getCollectList(hostHolder.getUser().getUserId(), EntityType.ENTITY_NOTE, offset, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("notes", transferToFavoriteNoteVO(noteIds));

        return JSON.toJSONString(ResultUtil.success(map), SerializerFeature.DisableCircularReferenceDetect);
    }

    private List<FavoriteNoteVO> transferToFavoriteNoteVO(Map<Long, Double> noteIds){
        List<Note> notes = new ArrayList<>();
        for(Long noteId: noteIds.keySet()){
            notes.add(noteService.getNote(noteId));
        }
        List<FavoriteNoteVO> favoriteNoteVOS = new ArrayList<>();
        for(Note note: notes){
            User author = userService.getUserByUserId(note.getUserId());
            FavoriteNoteVO favoriteNoteVO = new FavoriteNoteVO(note.getId(), note.getTitle(),
                    noteService.getNoteTypeById(note.getType()), note.getCreateTime(), note.getCommentCnt(),
                    author.getUserId(), author.getNickname(), author.getProfilePath(), new Date(noteIds.get(note.getId()).longValue()));
            favoriteNoteVOS.add(favoriteNoteVO);
        }

        return favoriteNoteVOS;
    }

    @GetMapping("/note/collectstatus")
    @ResponseBody
    public String getCollectStatus(@RequestParam("noteId") long noteId){
        User user = hostHolder.getUser();
        int collectStatus = -1;
        if(user != null){
            collectStatus = collectService.getCollectStatus(user.getUserId(), EntityType.ENTITY_NOTE, noteId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("collectStatus", collectStatus);
        Result result = ResultUtil.success(map);

        return JSON.toJSONString(result);
    }

    @PostMapping("/user/collect/note")
    @ResponseBody
    public Result collectNote(@RequestParam("noteId") long noteId){
        User user = hostHolder.getUser();
        Note note = noteService.getNote(noteId);
        if(note == null || note.getStatus() != Code.PUBLISH){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        if(!checkCollectStatus(EntityType.ENTITY_NOTE, noteId) && !user.getUserId().equals(note.getUserId())){
            eventProducer.sendEvent(new EventModel(EventType.COLLECT)
                    .setActorId(user.getUserId()).setEntityId(noteId)
                    .setEntityType(EntityType.ENTITY_NOTE).setEntityOwnerId(note.getUserId())
            );
        }

        int collectStatus = collectService.collect(hostHolder.getUser().getUserId(), EntityType.ENTITY_NOTE, noteId);
        Map<String, Object> map = new HashMap<>();
        map.put("collectStatus", collectStatus);

        return ResultUtil.success(map);
    }

    private boolean checkCollectStatus(int entityType, long noteId) {
        int status = collectService.getCollectStatus(hostHolder.getUser().getUserId(), entityType, noteId);

        return status == 1;
    }
}
