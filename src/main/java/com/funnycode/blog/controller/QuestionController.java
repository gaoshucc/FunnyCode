package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.funnycode.blog.model.*;
import com.funnycode.blog.service.QuestionService;
import com.funnycode.blog.util.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CC
 * @date 2020-01-02 21:50
 */
@Controller
public class QuestionController {
    private final String NAMESPACE = "/user";
    private final QuestionService questionService;
    private final HostHolder hostHolder;

    public QuestionController(QuestionService questionService, HostHolder hostHolder) {
        this.questionService = questionService;
        this.hostHolder = hostHolder;
    }

    @GetMapping(NAMESPACE + "/question/type")
    @ResponseBody
    public Result getNoteType(){
        List<QuestType> types = questionService.getQuestType();
        if(types == null || types.size() == 0){
            return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }else{
            Map<String, Object> map = new HashMap<>();
            map.put("types", JSON.toJSONString(types));
            return ResultUtil.success(map);
        }
    }

    @PostMapping(NAMESPACE + "/question/save")
    @ResponseBody
    public Result saveNote(@RequestParam("title") @NotBlank(message = "标题不能为空") String title,
                           @RequestParam("typeId") @NotBlank(message = "类型不能为空") Integer typeId,
                           @RequestParam("markdownDoc") @NotBlank(message = "问题内容不能为空") String content) {
        Result result;
        long userId = hostHolder.getUser().getUserId();
        Question question = new Question(title, typeId, userId, new Date(), Code.SAVE, content, 0);
        if(questionService.addQuestion(question)){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }

        return result;
    }
}
