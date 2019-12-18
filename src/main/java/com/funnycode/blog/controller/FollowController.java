package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventProducer;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.*;
import com.funnycode.blog.model.vo.UserVO;
import com.funnycode.blog.service.FollowService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CC
 * @date 2019-10-02 21:28
 */
@Valid
@Controller
public class FollowController {
    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @GetMapping("/followstatus/user")
    @ResponseBody
    public String getAuthorFollowStatus(@RequestParam("userId") long userId){
        Map<String, Object> map = new HashMap<>();
        if(hostHolder.getUser() == null){
            map.put("status", Code.UNFOLLOW);
        }else if(hostHolder.getUser().getUserId() == userId){
            map.put("status", Code.BANTO_FOLLOW);
        }else{
            boolean ret = followService.isFollower(hostHolder.getUser().getUserId(), EntityType.ENTITY_USER, userId);
            if(ret){
                map.put("status", Code.HASFOLLOW);
            }else{
                map.put("status", Code.UNFOLLOW);
            }
        }
        long followerCnt = followService.getFollowerCount(EntityType.ENTITY_USER, userId);
        map.put("followerCnt", followerCnt);
        Result result = ResultUtil.success(map);

        return JSON.toJSONString(result);
    }

    @PostMapping("/user/follow/user")
    @ResponseBody
    public Result followUser(@RequestParam("userId") long userId) {
        User user = userService.getUserByUserId(userId);
        //被关注用户不能为空，且不可关注自己
        if(user == null || hostHolder.getUser().getUserId() == userId){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
        boolean ret = followService.follow(hostHolder.getUser().getUserId(), EntityType.ENTITY_USER, userId);
        Result result;
        if(ret){
            eventProducer.sendEvent(new EventModel(EventType.FOLLOW)
                    .setActorId(hostHolder.getUser().getUserId()).setEntityId(userId)
                    .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

            Map<String, Object> map = new HashMap<>();
            map.put("followerCnt", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
            map.put("followeeCnt", followService.getFolloweeCount(hostHolder.getUser().getUserId(), EntityType.ENTITY_USER));
            map.put("status", 1);
            result = ResultUtil.success(map);
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }

        return result;
    }

    @PostMapping("/user/unfollow/user")
    @ResponseBody
    public Result unfollowUser(@RequestParam("userId") long userId) {
        User user = userService.getUserByUserId(userId);
        //取关用户必须存在，且不可取关自己
        if(user == null || hostHolder.getUser().getUserId() == userId){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getUserId(), EntityType.ENTITY_USER, userId);
        Result result;
        if(ret){
            eventProducer.sendEvent(new EventModel(EventType.UNFOLLOW)
                    .setActorId(hostHolder.getUser().getUserId()).setEntityId(userId)
                    .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

            Map<String, Object> map = new HashMap<>();
            map.put("followerCnt", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
            map.put("followeeCnt", followService.getFolloweeCount(hostHolder.getUser().getUserId(), EntityType.ENTITY_USER));
            map.put("status", -1);
            result = ResultUtil.success(map);
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }

        return result;
    }

    @GetMapping("/user/followers")
    @ResponseBody
    public Result followers(@RequestParam(value = "offset", defaultValue = "0")Integer offset,
                            @RequestParam(value = "limit", defaultValue = "10")Integer limit) {
        List<Long> followerIds = followService.getFollowers(EntityType.ENTITY_USER, hostHolder.getUser().getUserId(), offset, limit);
        Map<String, Object> map = new HashMap<>(2);
        map.put("followers", getUsersInfo(followerIds));
        map.put("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, hostHolder.getUser().getUserId()));
        return ResultUtil.success(map);
    }

    @GetMapping("/user/followees")
    @ResponseBody
    public Result followees(@RequestParam(value = "offset", defaultValue = "0")Integer offset,
                            @RequestParam(value = "limit", defaultValue = "10")Integer limit) {
        List<Long> followeeIds = followService.getFollowees(hostHolder.getUser().getUserId(), EntityType.ENTITY_USER, offset, limit);
        Map<String, Object> map = new HashMap<>(2);
        map.put("followees", getUsersInfo(followeeIds));
        map.put("followeeCount", followService.getFolloweeCount(hostHolder.getUser().getUserId(), EntityType.ENTITY_USER));
        return ResultUtil.success(map);
    }

    private List<UserVO> getUsersInfo(List<Long> userIds) {
        List<UserVO> userInfos = new ArrayList<>();
        for (Long uid : userIds) {
            UserVO user = userService.getUserVOByUserId(uid);
            if (user == null) {
                continue;
            }

            userInfos.add(user);
        }
        return userInfos;
    }
}
