package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.funnycode.blog.model.*;
import com.funnycode.blog.model.VO.BriefNoteVO;
import com.funnycode.blog.model.VO.HomeNoteVO;
import com.funnycode.blog.model.VO.NoteVO;
import com.funnycode.blog.service.NoteService;
import com.funnycode.blog.service.SensitiveService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.JedisAdapter;
import com.funnycode.blog.util.RedisKeyUtil;
import com.funnycode.blog.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author CC
 * @date 2019-10-01 20:42
 */
@Valid
@Controller
public class NoteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final String NAMESPACE = "/user";

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private SensitiveService sensitiveService;

    @GetMapping("/notes/count")
    @ResponseBody
    public String getNotesCount(){
        Map<String, Object> map = new HashMap<>();
        map.put("count", noteService.getNotesCount());

        return JSON.toJSONString(ResultUtil.success(map));
    }

    @GetMapping("/notes")
    @ResponseBody
    public String findNotes(@RequestParam(value = "offset", defaultValue = "0") int offset,
                            @RequestParam(value = "limit", defaultValue = "10") int limit){
        List<Note> notes = noteService.findNotes(offset, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("notes", transferToHomeNoteVO(notes));

        return JSON.toJSONString(ResultUtil.success(map), SerializerFeature.DisableCircularReferenceDetect);
    }

    private List<HomeNoteVO> transferToHomeNoteVO(List<Note> notes){
        String key = RedisKeyUtil.getVisitKey(EntityType.ENTITY_NOTE);
        List<HomeNoteVO> homeNoteVOS = new ArrayList<>();
        for(Note note: notes){
            HomeNoteVO homeNoteVO = new HomeNoteVO(note.getId(), note.getTitle(), 
                    noteService.getNoteTypeById(note.getType()), 
                    note.getCreateTime(), note.getContent(),
                    jedisAdapter.zscore(key, String.valueOf(note.getId())) == null ? 0 : jedisAdapter.zscore(key, String.valueOf(note.getId())),
                    note.getCommentCnt(), note.getUserId(),
                    userService.getNicknameById(note.getUserId()));
            homeNoteVOS.add(homeNoteVO);
        }
        
        return homeNoteVOS;
    }

    @GetMapping("/notes/hot")
    @ResponseBody
    public Result getHotNotes(@RequestParam(value = "offset", defaultValue = "0") int offset,
                              @RequestParam(value = "limit", defaultValue = "10") int limit){
        String key = RedisKeyUtil.getVisitKey(EntityType.ENTITY_NOTE);
        List<Long> noteIds = noteService.getHotNotes(key, offset, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("notes", transferToBriefNoteVO(noteIds));

        return ResultUtil.success(map);
    }

    private List<BriefNoteVO> transferToBriefNoteVO(List<Long> noteIds){
        if(noteIds == null || noteIds.size() <= 0){
            return null;
        }
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

    @GetMapping(NAMESPACE + "/mynotes")
    public String notes(){
        return "notes";
    }

    @GetMapping(NAMESPACE + "/mynotes/{status}")
    @ResponseBody
    public String allMyNotes(@PathVariable("status") Integer status,
                             @RequestParam(value = "offset", defaultValue = "0") int offset,
                             @RequestParam(value = "limit", defaultValue = "10") int limit){
        List<Note> notes = noteService.listUserNotesByStatus(hostHolder.getUser().getUserId(), status, offset, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("notes", transferToHomeNoteVO(notes));

        return JSON.toJSONString(ResultUtil.success(map), SerializerFeature.DisableCircularReferenceDetect);
    }

    @GetMapping(NAMESPACE + "/note")
    public String noting(){
        return "noting";
    }

    @GetMapping("/note/{noteId}")
    public String notedetail(Model model, @PathVariable("noteId") @NotBlank Long noteId){
        if(noteId != null && noteId > 0){
            Note note = userService.getNote(noteId);
            if(note == null){
                return "commons/error";
            }
            if(note.getStatus() == Code.SAVE){
                return "redirect: /user/note/edit/" + note.getId();
            }
            if(note.getStatus() == Code.PUBLISH){
                //增加浏览量
                String key = RedisKeyUtil.getVisitKey(EntityType.ENTITY_NOTE);
                jedisAdapter.zincrby(key, 1, String.valueOf(noteId));
            }

            NoteVO noteVO = new NoteVO(note.getId(), note.getTitle(),
                    noteService.getNoteTypeById(note.getType()),
                    note.getCreateTime(), note.getContent(), note.getCommentCnt());
            User author = userService.getUserByUserId(note.getUserId());
            model.addAttribute("userId", hostHolder.getUser()!=null ? hostHolder.getUser().getUserId() : -1);
            model.addAttribute("note", noteVO);
            model.addAttribute("authorId", author.getUserId());
            model.addAttribute("authorNickname", author.getNickname());
            model.addAttribute("authorProfile", author.getProfilePath());
            model.addAttribute("authorExperience", author.getExperience());

            return "note";
        }else{
            return "commons/error";
        }

    }

    @GetMapping("/note/count")
    @ResponseBody
    public String getNoteCount(@RequestParam("userId") @NotNull long userId){
        int notecnt = noteService.getNoteCount(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("notecnt", notecnt);
        Result result = ResultUtil.success(map);

        return JSON.toJSONString(result);
    }

    @PostMapping(NAMESPACE + "/note/publish")
    @ResponseBody
    public Result publishNote(@RequestParam("title") @NotBlank(message = "标题不能为空") String title,
                              @RequestParam("typeId") @NotBlank(message = "类型不能为空") Integer typeId,
                              @RequestParam("markdownDoc") @NotBlank(message = "手记内容不能为空") String content) {
        Result result;
        long userId = hostHolder.getUser().getUserId();
        Note note = new Note(sensitiveService.filter(title), typeId, userId, new Date(), Code.PUBLISH, sensitiveService.filter(content), 0);
        int ret = noteService.addNote(note);
        if(ret > 0){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }

        return result;
    }

    @PostMapping(NAMESPACE + "/note/save")
    @ResponseBody
    public Result saveNote(@RequestParam("title") @NotBlank(message = "标题不能为空") String title,
                           @RequestParam("typeId") @NotBlank(message = "类型不能为空") Integer typeId,
                           @RequestParam("markdownDoc") @NotBlank(message = "手记内容不能为空") String content) {
        Result result;
        long userId = hostHolder.getUser().getUserId();
        Note note = new Note(sensitiveService.filter(title), typeId, userId, new Date(), Code.SAVE, sensitiveService.filter(content), 0);
        int ret = noteService.addNote(note);
        if(ret > 0){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }

        return result;
    }

    @PostMapping(NAMESPACE + "/note/delete")
    @ResponseBody
    public Result deletePublishedNote(@RequestParam("noteId") Long noteId){
        int ret = noteService.updateNoteStatus(hostHolder.getUser().getUserId(), noteId, 0, 1);
        Result result;
        if(ret > 0){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        return result;
    }

    @GetMapping(NAMESPACE + "/note/edit/{noteId}")
    public String editNote(@PathVariable("noteId") Long noteId, Model model){
        Note note = noteService.getNote(noteId);
        if(note.getUserId().equals(hostHolder.getUser().getUserId())){
            model.addAttribute("note", note);
        }else{
            return "commons/error";
        }
        return "editing";
    }


    @PostMapping(NAMESPACE + "/note/edit/save")
    @ResponseBody
    public String editSave(@RequestParam("noteId") Long noteId,
                           @RequestParam("title") String title,
                           @RequestParam("typeId") Integer typeId,
                           @RequestParam("markdownDoc") String content){
        Note note = new Note(noteId, sensitiveService.filter(title), typeId, hostHolder.getUser().getUserId(), new Date(), sensitiveService.filter(content));
        int ret = noteService.updateNote(note);
        Result result;
        if(ret > 0){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        return JSON.toJSONString(result);
    }

    @PostMapping(NAMESPACE + "/note/edit/publish")
    @ResponseBody
    public String editPublish(@RequestParam("noteId") Long noteId,
                           @RequestParam("title") String title,
                           @RequestParam("typeId") Integer typeId,
                           @RequestParam("markdownDoc") String content){
        Result result;
        if(noteService.getNote(noteId).getStatus() == Code.SAVE){
            Note note = new Note(noteId, sensitiveService.filter(title), typeId, hostHolder.getUser().getUserId(), new Date(), sensitiveService.filter(content));
            note.setStatus(Code.PUBLISH);
            int ret = noteService.publishSavedNote(note);
            if(ret > 0){
                result = ResultUtil.success();
            }else{
                result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
            }
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        return JSON.toJSONString(result);
    }

    @PostMapping(NAMESPACE + "/note/deletesaved")
    @ResponseBody
    public String deleteSavedNote(@RequestParam("noteId") Long noteId){
        int ret = noteService.updateNoteStatus(hostHolder.getUser().getUserId(), noteId, -1, 2);
        Result result;
        if(ret > 0){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        return JSON.toJSONString(result);
    }

    @PostMapping(NAMESPACE + "/note/deletecompletely")
    @ResponseBody
    public String deleteNoteCompletely(@RequestParam("noteId") Long noteId){
        int ret = noteService.updateNoteStatus(hostHolder.getUser().getUserId(), noteId, -1, 0);
        Result result;
        if(ret > 0){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        return JSON.toJSONString(result);
    }

    @PostMapping(NAMESPACE + "/note/restore")
    @ResponseBody
    public String restoreNote(@RequestParam("noteId") Long noteId){
        int ret = noteService.updateNoteStatus(hostHolder.getUser().getUserId(), noteId, 1, 0);
        Result result;
        if(ret > 0){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        return JSON.toJSONString(result);
    }

    @GetMapping(NAMESPACE + "/note/type")
    @ResponseBody
    public String getNoteType(){
        Result result = userService.getNoteType();
        return JSON.toJSONString(result);
    }
}
