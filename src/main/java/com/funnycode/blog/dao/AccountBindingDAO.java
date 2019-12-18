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

    /**
     * 创建第三方绑定账户
     * @param binding 绑定账号
     * @return 影响行数
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{thirdId},#{type})"})
    int add(AccountBinding binding);

    /**
     * 删除第三方绑定账号
     * @param userId 用户ID
     * @param type 第三方账号类型
     * @return 影响行数
     */
    @Delete({"DELETE FROM ", TABLE_NAME, " WHERE user_id=#{userId} AND type=#{type}"})
    int removeByUserIdAndType(Long userId, Integer type);

    /**
     * 获取第三方绑定账号
     * @param thirdId 第三方账号id
     * @param type 第三方账号类型
     * @return 第三方账号
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where third_id=#{thirdId} AND type=#{type}"})
    AccountBinding getByThirdIdAndType(String thirdId, Integer type);

    /**
     * 第三方绑定账号是否存在
     * @param userId 用户id
     * @param type 第三方账号类型
     * @return 第三方绑定账号数
     */
    @Select({"select count(*) from ", TABLE_NAME, " where user_id=#{userId} AND type=#{type}"})
    int existsByUserIdAndType(Long userId, Integer type);
}
