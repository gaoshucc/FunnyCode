package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.async.producer.MessageEventProducer;
import com.funnycode.blog.model.*;
import com.funnycode.blog.model.vo.CommentVO;
import com.funnycode.blog.model.vo.FeedVO;
import com.funnycode.blog.service.*;
import com.funnycode.blog.util.JedisAdapter;
import com.funnycode.blog.util.RedisKeyUtil;
import com.funnycode.blog.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.*;

/**
 * @author CC
 * @date 2019-11-03 10:20
 */
@Valid
@Controller
public class FeedController {
    private final HostHolder hostHolder;
    private final JedisAdapter jedisAdapter;
    private final FeedService feedService;
    private final SensitiveService sensitiveService;
    private final MessageEventProducer eventProducer;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;

    public FeedController(FeedService feedService, HostHolder hostHolder, JedisAdapter jedisAdapter, SensitiveService sensitiveService, MessageEventProducer eventProducer, UserService userService, CommentService commentService, LikeService likeService) {
        this.feedService = feedService;
        this.hostHolder = hostHolder;
        this.jedisAdapter = jedisAdapter;
        this.sensitiveService = sensitiveService;
        this.eventProducer = eventProducer;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping("/user/tofeed")
    public String toFeed(Model model){
        model.addAttribute("userId", hostHolder.getUser().getUserId());
        return "feed";
    }

    @PostMapping("/user/feed/original")
    @ResponseBody
    public Result addOriginalFeed(@RequestParam("content") String content,
                                  @RequestParam("attachment") String attachment) throws Exception {
        if(StringUtils.isBlank(content) && StringUtils.isBlank(attachment)){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        User user = hostHolder.getUser();
        Feed feed = new Feed();
        feed.setUserId(user.getUserId());
        feed.setCreatedDate(new Date());
        feed.setContent(content);
        feed.setAttachment(attachment);
        if(StringUtils.isBlank(attachment)){
            feed.setAttachmentType(Code.FEED_ATTACH_NONE);
        }else{
            feed.setAttachmentType(Code.FEED_ATTACH_IMG);
        }
        feed.setType(Code.FEED_ORIGINAL);
        feed.setBindId(0L);
        feed.setForwordCnt(0L);
        feed.setCommentCnt(0L);

        if(feedService.addOriginalFeed(feed)){
            eventProducer.sendEvent(new EventModel(EventType.FEED)
                    .setActorId(user.getUserId())
                    .setEntityType(EntityType.ENTITY_FEED)
                    .setEntityId(feed.getId()).setExt("time", String.valueOf(feed.getCreatedDate().getTime())));
            return ResultUtil.success();
        }else{
            return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }
    }

    @PostMapping("/user/feed/forword")
    @ResponseBody
    public Result addForwordFeed(@RequestParam("content") String content,
                                 @RequestParam("feedId") @NotBlank(message = "动态不存在") Long feedId){
        User user = hostHolder.getUser();
        Feed beForword = feedService.getFeedById(feedId);
        if(beForword == null){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }

        Feed feed = new Feed();
        feed.setUserId(user.getUserId());
        feed.setCreatedDate(new Date());
        feed.setContent(content);
        feed.setAttachment(null);
        feed.setAttachmentType(Code.FEED_ATTACH_NONE);
        feed.setType(Code.FEED_FORWORD);
        feed.setBindId(beForword.getId());
        feed.setForwordCnt(0L);
        feed.setCommentCnt(0L);

        if(feedService.addForwordFeed(feed, user.getUserId(), beForword.getId())){
            eventProducer.sendEvent(new EventModel(EventType.FEED)
                    .setActorId(user.getUserId())
                    .setEntityType(EntityType.ENTITY_FEED)
                    .setEntityId(feed.getId()).setExt("time", String.valueOf(feed.getCreatedDate().getTime())));
            return ResultUtil.success();
        }else{
            return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }
    }

    @PostMapping("/user/feed/delete")
    @ResponseBody
    public Result deleteFeed(@RequestParam("feedId") @NotBlank(message = "动态不存在") Long feedId){
        User user = hostHolder.getUser();
        Feed feed = feedService.getFeedById(feedId);
        if(feed != null && feed.getUserId().equals(hostHolder.getUser().getUserId())){
            boolean ret = feedService.removeFeed(feed, hostHolder.getUser().getUserId());
            if(ret){
                eventProducer.sendEvent(new EventModel(EventType.REMOVEFEED)
                        .setActorId(user.getUserId())
                        .setEntityType(EntityType.ENTITY_FEED)
                        .setEntityId(feed.getId()));
                return ResultUtil.success();
            }else{
                return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
            }
        }else{
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
    }

    @PostMapping("/user/feed/comment")
    @ResponseBody
    public Result commentToFeed(@RequestParam("content") @NotBlank(message = "评论内容不能为空") String content,
                                @RequestParam("feedId") @NotBlank(message = "被评论者不存在") Long feedId,
                                @RequestParam("parentId")Long parentId){
        Comment comment = new Comment(hostHolder.getUser().getUserId(),
                EntityType.ENTITY_FEED, feedId, sensitiveService.filter(content), new Date(), Code.NORMAL_COMMENT, parentId);
        boolean ret = commentService.addComment(comment);
        if(ret){
            Feed feed = feedService.getFeedById(feedId);
            if(!feed.getUserId().equals(hostHolder.getUser().getUserId())){
                eventProducer.sendEvent(new EventModel().setActorId(hostHolder.getUser().getUserId())
                        .setEntityType(EntityType.ENTITY_FEED).setEntityId(feedId).setEntityOwnerId(feed.getUserId())
                        .setType(EventType.COMMENT));
            }
            return ResultUtil.success();
        }else{
            return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }
    }

    @GetMapping("/user/feed/comments")
    @ResponseBody
    public String getNoteComments(@RequestParam("feedId") @NotBlank Long feedId){
        List<CommentVO> comments = commentService.selectCommentsByEntity(EntityType.ENTITY_FEED, feedId, 0);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", comments);

        return JSON.toJSONString(ResultUtil.success(map), SerializerFeature.DisableCircularReferenceDetect);
    }

    @RequestMapping("/user/feed/brief")
    @ResponseBody
    public Result getFeedsBrief(@RequestParam(value = "page", defaultValue = "0")Long page,
                                @RequestParam(value = "limit", defaultValue = "6")Long limit){
        long localUserId = hostHolder.getUser().getUserId();
        String key = RedisKeyUtil.getTimelineKey(localUserId);
        long count = jedisAdapter.zcard(key),
             pages = (count/limit)+1;
        Set<String> feedIds = jedisAdapter.zrevrange(key, (page-1)*limit, page*limit);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getFeedById(Long.parseLong(feedId));
            if (feed != null) {
                feeds.add(feed);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("pages", pages);
        map.put("feeds", feeds);

        return ResultUtil.success(map);
    }

    @GetMapping("/user/feed/detail")
    @ResponseBody
    public Result getFeedsDetail(@RequestParam(value = "page", defaultValue = "0")Long page,
                                   @RequestParam(value = "limit", defaultValue = "6")Long limit){
        long localUserId = hostHolder.getUser().getUserId();
        String key = RedisKeyUtil.getTimelineKey(localUserId);
        long count = jedisAdapter.zcard(key),
             pages = (count/limit)+1;
        Set<String> feedIds = jedisAdapter.zrevrange(key, (page-1)*limit, page*limit);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getFeedById(Long.parseLong(feedId));
            if (feed != null) {
                feeds.add(feed);
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
