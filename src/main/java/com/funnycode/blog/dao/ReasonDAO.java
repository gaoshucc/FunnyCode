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

    /**
     * 创建举报原因
     * @param reason 举报原因
     * @return 影响行数
     */
    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " VALUES(#{reasonId}, #{reason})"})
    int add(Reason reason);

    /**
     * 获取举报原因列表
     * @return 举报原因列表
     */
    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME})
    List<Reason> findAll();
}
