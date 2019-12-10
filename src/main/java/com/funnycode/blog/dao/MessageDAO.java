package com.funnycode.blog.dao;

import com.funnycode.blog.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * @author CC
 * @date 2019-09-23 00:44
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date, type";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 创建消息
     * @param message 消息
     * @return 影响行数
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate},#{type})"})
    int addMessage(Message message);

    /**
     * 获取对话消息条数
     * @param conversationId 对话ID
     * @return 数据条数
     */
    @Select({"select count(*) from ", TABLE_NAME, " where conversation_id=#{conversationId}"})
    int getMessageCount(@Param("conversationId")String conversationId);

    /**
     * 获取对话详情
     * @param conversationId 对话编号
     * @param offset 偏移量
     * @param limit 查询消息数据数
     * @return 消息列表
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            " where conversation_id=#{conversationId} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetailInit(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    /**
     * 获取对话详情
     * @param conversationId 对话编号
     * @param offset 偏移量
     * @param limit 查询消息数据数
     * @param firstTime 页面最早消息的创建时间
     * @return 消息列表
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            " where conversation_id=#{conversationId} and created_date<#{firstTime} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit,
                                        @Param("firstTime") Date firstTime);

    /**
     * 获取对话详情
     * @param conversationId 对话编号
     * @param lastTime 页面最新消息的创建时间
     * @return 消息列表
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            " where conversation_id=#{conversationId} and created_date>#{lastTime} order by created_date ASC"})
    List<Message> getConversationDetailRegular(@Param("conversationId") String conversationId,
                                               @Param("lastTime")Date lastTime);

    /**
     * 获取所有对话(子查询加入limit是为了防止子查询group by被优化)
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 对话数据数
     * @return 对话列表
     */
    @Select({"select ", INSERT_FIELDS, " , count(id) as id from ( select * from ", TABLE_NAME,
            " where (from_id=#{userId} or to_id=#{userId}) AND type=1 order by created_date desc limit 111111111) tt group by conversation_id order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") long userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    /**
     * 获取用户某个类型的消息
     * @param userId 用户ID
     * @param type 消息类型
     * @param offset 偏移量
     * @param limit 条数
     * @return 消息列表
     */
    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " WHERE to_id=#{userId} AND type=#{type} ORDER BY created_date DESC LIMIT #{offset},#{limit}"})
    List<Message> listMessageByType(@Param("userId") long userId,
                                    @Param("type") int type,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    /**
     * 获取对话未读消息数
     * @param userId 用户ID
     * @param conversationId 对话ID
     * @return 对话未读消息数
     */
    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId") long userId, @Param("conversationId") String conversationId);

    /**
     * 获取某类型系统消息未读消息数
     * @param userId 用户ID
     * @param type 类型
     * @return 未读消息数
     */
    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and type = #{type}"})
    int getMessageUnreadCount(@Param("userId") long userId, @Param("type") int type);

    /**
     * 获取所有未读消息数
     * @param userId 用户ID
     * @return 所有未读消息数
     */
    @Select({"select count(id) from ", TABLE_NAME, " where to_id=#{userId} and has_read=0"})
    int getMessageAllUnreadCount(@Param("userId") long userId);

    /**
     * 更新消息为已读状态
     * @param userId 用户ID
     * @param type 消息类型
     * @return 影响行数
     */
    @Update({"UPDATE ", TABLE_NAME, " SET has_read=1 WHERE to_id=#{userId} AND type=#{type}"})
    int updateMessageHasread(@Param("userId") long userId, @Param("type") int type);

    /**
     * 更新对话消息为已读状态
     * @param conversationId 对话ID
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update({"UPDATE ", TABLE_NAME, " SET has_read=1 WHERE conversation_id=#{conversationId} AND to_id=#{userId} AND has_read=0"})
    int updatePersonalMessageHasread(@Param("conversationId")String conversationId,
                                     @Param("userId") long userId);
}
