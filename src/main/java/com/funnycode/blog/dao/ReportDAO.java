package com.funnycode.blog.dao;

import com.funnycode.blog.model.Report;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author CC
 * @date 2019-10-04 22:11
 */
@Mapper
public interface ReportDAO {
    String TABLE_NAME = " report ";
    String INSERT_FIELDS = " actor_id, entity_type, entity_id, reasons, description, report_time, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " VALUES(#{actorId}, #{entityType}, #{entityId}, #{reasons}, #{description}, #{reportTime}, #{status})"})
    int addReport(Report report);
}
