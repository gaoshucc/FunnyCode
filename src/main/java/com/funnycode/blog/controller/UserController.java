package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.funnycode.blog.model.*;
import com.funnycode.blog.model.vo.FeedVO;
import com.funnycode.blog.model.vo.HomeNoteVO;
import com.funnycode.blog.model.vo.PHUserVO;
import com.funnycode.blog.model.vo.PopupUserVO;
import com.funnycode.blog.service.*;
import com.funnycode.blog.util.JedisAdapter;
import com.funnycode.blog.util.RedisKeyUtil;
import com.funnycode.blog.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.*;

/**
 * @author CC
 * @date 2019-09-19 21:06
 */
@Valid
@Controller
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final String NAMESPACE = "/user";

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private FollowService followService;

    @GetMapping(NAMESPACE+"/userdetail")
    @ResponseBody
    public Result userDetail(){
        String user = JSON.toJSONString(hostHolder.getUser());
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);

        return ResultUtil.success(map);
    }

    @GetMapping("/popupuser/{userId}")
    @ResponseBody
    public String getPopupUserInfo(@PathVariable("userId") Long userId){
        PopupUserVO user = transfertoPopupUserVO(userService.getUserByUserId(userId));
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);

        return JSON.toJSONString(ResultUtil.success(map));
    }

    private PopupUserVO transfertoPopupUserVO(User user){
        int noteCnt = noteService.getNoteCount(user.getUserId());
        long followerCnt = followService.getFollowerCount(EntityType.ENTITY_USER, user.getUserId());
        PopupUserVO popupUserVO = new PopupUserVO(
            user.getUserId(), user.getNickname(), user.getProfilePath(), user.getExperience(), noteCnt, followerCnt, user.getMotto(), user.getGender()
        );

        return popupUserVO;
    }

    @GetMapping("/user/account")
    public String toUserAccount(){
        return "account";
    }

    @GetMapping("/user/{userId}")
    public String toUserPersonalHomepage(@PathVariable("userId")Long userId, Model model){
        User user = userService.getUserByUserId(userId);
        PHUserVO userVO = new PHUserVO();
        userVO.setUserId(user.getUserId());
        userVO.setExperience(user.getExperience());
        userVO.setGender(user.getGender());
        userVO.setMotto(user.getMotto());
        userVO.setNickname(user.getNickname());
        userVO.setProfilePath(user.getProfilePath());
        model.addAttribute("owner", userVO);
        model.addAttribute("curuserId", hostHolder.getUser().getUserId());

        return "personal-homepage";
    }

    @GetMapping("/user/ph/note/{userId}")
    @ResponseBody
    public Result getPhNote(@PathVariable("userId")Long userId,
                            @RequestParam(value = "page", defaultValue = "0")Integer page,
                            @RequestParam(value = "limit", defaultValue = "6")Integer limit){
        int count = noteService.getNoteCount(userId);
        List<Note> notes = noteService.listUserNotesByStatus(userId, Code.PUBLISH, (page-1)*limit, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("pages", (count/limit)+1);
        map.put("notes", transferToHomeNoteVO(notes));

        return ResultUtil.success(map);
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

    @GetMapping("/user/ph/feed/{userId}")
    @ResponseBody
    public Result getPhFeed(@PathVariable("userId")Long userId,
                            @RequestParam(value = "page", defaultValue = "0")Integer page,
                            @RequestParam(value = "limit", defaultValue = "6")Integer limit){
        String key = RedisKeyUtil.getFeedKey(userId);
        long count = jedisAdapter.zcard(key);
        long pages = (count/limit)+1;
        Set<String> feedIds = jedisAdapter.zrevrange(key, (page-1)*limit, page*limit);
        List<Feed> feeds = new ArrayList<>();
        if(feedIds != null){
            for (String feedId : feedIds) {
                Feed feed = feedService.getFeedById(Long.parseLong(feedId));
                if (feed != null) {
                    feeds.add(feed);
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("pages", pages);
        map.put("feeds", transferToFeedVO(feeds));

        return ResultUtil.success(map);
    }

    private List<FeedVO> transferToFeedVO(List<Feed> feeds){
        User user = hostHolder.getUser();
        List<FeedVO> feedVOS = new ArrayList<>();
        FeedVO feedVO;
        JSONObject map;
        for(Feed feed: feeds){
            map = new JSONObject();
            feedVO = new FeedVO();
            feedVO.setId(feed.getId());
            feedVO.setCreatedDate(feed.getCreatedDate());
            feedVO.setAttachmentType(feed.getAttachmentType());
            feedVO.setType(feed.getType());
            feedVO.setUser(userService.getUserVOByUserId(feed.getUserId()));
            feedVO.setForwordCnt(feed.getForwordCnt());
            feedVO.setCommentCnt(feed.getCommentCnt());
            feedVO.setLikeCnt(likeService.getLikeCount(EntityType.ENTITY_FEED, feed.getId()));
            feedVO.setForwordState(jedisAdapter.sismember(RedisKeyUtil.getForwordKey(user.getUserId()), String.valueOf(feed.getId())));
            feedVO.setLikeState(likeService.getLikeStatus1(user.getUserId(), EntityType.ENTITY_FEED, feed.getId()));
            map.put("content", feed.getContent());
            if(feed.getType() == Code.FEED_ORIGINAL){
                map.put("attachment", feed.getAttachment());
            }else{
                //查找被转发的动态
                Feed forwordFeed = feedService.getFeedById(feed.getBindId());
                if(forwordFeed != null){
                    FeedVO forwordFeedVO = new FeedVO();
                    forwordFeedVO.setId(forwordFeed.getId());
                    forwordFeedVO.setCreatedDate(forwordFeed.getCreatedDate());
                    forwordFeedVO.setAttachmentType(forwordFeed.getAttachmentType());
                    forwordFeedVO.setType(forwordFeed.getType());
                    forwordFeedVO.setUser(userService.getUserVOByUserId(forwordFeed.getUserId()));
                    JSONObject forwordMap = new JSONObject();
                    forwordMap.put("content", forwordFeed.getContent());
                    forwordMap.put("attachment", forwordFeed.getAttachment());
                    forwordFeedVO.setData(JSON.toJSONString(forwordMap));
                    map.put("feed", forwordFeedVO);
                }else{
                    map.put("feed", null);
                }
            }
            feedVO.setData(JSON.toJSONString(map));
            feedVOS.add(feedVO);
        }

        return feedVOS;
    }
}
