package com.funnycode.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author CC
 * @date 2019-12-11 21:03
 */
@RequestMapping("/user")
@Controller
public class AskController {

    @GetMapping("/ask")
    public String noting(){
        return "asking";
    }
}
