package com.funnycode.blog.service;

import com.funnycode.blog.model.Message;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author CC
 * @date 2019-09-23 00:36
 */
public interface MessageService {
    /**
     * 创建消息
     * @param message 消息
     */
    boolean addMessage(Message message);

    /**
     * 获取对话消息条数
     * @param conversationId 对话ID
     * @return 数据条数
     */
    int getMessageCount(String conversationId);

    /**
     * 获取对话详情
     * @param conversationId 对话编号
     * @param offset 偏移量
     * @param limit 查询消息数据数
     * @return 消息列表
     */
    List<Message> getConversationDetailInit(String conversationId, int offset, int limit);

    /**
     * 获取对话详情
     * @param conversationId 对话编号
     * @param offset 偏移量
     * @param limit 查询消息数据数
     * @param firstTime 页面最早消息的创建时间
     * @return 消息列表
     */
    List<Message> getConversationDetail(String conversationId, int offset, int limit, Date firstTime);

    /**
     * 获取对话详情
     * @param conversationId 对话编号
     * @param lastTime 页面最新消息的创建时间
     * @return 消息列表
     */
    List<Message> getConversationDetailRegular(String conversationId, Date lastTime);

    /**
     * 获取所有对话
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 对话数据数
     * @return 对话列表
     */
    List<Message> getConversationList(long userId, int offset, int limit);

    /**
     * 获取用户某个类型的消息
     * @param userId 用户ID
     * @param type 消息类型
     * @param offset 偏移量
     * @param limit 条数
     * @return 消息列表
     */
    List<Message> listMessageByType(long userId, int type, int offset, int limit);

    /**
     * 获取对话未读消息数
     * @param userId 用户ID
     * @param conversationId 对话ID
     * @return 对话未读消息数
     */
    int getConversationUnreadCount(long userId, String conversationId);

    /**
     * 获取某类型对话未读消息数
     * @param userId 用户ID
     * @param type 对话类型
     * @return 未读消息数
     */
    long getMessageUnreadCount(long userId, int type);

    /**
     * 获取未读消息数
     * @param userId 用户ID
     * @return 所有未读消息数
     */
    long getMessageAllUnreadCount(long userId);

    /**
     * 更新消息为已读状态
     * @param userId 用户ID
     * @param type 消息类型
     */
    int updateMessageHasread(long userId, int type);

    /**
     * 更新消息为已读状态
     * @param conversationId 对话ID
     * @param userId 用户ID
     */
    int updatePersonalMessageHasread(@Param("conversationId")String conversationId,
                                     @Param("userId") long userId);
}
