package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.funnycode.blog.model.*;
import com.funnycode.blog.model.VO.AccountUserVO;
import com.funnycode.blog.model.VO.EditableAccountUserVO;
import com.funnycode.blog.service.AccountBindingService;
import com.funnycode.blog.service.FollowService;
import com.funnycode.blog.service.NoteService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.BlogUtil;
import com.funnycode.blog.util.CheckParamUtil;
import com.funnycode.blog.util.QiniuUploadUtil;
import com.funnycode.blog.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CC
 * @date 2019-10-24 14:43
 */
@Valid
@RequestMapping("/user")
@Controller
public class AccountController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private NoteService noteService;

    @Autowired
    private FollowService followService;

    @Autowired
    private AccountBindingService accountBindingService;

    @Autowired
    private UserService userService;

    @GetMapping("/account/basic")
    @ResponseBody
    public String getAccountBasicInfo(){
        AccountUserVO user = transferToAccountUserVO(hostHolder.getUser());
        user.setQqBind(accountBindingService.getAccountBindingState(user.getUserId(), ThirdLoginType.QQ));
        user.setWechatBind(accountBindingService.getAccountBindingState(user.getUserId(), ThirdLoginType.WECHAT));
        user.setNoteCnt(noteService.getNoteCount(user.getUserId()));
        user.setFolloweeCnt(followService.getFolloweeCount(user.getUserId(), EntityType.ENTITY_USER));
        user.setFollowerCnt(followService.getFollowerCount(EntityType.ENTITY_USER, user.getUserId()));
        user.setLevel(BlogUtil.getLevel(user.getExperience()));
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);

        return JSON.toJSONString(ResultUtil.success(map));
    }

    private AccountUserVO transferToAccountUserVO(User user){
        AccountUserVO accountUserVO = new AccountUserVO();
        accountUserVO.setUserId(user.getUserId());
        accountUserVO.setUsername(user.getUsername());
        accountUserVO.setNickname(user.getNickname());
        accountUserVO.setProfilePath(user.getProfilePath());
        accountUserVO.setMotto(user.getMotto());
        accountUserVO.setEmail(user.getEmail());
        accountUserVO.setGender(user.getGender());
        Long day = (System.currentTimeMillis() - user.getRegtime().getTime())/(24*60*60*1000);
        accountUserVO.setActive(day);
        accountUserVO.setExperience(user.getExperience());

        return accountUserVO;
    }

    @GetMapping("/account/edit")
    public String toEditAccount(){
        return "commons/edit-account";
    }

    @GetMapping("/account/editable")
    @ResponseBody
    public Result getAccountEditable(){
        User user = hostHolder.getUser();
        EditableAccountUserVO editableUser = new EditableAccountUserVO(user.getUserId(), user.getProfilePath(), user.getNickname(), user.getEmail(), user.getMotto(), user.getGender());
        Map<String, Object> map = new HashMap<>();
        map.put("user", editableUser);

        return ResultUtil.success(map);
    }

    @PostMapping("/account/edited")
    @ResponseBody
    public Result updateAccount(@RequestParam(value = "profile", required = false) MultipartFile profile,
                                @RequestParam(value = "nickname", required = false)String nickname,
                                @RequestParam(value = "email", required = false)String email,
                                @RequestParam(value = "motto", required = false)String motto,
                                @RequestParam(value = "gender", required = false)Integer gender){
        Result result;
        User user = hostHolder.getUser();
        String profilePath = user.getProfilePath();
        //传入头像不为空，则上传头像
        if(!profile.isEmpty()){
            try {
                profilePath = QiniuUploadUtil.uploadFile(profile, null);
            } catch (Exception e) {
                return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
            }
        }
        if (StringUtils.isBlank(email) || CheckParamUtil.checkEmail(email)) {
            if (StringUtils.isBlank(nickname) || !CheckParamUtil.isSpecialChar(nickname)) {
                boolean ret = userService.updateUserInfo(
                        profilePath,
                        StringUtils.isBlank(nickname) ? user.getNickname() : nickname,
                        StringUtils.isBlank(email) ? user.getEmail() : email,
                        StringUtils.isBlank(motto) ? user.getMotto() : motto,
                        StringUtils.isBlank(String.valueOf(gender)) ? user.getGender() : gender,
                        hostHolder.getUser().getUserId());
                if (ret) {
                    result = ResultUtil.success();
                } else {
                    result = ResultUtil.error(ExceptionEnum.SER_EOR);
                }
            } else {
                result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
            }
        } else {
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }

        return result;
    }

    /**
     * 判断昵称是否已存在
     * @param nickname 昵称
     */
    @PostMapping("/nicknameexists")
    @ResponseBody
    public Result nicknameExistsAjax(String nickname) {
        boolean exists = userService.nicknameexistsExcept(nickname, hostHolder.getUser().getUserId());
        if(exists){
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }else{
            return ResultUtil.success();
        }
    }
}
