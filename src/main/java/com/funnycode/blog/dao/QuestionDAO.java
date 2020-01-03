package com.funnycode.blog.dao;

import com.funnycode.blog.model.QuestType;
import com.funnycode.blog.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author CC
 * @date 2020-01-02 22:20
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = "question";
    String INSERT_FIELDS = " title, type, user_id, create_time, status, content, answer_cnt ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 获取问题类型
     * @return 问题类型列表
     */
    @Select({"SELECT type_id, type_name FROM question_type"})
    List<QuestType> findAllType();

    /**
     * 创建问题
     * @param question 问题
     * @return 影响行数
     */
    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " VALUES(#{title}, #{type}, #{userId}, #{createTime}, #{status}, #{content}, #{answerCnt})"})
    int add(Question question);
}
