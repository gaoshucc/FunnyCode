package com.funnycode.blog.dao;

import com.funnycode.blog.model.AccountBinding;
import org.apache.ibatis.annotations.*;

/**
 * @author CC
 * @date 2019-10-26 19:07
 */
@Mapper
public interface AccountBindingDAO {
    String TABLE_NAME = "account_binding";
    String INSERT_FIELDS = " user_id, third_id, type ";
    String SELECT_FIELDS = INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{thirdId},#{type})"})
    int addAccountBinding(AccountBinding binding);

    @Delete({"DELETE FROM ", TABLE_NAME, " WHERE user_id=#{userId} AND type=#{type}"})
    int removeAccountBinding(Long userId, Integer type);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where third_id=#{thirdId} AND type=#{type}"})
    AccountBinding getAccountBinding(String thirdId, Integer type);

    @Select({"select count(*) from ", TABLE_NAME, " where user_id=#{userId} AND type=#{type}"})
    int getAccountBindingState(Long userId, Integer type);
}
