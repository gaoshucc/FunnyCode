package com.funnycode.blog.dao;

import com.funnycode.blog.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author CC
 * @date 2019-11-03 10:46
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type, comment_cnt, forword_cnt ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 增加动态
     * @param feed 动态
     * @return 影响行数
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{data},#{createdDate},#{type},#{commentCnt},#{forwordCnt})"})
    @Options(useGeneratedKeys=true, keyColumn="id")
    int add(Feed feed);

    /**
     * 获取单个动态
     * @param id 动态编号
     * @return 动态
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getById(long id);

    /**
     * 获取用户的动态列表
     * @param maxId 当前已加载动态最大编号
     * @param userIds 关注用户id列表
     * @param count 获取数据条数
     * @return 动态列表
     */
    List<Feed> findAllByIds(@Param("maxId") long maxId,
                               @Param("userIds") List<Long> userIds,
                               @Param("count") long count);

    /**
     * 更新动态点赞数
     * @param feedId 动态编号
     * @param offset 偏移量
     * @return 影响行数
     */
    @Update({"UPDATE ", TABLE_NAME, " SET comment_cnt=comment_cnt+#{offset} WHERE id=#{feedId}"})
    int updateCommentCntById(long feedId, long offset);

    /**
     * 更新动态转发数
     * @param feedId 动态ID
     * @param offset 偏移量
     * @return 影响行数
     */
    @Update({"UPDATE ", TABLE_NAME, " SET forword_cnt=forword_cnt+#{offset} WHERE id=#{feedId}"})
    int updateForwordCntById(long feedId, long offset);

    /**
     * 删除动态
     * @param feedId 动态编号
     * @return 影响行数
     */
    @Delete({"delete from ", TABLE_NAME, " where id=#{feedId}"})
    int removeById(Long feedId);
}
