package com.funnycode.blog.dao;

import com.funnycode.blog.model.User;
import com.funnycode.blog.model.vo.UserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * @author CC
 * @date 2019-09-10 14:31
 */
@Mapper
public interface UserDAO {
    String TABLE_NAME = "user";
    String INSERT_FIELDS = "username, password, nickname, status, last_logintime, role, profile_path, gender, experience, motto, regtime, salt, email";
    String SELECT_FIELDS = "user_id, " + INSERT_FIELDS;

    /**
     * 判断用户是否已存在
     * @param username 用户名
     * @return int
     */
    @Select({"SELECT COUNT(*) FROM ",TABLE_NAME," WHERE username = #{username}"})
    int existsByUsername(String username);

    /**
     * 判断昵称是否已存在
     * @param nickname 昵称
     * @return 影响行数
     */
    @Select({"SELECT COUNT(*) FROM ",TABLE_NAME," WHERE nickname = #{username}"})
    int existsByNickname(String nickname);

    /**
     * 判断昵称是否已存在
     * @param nickname 昵称
     * @param exceptId 需排除用户ID
     * @return 影响行数
     */
    @Select({"SELECT COUNT(*) FROM ",TABLE_NAME," WHERE nickname = #{nickname} AND user_id!=#{exceptId}"})
    int existsByNicknameAndExceptId(String nickname, Long exceptId);

    /**
     * 用户注册
     * @param user 用户信息
     * @return 影响行数
     */
    @Insert({"INSERT INTO ",TABLE_NAME,"(",INSERT_FIELDS,")",
            "VALUES(#{username}, #{password}, #{nickname}, #{status}, #{lastLogintime}, #{role}, #{profilePath}, #{gender}, #{experience}, #{motto}, #{regtime}, #{salt}, #{email})"})
    int add(User user);

    /**
     * 通过用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    @Select({"SELECT ",SELECT_FIELDS," FROM ",TABLE_NAME," WHERE username = #{username}"})
    User getByUsername(String username);

    /**
     * 通过用户名更新用户最后登录时间
     * @param userId 用户名
     * @param date 最后登录时间
     * @return 影响行数
     */
    long updateLastLogintimeByUserId(long userId, Date date);

    /**
     * 更新用户信息
     * @param user 用户
     * @return 影响行数
     */
    @Update({"UPDATE ", TABLE_NAME, " SET profile_path=#{profilePath}, nickname=#{nickname}, email=#{email}, motto=#{motto}, gender=#{gender} WHERE user_id=#{userId}"})
    long updateByUser(User user);

    /**
     * 更新用户积分
     * @param userId 用户ID
     * @param increment 积分增加值
     * @return 影响行数
     */
    @Update({"update ", TABLE_NAME, " set experience=experience+#{increment} where user_id=#{userId}"})
    long updateExperienceByUserId(long userId, int increment);

    /**
     * 通过用户id查找用户
     * @param userId 用户id
     * @return 用户
     */
    @Select({"SELECT ",SELECT_FIELDS," FROM ",TABLE_NAME," WHERE user_id = #{userId}"})
    User getByUserId(long userId);

    /**
     * 通过用户id查找用户VO
     * @param userId 用户id
     * @return User
     */
    @Select({"SELECT user_id, nickname, profile_path FROM ",TABLE_NAME," WHERE user_id = #{userId}"})
    UserVO getUserVOByUserId(long userId);

    /**
     * 通过评论者id查找评论者
     * @param userId 评论者id
     * @return User
     */
    @Select({"SELECT user_id, nickname, profile_path FROM ",TABLE_NAME," WHERE user_id = #{userId}"})
    User getObserverById(Long userId);

    /**
     * 通过用户昵称查找用户
     * @param nickname 用户昵称
     * @return 相关用户列表
     */
    @Select({"SELECT user_id, nickname, profile_path FROM ",TABLE_NAME," WHERE nickname like concat('%', #{nickname}, '%') AND user_id != #{userId}"})
    List<UserVO> findAllUserVOByNickname(String nickname, long userId);

    /**
     * 通过用户id查找用户昵称
     * @param userId 用户id
     * @return 用户昵称
     */
    @Select({"SELECT nickname FROM ", TABLE_NAME, " WHERE user_id = #{userId}"})
    String getNicknameById(long userId);

    /**
     * 通过用户id查找用户昵称
     * @param userId 用户id
     * @return 头像路径
     */
    @Select({"SELECT profile_path FROM ", TABLE_NAME, " WHERE user_id = #{userId}"})
    String getProfilePathById(long userId);

    /**
     * 更新个性签名
     * @param userId 用户ID
     * @param signature 个性签名
     * @return 影响行数
     */
    @Update({"update ", TABLE_NAME, " set motto=#{signature} where user_id=#{userId}"})
    int updateSignatureByUserId(long userId, String signature);
}
