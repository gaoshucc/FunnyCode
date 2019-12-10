package com.funnycode.blog.controller;

import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventProducer;
import com.funnycode.blog.async.EventType;
import com.funnycode.blog.model.ExceptionEnum;
import com.funnycode.blog.model.Result;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.CheckParamUtil;
import com.funnycode.blog.util.ResultUtil;
import com.funnycode.blog.util.VerifyUtil;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CC
 * @date 2019-09-10 14:07
 */
@Valid
@Controller
public class RegistController {
    private static final Logger logger = LoggerFactory.getLogger(RegistController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 判断用户是否已存在
     * @param response
     * @param username 用户名
     */
    @PostMapping("/userexists")
    public void userExists(HttpServletResponse response, String username) throws IOException{
        boolean exists = userService.userexists(username);
        response.getWriter().write(exists ? "false" : "true");
    }

    /**
     * 判断昵称是否已存在
     * @param response
     * @param nickname 昵称
     */
    @PostMapping("/nicknameexists")
    public void nicknameExists(HttpServletResponse response, String nickname) throws IOException {
        boolean exists = userService.nicknameexists(nickname);
        response.getWriter().write(exists ? "false" : "true");
    }

    /**
     * 生成验证码
     * @param response 响应
     * @param session 会话
     */
    @GetMapping("/valicode")
    public void generateValicode(HttpServletResponse response, HttpSession session) throws IOException {
        logger.info("接收到更新验证码请求");
        //第一个参数是生成的验证码，第二个参数是生成的图片
        Object[] objs = VerifyUtil.createImage();
        //将验证码存入Session
        session.setAttribute("valiCode", objs[0]);
        //将图片输出给浏览器
        BufferedImage image = (BufferedImage) objs[1];
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(image, "png", os);
    }

    /**
     * 检验验证码是否正确
     * @param response 响应
     * @param session 会话
     * @param valicode 验证码
     */
    @PostMapping("/checkValicode")
    public void checkValicode(HttpServletResponse response, HttpSession session, String valicode) throws IOException{
        //获取之前存放在session中的验证码
        //todo 改为验证码存在redis
        String imageCode = (String) session.getAttribute("valiCode");
        if (!imageCode.toLowerCase().equals(valicode.toLowerCase())) {
            response.getWriter().write("false");
            logger.info("验证码错误");
        } else {
            response.getWriter().write("true");
            logger.info("验证码" + valicode);
        }
    }

    //todo 跳转链接处理，敏感词过滤
    /**
     * 注册
     * @param username 用户名
     * @param nickname 昵称
     * @param password 密码
     */
    @PostMapping("/reg")
    @ResponseBody
    public Result regist(
            @RequestParam("username") @NotBlank(message = "用户名不能为空") @Length(min = 11, max = 11, message = "用户名错误") String username,
            @RequestParam("nickname") @NotBlank(message = "昵称不能为空") @Length(max = 20, message = "昵称长度不能大于20") String nickname,
            @RequestParam("password") @NotBlank(message = "密码不能为空") @Length(min = 8, message = "密码长度不能小于8") String password) {
        Result result;
        if(!CheckParamUtil.isSpecialChar(nickname)){
            Long userId = userService.regist(username, nickname, password);
            eventProducer.sendEvent(new EventModel().setActorId(userId)
                    .setType(EventType.REGIST));
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            result = ResultUtil.success(map);
        }else{
            result = ResultUtil.error(ExceptionEnum.PARAM_FAIL);
        }

        return result;
    }
}
