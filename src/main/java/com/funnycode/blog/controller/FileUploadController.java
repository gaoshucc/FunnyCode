package com.funnycode.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.funnycode.blog.util.QiniuUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CC
 * @date 2019-10-15 14:17
 */
@RequestMapping("/user/file")
@Controller
public class FileUploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

    @RequestMapping("/notepic")
    @ResponseBody
    public JSON uploadNotePic(@RequestParam(value = "editormd-image-file") MultipartFile notePic) {
        JSONObject result = new JSONObject();
        if (notePic.isEmpty()) {
            LOGGER.error("上传图片不可为空");
            result.put("success", 0);
            result.put("message", "上传异常");
        }
        try {
            String url = QiniuUploadUtil.uploadFile(notePic, null);
            result.put("success", 1);
            result.put("message", "上传成功");
            result.put("url", url);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("上传图片异常={}", e.getMessage());
            result.put("success", 0);
            result.put("message", "上传异常");
        }

        return result;
    }

    /**
     * layedit图片上传接口
     * @param feedPic 动态图片
     * @return json
     */
    @PostMapping("/feedpic")
    @ResponseBody
    public JSON uploadFeedPic(@RequestParam(value = "file")MultipartFile feedPic) {
        JSONObject result = new JSONObject();
        if (feedPic.isEmpty()) {
            LOGGER.error("上传图片异常={}","上传图片为空");
            result.put("code", -1);
            result.put("msg", "上传图片不可为空");
            Map<String, String> map = new HashMap<>();
            map.put("src", "");
            result.put("data", map);
        }
        try {
            String url = QiniuUploadUtil.uploadFile(feedPic, null);
            result.put("code", 0);
            result.put("msg", "上传成功");
            Map<String, String> map = new HashMap<>();
            map.put("src", url);
            result.put("data", map);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("上传图片异常={}", e.getMessage());
            result.put("code", -2);
            result.put("msg", "上传异常");
            Map<String, String> map = new HashMap<>();
            map.put("src", "");
            result.put("data", map);
        }

        return result;
    }


}
