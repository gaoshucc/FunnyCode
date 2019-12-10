package com.funnycode.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author CC
 * @date 2019-09-09 22:13
 */
@Validated
@Controller
public class HomeController {

    @RequestMapping(path = {"/", "/home"}, method = RequestMethod.GET)
    public String home(){
        return "home";
    }
}
