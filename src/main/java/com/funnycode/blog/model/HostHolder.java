package com.funnycode.blog.model;

import org.springframework.stereotype.Component;

/**
 * @author CC
 * @date 2019-09-18 22:11
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear() {
        users.remove();;
    }
}
