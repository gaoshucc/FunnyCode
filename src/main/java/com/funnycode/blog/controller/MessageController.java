package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.funnycode.blog.model.*;
import com.funnycode.blog.model.vo.*;
import com.funnycode.blog.service.MessageService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author CC
 * @date 2019-09-24 00:03
 */
@Valid
@Controller
public class MessageController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/pluser")
    @ResponseBody
    public Result getPluser(@RequestParam("nickname") String nickname){
        List<UserVO> users = userService.getUserVOSByNickname(nickname, hostHolder.getUser().getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("users", transferToPLUserVO(users));

        return ResultUtil.success(map);
    }

    private List<PLUserVO> transferToPLUserVO(List<UserVO> users){
        User user = hostHolder.getUser();
        List<PLUserVO> plUserVOS = new ArrayList<>();
        PLUserVO plUserVO;
        UserVO toUser;
        for(int i=0, len=users.size(); i<len; i++){
            toUser = users.get(i);
            String conversationId;
            if (user.getUserId() < toUser.getUserId()) {
                conversationId = String.format("%d_%d", user.getUserId(), toUser.getUserId());
            } else {
                conversationId = String.format("%d_%d", toUser.getUserId(), user.getUserId());
            }
            plUserVO = new PLUserVO();
            plUserVO.setConversationId(conversationId);
            plUserVO.setProfilePath(toUser.getProfilePath());
            plUserVO.setNickname(toUser.getNickname());
            plUserVOS.add(plUserVO);
        }

        return plUserVOS;
    }

    @PostMapping("/user/message/send")
    @ResponseBody
    public Result addMessage(@RequestParam("conversationId") @NotBlank(message = "对话不存在") String conversationId,
                             @RequestParam("content") @NotBlank(message = "对话消息不能为空") String content){
        User user = hostHolder.getUser();
        //获取toId，并判断对话是否属于该用户
        String[] ids = conversationId.split("_");
        long id1 = Long.parseLong(ids[0]), id2 = Long.parseLong(ids[1]),
             toId;
        if(id1 == user.getUserId()){
            toId = id2;
        }else if(id2 == user.getUserId()){
            toId = id1;
        }else{
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        User toUser = userService.getUserByUserId(toId);
        if(toUser == null || toUser.getStatus() == Code.LOGOUT){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        //生成消息
        Message message = new Message();
        message.setFromId(user.getUserId());
        message.setToId(toId);
        message.setContent(content);
        message.setCreatedDate(new Date());
        message.setHasRead(0);
        message.setType(Code.MSG_PM);
        boolean ret = messageService.addMessage(message);
        if(ret){
            return ResultUtil.success();
        }else{
            return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }
    }

    @GetMapping("/user/conversations")
    @ResponseBody
    public Result getConversations(){
        User user = hostHolder.getUser();
        List<Message> messageList = messageService.getConversationList(user.getUserId(), 0, Integer.MAX_VALUE);
        Map<String, Object> map = new HashMap<>();
        map.put("conversations", transferToMessageUserVO(messageList));

        return ResultUtil.success(map);
    }

    private List<MessageUserVO> transferToMessageUserVO(List<Message> messageList) {
        User user = hostHolder.getUser();
        List<MessageUserVO> messageUserVOS = new ArrayList<>();
        User pluser;
        Message message;
        MessageUserVO messageUserVO;
        for(int i=0, len=messageList.size(); i<len; i++){
            message = messageList.get(i);
            long userId = message.getFromId() == user.getUserId() ? message.getToId() : message.getFromId();
            pluser = userService.getUserByUserId(userId);

            messageUserVO = new MessageUserVO();
            messageUserVO.setConversationId(message.getConversationId());
            messageUserVO.setProfilePath(pluser.getProfilePath());
            messageUserVO.setNickname(pluser.getNickname());
            messageUserVO.setMessage(message.getContent());
            messageUserVO.setUnreadCnt(messageService.getConversationUnreadCount(user.getUserId(), message.getConversationId()));
            messageUserVOS.add(messageUserVO);
        }

        return messageUserVOS;
    }

    @GetMapping("/user/conversations/unread")
    @ResponseBody
    public Result getMessageUnreadCnt(@RequestParam("type") Integer type){
        User user = hostHolder.getUser();
        Map<String, Object> map = new HashMap<>();
        if(type == Code.MSG_SYS){
            map.put("unread", messageService.getMessageUnreadCount(Code.SYSMSG_TOID, type));
        }else{
            map.put("unread", messageService.getMessageUnreadCount(user.getUserId(), type));
        }

        return ResultUtil.success(map);
    }

    /*@GetMapping("/user/conversations/unread")
    @ResponseBody
    public Result getMessageUnreadCnt(){
        User user = hostHolder.getUser();
        Map<String, Object> map = new HashMap<>();
        map.put("sys", messageService.getMessageUnreadCount(Code.SYSMSG_TOID, Code.MSG_SYS));
        map.put("pm", messageService.getMessageUnreadCount(user.getUserId(), Code.MSG_PM));
        map.put("follow", messageService.getMessageUnreadCount(user.getUserId(), Code.MSG_FOLLOW));
        map.put("like", messageService.getMessageUnreadCount(user.getUserId(), Code.MSG_LIKE));
        map.put("comment", messageService.getMessageUnreadCount(user.getUserId(), Code.MSG_COMMENT));

        return ResultUtil.success(map);
    }*/


    @GetMapping("/user/conversations/allunread")
    @ResponseBody
    public Result getMessageUnreadCntInAll(){
        User user = hostHolder.getUser();
        Map<String, Object> map = new HashMap<>();
        map.put("allunread", messageService.getMessageAllUnreadCount(user.getUserId()));

        return ResultUtil.success(map);
    }

    @PostMapping("/user/message/hasread")
    @ResponseBody
    public Result updateMessageHasread(@RequestParam("type") Integer type){
        int ret = messageService.updateMessageHasread(hostHolder.getUser().getUserId(), type);
        Result result;
        if(ret > 0){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }

        return result;
    }

    @PostMapping("/user/personalmessage/hasread")
    @ResponseBody
    public Result updatePersonalMessageHasread(@RequestParam("conversationId")String conversationId){
        //判断对话是否属于该用户
        User user = hostHolder.getUser();
        String[] ids = conversationId.split("_");
        long id1 = Long.parseLong(ids[0]);
        long id2 = Long.parseLong(ids[1]);
        if(id1 != user.getUserId() && id2 != user.getUserId()){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        messageService.updatePersonalMessageHasread(conversationId, user.getUserId());

        return ResultUtil.success();
    }

    @GetMapping("/user/message/{type}")
    public String toUserMessage(Model model, @PathVariable("type")Integer type){
        model.addAttribute("msgtype", type);
        return "message";
    }

    @GetMapping("/user/message")
    @ResponseBody
    public String getMessage(@RequestParam("type")Integer type,
                             @RequestParam(value = "offset", defaultValue = "0")Integer offset,
                             @RequestParam(value = "limit", defaultValue = "10")Integer limit){
        List<Message> messageList = messageService.listMessageByType(hostHolder.getUser().getUserId(), type, offset, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("msgs", transferToMessageVO(messageList));

        return JSON.toJSONString(ResultUtil.success(map), SerializerFeature.DisableCircularReferenceDetect);
    }

    private List<MessageVO> transferToMessageVO(List<Message> messageList){
        if(messageList == null || messageList.size() <= 0){
            return null;
        }
        List<MessageVO> messageVOS = new ArrayList<>();
        for(Message message: messageList){
            MessageVO messageVO = new MessageVO(message.getId(),
                    userService.getUserVOByUserId(message.getFromId()),
                    message.getContent(), message.getCreatedDate(),
                    message.getHasRead(), message.getType());
            messageVOS.add(messageVO);
        }

        return messageVOS;
    }

    @GetMapping("/user/message/get")
    @ResponseBody
    public Result getMessage(@RequestParam("conversationId")String conversationId,
                             @RequestParam(value = "limit", defaultValue = "6")Integer limit,
                             @RequestParam("firstTime")String firstTime) throws ParseException {
        //判断对话是否属于该用户
        User user = hostHolder.getUser();
        String[] ids = conversationId.split("_");
        long id1 = Long.parseLong(ids[0]);
        long id2 = Long.parseLong(ids[1]);
        if(id1 != user.getUserId() && id2 != user.getUserId()){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        //查询过往消息
        List<Message> messages;
        if(StringUtils.isBlank(firstTime)){
            messages = messageService.getConversationDetailInit(conversationId, 0, limit);
        }else{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            messages = messageService.getConversationDetail(conversationId, 0, limit, format.parse(firstTime));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("messages", transferToPLMessageVO(messages));

        return ResultUtil.success(map);
    }

    @GetMapping("/user/message/get/regular")
    @ResponseBody
    public Result getMessageRegular(@RequestParam("conversationId")String conversationId,
                                    @RequestParam("lastTime")String lastTime) throws ParseException {
        //判断该对话是否属于该用户
        User user = hostHolder.getUser();
        String[] ids = conversationId.split("_");
        long id1 = Long.parseLong(ids[0]);
        long id2 = Long.parseLong(ids[1]);
        if(id1 != user.getUserId() && id2 != user.getUserId()){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        //查询新消息
        List<Message> messages;
        if(StringUtils.isBlank(lastTime)){
            messages = messageService.getConversationDetailInit(conversationId, 0, Integer.MAX_VALUE);
        }else{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            messages = messageService.getConversationDetailRegular(conversationId, format.parse(lastTime));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("messages", transferToPLMessageVO(messages));

        return ResultUtil.success(map);
    }

    private List<PLMessageVO> transferToPLMessageVO(List<Message> messages){
        if(messages == null || messages.size() <= 0){
            return null;
        }
        User user = hostHolder.getUser();
        List<PLMessageVO> messageVOS = new ArrayList<>();
        PLMessageVO messageVO;
        for(Message message: messages){
            messageVO = new PLMessageVO();
            messageVO.setId(message.getId());
            messageVO.setContent(message.getContent());
            messageVO.setCreatedDate(message.getCreatedDate());
            messageVO.setFromUser(userService.getUserVOByUserId(message.getFromId()));
            messageVO.setSelf(message.getFromId() == user.getUserId());

            messageVOS.add(messageVO);
        }

        return messageVOS;
    }
}
