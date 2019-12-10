package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventProducer;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.*;
import com.funnycode.blog.model.VO.CommentVO;
import com.funnycode.blog.service.CommentService;
import com.funnycode.blog.service.NoteService;
import com.funnycode.blog.service.SensitiveService;
import com.funnycode.blog.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CC
 * @date 2019-10-03 20:55
 */
@Valid
@Controller
public class CommentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private NoteService noteService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SensitiveService sensitiveService;

    @GetMapping("/user/note/reply/{noteId}/{bereplyId}")
    public String toNoteReply(Model model, @PathVariable("noteId") Long noteId,
                          @PathVariable("bereplyId")Long bereplyId){
        model.addAttribute("noteId", noteId);
        model.addAttribute("bereplyId", bereplyId);
        return "commons/reply";
    }

    @GetMapping("/user/feed/reply/{feedId}/{bereplyId}")
    public String toFeedReply(Model model, @PathVariable("feedId") Long feedId,
                          @PathVariable("bereplyId")Long bereplyId){
        model.addAttribute("feedId", feedId);
        model.addAttribute("bereplyId", bereplyId);
        return "commons/feed-reply";
    }

    @PostMapping("/user/note/comment")
    @ResponseBody
    public Result addComment(@RequestParam("noteId") Long noteId,
                             @RequestParam("content") @NotBlank(message = "评论内容不能为空") String content,
                             @RequestParam("parentId")Long parentId){
        Note note = noteService.getNote(noteId);
        if(note == null || note.getStatus() != Code.PUBLISH){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        Comment comment = new Comment(hostHolder.getUser().getUserId(),
                EntityType.ENTITY_NOTE, noteId, sensitiveService.filter(content), new Date(), 1, parentId);
        boolean ret = commentService.addComment(comment);
        if(ret){
            if(!note.getUserId().equals(hostHolder.getUser().getUserId())){
                eventProducer.sendEvent(new EventModel().setActorId(hostHolder.getUser().getUserId())
                        .setEntityType(EntityType.ENTITY_NOTE).setEntityId(noteId).setEntityOwnerId(note.getUserId())
                        .setType(EventType.COMMENT));
            }

            return ResultUtil.success();
        }else{
            return ResultUtil.error(ExceptionEnum.SER_EOR);
        }
    }

    @GetMapping("/note/commentcnt")
    @ResponseBody
    public Result getCommentCount(@RequestParam("noteId")Long noteId){
        int commentCnt = commentService.getCommentCount(EntityType.ENTITY_NOTE, noteId, Code.NORMAL_COMMENT);
        Map<String, Object> map = new HashMap<>();
        map.put("commentCnt", commentCnt);

        return ResultUtil.success(map);
    }

    @GetMapping("/note/comments")
    @ResponseBody
    public String getNoteComments(@RequestParam("noteId")Long noteId){
        List<CommentVO> comments = commentService.selectCommentsByEntity(EntityType.ENTITY_NOTE, noteId, 0);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", comments);

        return JSON.toJSONString(ResultUtil.success(map), SerializerFeature.DisableCircularReferenceDetect);
    }

    @PostMapping("/user/comment/delete")
    @ResponseBody
    public Result deleteComment(@RequestParam("commentId")Long commentId){
        Comment comment = commentService.getCommentById(commentId);
        if(comment == null || !isCommentOwner(comment)){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        boolean ret = commentService.updateCommentDeleted(comment);
        if(ret){
            return ResultUtil.success();
        }else{
            return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }
    }

    private boolean isCommentOwner(Comment comment){
        int status = -1;
        if(comment.getUserId() == hostHolder.getUser().getUserId()){
            status = 1;
        }

        return status == 1;
    }
}
