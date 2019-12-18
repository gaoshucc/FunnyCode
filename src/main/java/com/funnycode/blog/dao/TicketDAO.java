package com.funnycode.blog.dao;

import com.funnycode.blog.model.Ticket;
import org.apache.ibatis.annotations.*;

/**
 * @author CC
 * @date 2019-09-18 21:25
 */
@Mapper
public interface TicketDAO {
    String TABLE_NAME = "ticket";
    String INSERT_FIELDS = " user_id, expired, status, ticket ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 创建Ticket
     * @param ticket Ticket
     * @return 影响行数
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{expired},#{status},#{ticket})"})
    int add(Ticket ticket);

    /**
     * 获取Ticket
     * @param ticket token
     * @return Ticket
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    Ticket getByToken(String ticket);

    /**
     * 更新Ticket状态
     * @param ticket token
     * @param status 状态
     */
    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
    long updateStatusByToken(@Param("ticket") String ticket, @Param("status") int status);
}
