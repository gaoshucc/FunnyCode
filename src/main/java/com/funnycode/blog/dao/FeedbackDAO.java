package com.funnycode.blog.dao;

import com.funnycode.blog.model.Feedback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author CC
 * @date 2019-10-28 23:53
 */
@Mapper
public interface FeedbackDAO {
    String TABLE_NAME = " feedback ";
    String INSERT_FIELDS = " content, user_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " VALUES(#{content}, #{userId})"})
    int addFeedback(Feedback feedback);
}
