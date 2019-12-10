package com.funnycode.blog.controller;

import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventProducer;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.*;
import com.funnycode.blog.service.FeedService;
import com.funnycode.blog.service.LikeService;
import com.funnycode.blog.service.NoteService;
import com.funnycode.blog.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CC
 * @date 2019-10-02 11:07
 */
@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NoteService noteService;

    @Autowired
    private FeedService feedService;

    @Autowired
    EventProducer eventProducer;

    @GetMapping("/note/likecnt")
    @ResponseBody
    public Result getLikeCnt(@RequestParam("noteId")long noteId){
        long likeCnt = likeService.getLikeCount(EntityType.ENTITY_NOTE, noteId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCnt", likeCnt);

        return ResultUtil.success(map);
    }

    @GetMapping("/note/dislikecnt")
    @ResponseBody
    public Result getDislikeCnt(@RequestParam("noteId")long noteId){
        long dislikeCnt = likeService.getDislikeCount(EntityType.ENTITY_NOTE, noteId);
        Map<String, Object> map = new HashMap<>();
        map.put("dislikeCnt", dislikeCnt);

        return ResultUtil.success(map);
    }

    @GetMapping("/note/likestatus")
    @ResponseBody
    public Result getLikeStatus(@RequestParam("noteId")long noteId){
        User user = hostHolder.getUser();
        int status = 0;
        if(user != null){
            status = likeService.getLikeStatus(user.getUserId(), EntityType.ENTITY_NOTE, noteId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        return ResultUtil.success(map);
    }

    private boolean checkLikeStatus(long noteId){
        User user = hostHolder.getUser();
        int status = likeService.getLikeStatus(user.getUserId(), EntityType.ENTITY_NOTE, noteId);

        return status == 1;
    }

    @PostMapping(path = {"/user/like/note"})
    @ResponseBody
    public Result likeNote(@RequestParam("noteId") long noteId) {
        User user = hostHolder.getUser();
        Note note = noteService.getNote(noteId);
        if(note == null || note.getStatus() != Code.PUBLISH){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        if(!checkLikeStatus(noteId) && !user.getUserId().equals(note.getUserId())){
            eventProducer.sendEvent(new EventModel(EventType.LIKE)
                    .setActorId(user.getUserId()).setEntityId(noteId)
                    .setEntityType(EntityType.ENTITY_NOTE).setEntityOwnerId(note.getUserId())
            );
        }

        long likeCount = likeService.like(hostHolder.getUser().getUserId(), EntityType.ENTITY_NOTE, noteId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);

        return ResultUtil.success(map);
    }

    @PostMapping(path = {"/user/dislike/note"})
    @ResponseBody
    public Result dislikeNote(@RequestParam("noteId") long noteId) {
        Note note = noteService.getNote(noteId);
        if(note == null || note.getStatus() != Code.PUBLISH){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        long dislikeCount = likeService.disLike(hostHolder.getUser().getUserId(), EntityType.ENTITY_NOTE, noteId);
        Map<String, Object> map = new HashMap<>();
        map.put("dislikeCount", dislikeCount);

        return ResultUtil.success(map);
    }

    @PostMapping(path = {"/user/like/feed"})
    @ResponseBody
    public Result likeFeed(@RequestParam("feedId") long feedId) {
        User user = hostHolder.getUser();
        Feed feed = feedService.getFeedById(feedId);
        if(feed == null){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        if(!checkLikeFeedStatus(feedId) && !user.getUserId().equals(feed.getUserId())){
            eventProducer.sendEvent(new EventModel(EventType.LIKE)
                    .setActorId(user.getUserId()).setEntityId(feedId)
                    .setEntityType(EntityType.ENTITY_FEED).setEntityOwnerId(feed.getUserId())
            );
        }

        long likeCnt = likeService.like1(hostHolder.getUser().getUserId(), EntityType.ENTITY_FEED, feedId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCnt", likeCnt);

        return ResultUtil.success(map);
    }

    @PostMapping(path = {"/user/dislike/feed"})
    @ResponseBody
    public Result dislikeFeed(@RequestParam("feedId") long feedId) {
        Feed feed = feedService.getFeedById(feedId);
        if(feed == null){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        long likeCnt = likeService.disLike1(hostHolder.getUser().getUserId(), EntityType.ENTITY_FEED, feedId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCnt", likeCnt);

        return ResultUtil.success(map);
    }

    private boolean checkLikeFeedStatus(long feedId){
        User user = hostHolder.getUser();
        int status = likeService.getLikeStatus(user.getUserId(), EntityType.ENTITY_FEED, feedId);

        return status == 1;
    }
}
