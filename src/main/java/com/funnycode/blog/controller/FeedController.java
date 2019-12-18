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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private FeedService feedService;

    @Autowired
    private SensitiveService sensitiveService;

    @Autowired
    private MessageEventProducer eventProducer;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/user/tofeed")
    public String toFeed(Model model){
        model.addAttribute("userId", hostHolder.getUser().getUserId());
        return "feed";
    }

    @PostMapping("/user/feed/original")
    @ResponseBody
    public Result addOriginalFeed(@RequestParam("content") @NotBlank(message = "动态内容不能为空") String content) throws Exception {
        /*MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        StringBuilder imgContent = new StringBuilder();
        List<MultipartFile> files = multipartRequest.getFiles("fileArray");
        if(files.size() > 0){
            imgContent.append("<span class='feed-item-content-img-box'>");
            for(MultipartFile file: files){
                String url = QiniuUploadUtil.uploadFile(file, null);
                imgContent.append("<img src='").append(url).append("' class='feed-item-content-img' title='查看图片'>");
            }
            imgContent.append("</span>");
        }*/
        User user = hostHolder.getUser();
        Feed feed = new Feed();
        feed.setUserId(user.getUserId());
        feed.setType(Code.FEED_ORIGINAL);
        feed.setCreatedDate(new Date());
        feed.setForwordCnt(0L);
        feed.setCommentCnt(0L);
        Map<String, Object> map = new HashMap<>();
        map.put("nickname", user.getNickname());
        map.put("content", content);
        feed.setData(JSON.toJSONString(map));
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
        feed.setType(Code.FEED_FORWORD);
        feed.setCreatedDate(new Date());
        feed.setForwordCnt(0L);
        feed.setCommentCnt(0L);
        Map<String, Object> map = new HashMap<>();
        map.put("nickname", user.getNickname());
        map.put("content", sensitiveService.filter(content));
        map.put("feedId", feedId);
        feed.setData(JSON.toJSONString(map));
        boolean ret = feedService.addForwordFeed(feed, user.getUserId(), feedId);
        if(ret){
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
                EntityType.ENTITY_FEED, feedId, sensitiveService.filter(content), new Date(), 1, parentId);
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
        Long localUserId = hostHolder.getUser().getUserId();
        String key = RedisKeyUtil.getTimelineKey(localUserId);
        long count = jedisAdapter.zcard(key);
        long pages = (count/limit)+1;
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
        Long localUserId = hostHolder.getUser().getUserId();
        String key = RedisKeyUtil.getTimelineKey(localUserId);
        long count = jedisAdapter.zcard(key);
        long pages = (count/limit)+1;
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
        for(Feed feed: feeds){
            feedVO = new FeedVO();
            feedVO.setId(feed.getId());
            feedVO.setCreatedDate(feed.getCreatedDate());
            feedVO.setType(feed.getType());
            feedVO.setUser(userService.getUserVOByUserId(feed.getUserId()));
            feedVO.setForwordCnt(feed.getForwordCnt());
            feedVO.setCommentCnt(feed.getCommentCnt());
            feedVO.setLikeCnt(likeService.getLikeCount(EntityType.ENTITY_FEED, feed.getId()));
            feedVO.setForwordState(jedisAdapter.sismember(RedisKeyUtil.getForwordKey(user.getUserId()), String.valueOf(feed.getId())));
            feedVO.setLikeState(likeService.getLikeStatus1(user.getUserId(), EntityType.ENTITY_FEED, feed.getId()));
            if(feed.getType() == Code.FEED_ORIGINAL){
                feedVO.setData(feed.getData());
            }else{
                JSONObject map = JSON.parseObject(feed.getData());
                //查找被转发的动态
                Feed forwordFeed = feedService.getFeedById(map.getLong("feedId"));
                if(forwordFeed != null){
                    FeedVO forwordFeedVO = new FeedVO();
                    forwordFeedVO.setId(forwordFeed.getId());
                    forwordFeedVO.setCreatedDate(forwordFeed.getCreatedDate());
                    forwordFeedVO.setType(forwordFeed.getType());
                    forwordFeedVO.setUser(userService.getUserVOByUserId(forwordFeed.getUserId()));
                    forwordFeedVO.setData(forwordFeed.getData());
                    map.put("feed", forwordFeedVO);
                }else{
                    map.put("feed", null);
                }

                feedVO.setData(JSON.toJSONString(map));
            }
            feedVOS.add(feedVO);
        }

        return feedVOS;
    }
}
