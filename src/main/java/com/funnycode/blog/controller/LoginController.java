package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.funnycode.blog.configration.Constants;
import com.funnycode.blog.model.*;
import com.funnycode.blog.service.AccountBindingService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.HttpClientUtil;
import com.funnycode.blog.util.ResultUtil;
import com.funnycode.blog.util.URLEncodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CC
 * @date 2019-09-10 15:27
 */
@Validated
@Controller
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Value("${cookie.domain}")
    private String domain;

    @Value("${cookie.max-age}")
    private int MAX_AGE;

    @Autowired
    private Constants constants;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountBindingService accountBindingService;

    @RequestMapping("/tologin")
    public String toLogin(){
        if(hostHolder.getUser() != null){
            return "redirect:/";
        }
        return "login";
    }

    /**
     * 判断用户是否已存在
     * @param response 响应
     * @param username 用户名
     */
    @PostMapping("/usernameexists")
    public void userExists(HttpServletResponse response, String username) throws IOException {
        boolean exists = userService.userexists(username);
        response.getWriter().write(exists ? "true" : "false");
        response.getWriter().close();
    }

    @PostMapping("/login")
    public String login(Model model,
            @RequestParam("username") @NotBlank(message = "用户名错误") @Length(min = 11, max = 11, message = "用户名错误") String username,
            @RequestParam("password") @NotBlank(message = "密码错误") @Length(min = 8, message = "密码错误") String password,
            @RequestParam(value="next", required = false) String next,
            @RequestParam(value="rememberMe", defaultValue = "false") boolean rememberMe,
            HttpServletResponse response) {
        Map<String, Object> map = userService.login(username, password);
        if (map.containsKey(constants.getTOKEN())) {
            Cookie cookie = new Cookie(constants.getTOKEN(), map.get(constants.getTOKEN()).toString());
            cookie.setPath("/");
            cookie.setDomain(domain);
            cookie.setHttpOnly(true);
            if (rememberMe) {
                cookie.setMaxAge(MAX_AGE);
            }
            response.addCookie(cookie);

            if(!StringUtils.isEmpty(next) && next.length() > 0){
                return "redirect:" + next;
            }

            return "redirect:/";
        }else{
            model.addAttribute("username", username);
            model.addAttribute("password", password);
            model.addAttribute("next", next);
            return "login";
        }
    }

    //todo 跳转链接处理
    @GetMapping("/relogin")
    public String regloginPage(Model model, @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response,
                         @CookieValue("fc_ticket")String ticket){
        userService.logout(ticket);
        Cookie cookie = new Cookie(constants.getTOKEN(), "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/haslogin")
    @ResponseBody
    public Result hasLogin(){
        User user = hostHolder.getUser();
        Map<String, Object> map = new HashMap<>();
        if(user == null){
            map.put("status", 0);
        }else{
            map.put("status", 1);
            map.put("user", userService.getUserVOByUserId(user.getUserId()));
        }

        return ResultUtil.success(map);
    }

    @GetMapping("/qq/url")
    @ResponseBody
    public Result getQQLoginURL(){
        StringBuilder url = new StringBuilder("https://graph.qq.com/oauth2.0/authorize?");
        url.append("response_type=code");
        url.append("&client_id=").append(constants.getQQ_APPID());
        url.append("&redirect_uri=").append(URLEncodeUtil.getURLEncoderString(constants.getQQ_REDIRECTURL()));
        url.append("&state=").append(constants.getQQ_STATE());
        url.append("&scope=get_user_info");
        Map<String, Object> map = new HashMap<>();
        map.put("url", url);

        return ResultUtil.success(map);
    }

    @GetMapping("/qq/callback")
    public String qqLogin(Model model, @RequestParam("code")String code,
                          @RequestParam("state")String state,
                          HttpServletResponse response) throws Exception {
        if(code == null || !constants.getQQ_STATE().equals(state)){
            return JSON.toJSONString(ResultUtil.error(ExceptionEnum.PARAM_FAIL));
        }
        Map<String, Object> qqProperties = getToken(code);
        String openId = getOpenId(qqProperties);
        qqProperties.put("openId",openId);

        AccountBinding accountBinding = accountBindingService.getAccountBinding(openId, ThirdLoginType.QQ);
        User user = hostHolder.getUser();
        if(accountBinding == null){
            //绑定账号
            System.out.println("user == null:" + (user == null));
            if(user != null){
                AccountBinding qqbinding = new AccountBinding(user.getUserId(), openId, ThirdLoginType.QQ);
                boolean ret = accountBindingService.addAccountBinding(qqbinding);
                System.out.println("ret:" + ret);
                if(ret){
                    //todo 获取并更新用户数据
                    return "redirect: /user/account";
                }
            }
        }else{
            if(user == null){
                //登录
                Map<String, Object> map = userService.thirdLogin(accountBinding.getUserId());
                Cookie cookie = new Cookie(constants.getTOKEN(), map.get(constants.getTOKEN()).toString());
                cookie.setPath("/");
                cookie.setDomain(domain);
                cookie.setHttpOnly(true);
                cookie.setMaxAge(MAX_AGE);
                response.addCookie(cookie);

                return "redirect:/";
            }
        }
        //错误
        model.addAttribute("code", ExceptionEnum.PARAM_FAIL.getCode());
        model.addAttribute("msg", ExceptionEnum.PARAM_FAIL.getMsg());

        return "commons/error";
    }

    /**
     * 解绑QQ
     * @return
     */
    @PostMapping("/user/qq/unbind")
    @ResponseBody
    public Result unbindQQ(){
        boolean ret = accountBindingService.removeAccountBinding(hostHolder.getUser().getUserId(), ThirdLoginType.QQ);
        if(ret){
            return ResultUtil.success();
        }else{
            return ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }
    }

    private Map<String, Object> getToken(String code) throws Exception {
        StringBuilder url = new StringBuilder();
        url.append("https://graph.qq.com/oauth2.0/token?");
        url.append("grant_type=authorization_code");
        url.append("&client_id=").append(constants.getQQ_APPID());
        url.append("&client_secret=").append(constants.getQQ_APPKEY());
        url.append("&code=").append(code);
        url.append("&redirect_uri=").append(URLEncodeUtil.getURLEncoderString(constants.getQQ_REDIRECTURL()));
        // 获得token
        String result = HttpClientUtil.get(url.toString(), "UTF-8");
        // 把token保存
        String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(result, "&");
        String accessToken = StringUtils.substringAfterLast(items[0], "=");
        Long expiresIn = new Long(StringUtils.substringAfterLast(items[1], "="));
        String refreshToken = StringUtils.substringAfterLast(items[2], "=");
        //token信息
        Map<String,Object > qqProperties = new HashMap<>();
        qqProperties.put("accessToken", accessToken);
        qqProperties.put("expiresIn", String.valueOf(expiresIn));
        qqProperties.put("refreshToken", refreshToken);
        return qqProperties;
    }

    @GetMapping("/refreshToken")
    public Map<String,Object> refreshToken(Map<String,Object> qqProperties) throws Exception {
        // 获取refreshToken
        String refreshToken = (String) qqProperties.get("refreshToken");
        StringBuilder url = new StringBuilder("https://graph.qq.com/oauth2.0/token?");
        url.append("grant_type=refresh_token");
        url.append("&client_id=").append(constants.getQQ_APPID());
        url.append("&client_secret=").append(constants.getQQ_APPKEY());
        url.append("&refresh_token=").append(refreshToken);
        String result = HttpClientUtil.get(url.toString(), "UTF-8");
        // 把新获取的token存到map中
        String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(result, "&");
        String accessToken = StringUtils.substringAfterLast(items[0], "=");
        Long expiresIn = new Long(StringUtils.substringAfterLast(items[1], "="));
        String newRefreshToken = StringUtils.substringAfterLast(items[2], "=");
        //重置信息
        qqProperties.put("accessToken", accessToken);
        qqProperties.put("expiresIn", String.valueOf(expiresIn));
        qqProperties.put("refreshToken", newRefreshToken);
        return qqProperties;
    }

    /**
     * 获取用户openId（根据token）
     */
    private String getOpenId(Map<String,Object> qqProperties) throws Exception {
        // 获取保存的用户的token
        String accessToken = (String) qqProperties.get("accessToken");
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        StringBuilder url = new StringBuilder("https://graph.qq.com/oauth2.0/me?");
        url.append("access_token=" + accessToken);
        String result = HttpClientUtil.get(url.toString(), "UTF-8");
        String openId = StringUtils.substringBetween(result, "\"openid\":\"", "\"}");
        return openId;
    }

    /**
     * 根据token,openId获取用户信息
     */
    private QQUser getQQUserInfo(Map<String,Object> qqProperties) throws Exception {
        // 取token
        String accessToken = (String) qqProperties.get("accessToken");
        String openId = (String) qqProperties.get("openId");
        if (!StringUtils.isNotEmpty(accessToken) || !StringUtils.isNotEmpty(openId)) {
            return null;
        }
        //拼接url
        StringBuilder url = new StringBuilder("https://graph.qq.com/user/get_user_info?");
        url.append("access_token=").append(accessToken);
        url.append("&oauth_consumer_key=").append(constants.getQQ_APPID());
        url.append("&openid=").append(openId);
        // 获取qq相关数据
        String result = HttpClientUtil.get(url.toString(), "UTF-8");
        Object json = JSON.parseObject(result, QQUser.class);
        QQUser user = (QQUser) json;

        return user;
    }
}
