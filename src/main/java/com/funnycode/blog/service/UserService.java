package com.funnycode.blog.service;

import com.funnycode.blog.model.User;
import com.funnycode.blog.model.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * @author CC
 * @date 2019-09-10 14:24
 */
public interface UserService {
    /**
     * 判断用户是否已存在
     *
     * @param username
     * @return boolean true为已存在，false为不存在
     */
    boolean userexists(String username);

    /**
     * 判断昵称是否已存在
     *
     * @param nickname 昵称
     * @return boolean true为已存在，false为不存在
     */
    boolean nicknameexists(String nickname);

    /**
     * 判断昵称是否已存在
     * @param nickname 昵称
     * @param exceptId 用户ID
     * @return
     */
    boolean nicknameexistsExcept(String nickname, Long exceptId);

    /**
     * 注册
     *
     * @param username 用户名
     * @param nickname 昵称
     * @param password 密码
     * @return Long 用户ID
     */
    Long regist(String username, String nickname, String password);

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return Map<String, Object>
     */
    Map<String, Object> login(String username, String password);

    /**
     * 第三方登录
     *
     * @param userId
     * @return Map<String, Object>
     */
    Map<String, Object> thirdLogin(Long userId);

    /**
     * 注销
     * @param ticket token
     */
    void logout(String ticket);

    /**
     * 通过用户名查找用户
     *
     * @param username
     * @return User
     */
    User getUserByUsername(String username);

    /**
     * 通过userId获得用户
     * @param userId 用户ID
     * @return User 用户
     */
    User getUserByUserId(long userId);

    /**
     * 通过用户id查找用户VO
     * @param userId 用户id
     * @return User
     */
    UserVO getUserVOByUserId(long userId);

    /**
     * 通过用户昵称查找用户
     * @param nickname 用户昵称
     * @return List<UserVO>
     */
    List<UserVO> getUserVOSByNickname(String nickname, Long userId);

    /**
     * 通过用户id查找用户昵称
     * @param userId 用户id
     * @return 用户昵称
     */
    String getNicknameById(Long userId);

    /**
     * 通过用户id查找用户昵称
     * @param userId 用户id
     * @return 头像路径
     */
    String getProfilePathById(Long userId);

    /**
     * 更新用户信息
     * @param profilePath 头像
     * @param nickname 昵称
     * @param email 邮箱
     * @param motto 个性签名
     * @param gender 性别
     * @param userId 用户ID
     * @return boolean
     */
    boolean updateUserInfo(String profilePath, String nickname, String email, String motto, Integer gender, Long userId);

    /**
     * 更新用户积分
     * @param userId 用户ID
     * @param increment 积分增加值
     * @return 更新是否成功
     */
    boolean updateUserExperience(long userId, int increment);

    /**
     * 更新个性签名
     * @param userId 用户ID
     * @param signature 个性签名
     * @return 更新是否成功
     */
    boolean updateUserSignature(long userId, String signature);
}
