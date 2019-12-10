package com.funnycode.blog.dao;

import com.funnycode.blog.model.Reason;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author CC
 * @date 2019-10-04 23:23
 */
@Mapper
public interface ReasonDAO {

    String TABLE_NAME = " reason ";
    String INSERT_FIELDS = " reason_id, reason ";
    String SELECT_FIELDS = " reason_id, reason ";

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " VALUES(#{reasonId}, #{reason})"})
    int addReason(Reason reason);

    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME})
    List<Reason> getAllReasons();
}
