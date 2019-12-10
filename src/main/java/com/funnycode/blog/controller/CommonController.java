package com.funnycode.blog.controller;

import com.funnycode.blog.model.*;
import com.funnycode.blog.service.FeedbackService;
import com.funnycode.blog.service.ReasonService;
import com.funnycode.blog.service.ReportService;
import com.funnycode.blog.service.SensitiveService;
import com.funnycode.blog.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CC
 * @date 2019-10-04 23:32
 */
@Valid
@Controller
public class CommonController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private ReasonService reasonService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private SensitiveService sensitiveService;

    @GetMapping("/report/reasons")
    @ResponseBody
    public Result getAllReasons(){
        List<Reason> reasons = reasonService.getAllReasons();
        Map<String, Object> map = new HashMap<>();
        map.put("reasons", reasons);

        return ResultUtil.success(map);
    }

    @GetMapping("/user/report/{entityType}/{entityId}")
    public String toreport(Model model, @PathVariable("entityType")Integer entityType,
                           @PathVariable("entityId")Long entityId){
        model.addAttribute("entityType", entityType);
        model.addAttribute("entityId", entityId);

        return "commons/report";
    }

    @PostMapping("/user/report")
    @ResponseBody
    public Result report(@RequestParam("entityType") @NotBlank Integer entityType,
                         @RequestParam("entityId") @NotBlank Long entityId,
                         @RequestParam("reasons") @NotBlank(message = "举报原因不能为空") String reasons,
                         @RequestParam("content") @NotBlank(message = "举报内容不能为空") String content){
        Report report = new Report(hostHolder.getUser().getUserId(),
                entityType, entityId,
                reasons, sensitiveService.filter(content), new Date(), 0);
        boolean ret = reportService.addReport(report);
        Result result;
        if(ret){
            result = ResultUtil.success();
        }else{
            result = ResultUtil.error(ExceptionEnum.SER_EOR);
        }

        return result;
    }

    @PostMapping("/user/feedback")
    @ResponseBody
    public Result feedback(@RequestParam("content") @NotBlank(message = "反馈内容不能为空") String content){
        boolean ret = feedbackService.addFeedback(new Feedback( hostHolder.getUser().getUserId(), sensitiveService.filter(content)));
        if(ret){
            return ResultUtil.success();
        }else{
            return ResultUtil.error(ExceptionEnum.UNKNOWN_EOR);
        }
    }
}
