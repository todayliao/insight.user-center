package com.apin.usercenter.common.mapper;

import com.apin.util.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends Mapper {

    /**
     * 根据ID查询用户数据
     *
     * @param id 用户ID
     * @return 用户实体
     */
    @Results({
            @Result(property = "applicationId", column = "application_id"),
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid"),
            @Result(property = "createdTime", column = "created_time")
    })
    @Select("select * from user where id=#{id};")
    User getUserById(@Param("id") String id);

    /**
     * 根据登录账号查询用户数据
     *
     * @param appId   应用ID
     * @param account 登录账号(账号、手机号、E-mail)
     * @return 用户实体
     */
    @Results({
            @Result(property = "applicationId", column = "application_id"),
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid"),
            @Result(property = "createdTime", column = "created_time")
    })
    @Select("select * from user where application_id=#{appid} and (account=#{account} or mobile=#{account} or email=#{account}) limit 1;")
    User getUserByAccount(@Param("appid") String appId, @Param("account") String account);

    /**
     * 查询指定账号、手机号、E-mail的用户数量
     *
     * @param appId   应用ID
     * @param account 登录账号
     * @param mobile  手机号
     * @param email   E-mail
     * @return 用户数量
     */
    @Select("select count(*) from user where application_id=#{appid} and (account=#{account} or mobile=#{mobile} or email=#{email});")
    Integer getExistedUserByApp(@Param("appid") String appId, @Param("account") String account, @Param("mobile") String mobile, @Param("email") String email);

    /**
     * 根据账户ID查询在指定应用下的用户总数
     *
     * @param appId 应用ID
     * @return 用户总数
     */
    @Select("select count(*) from user where user_type > 0 and application_id=#{appid};")
    Integer getUsersCountByApp(@Param("appid") String appId);

    /**
     * 根据账户ID查询在指定应用下的用户集合(分页)
     *
     * @param appId  应用ID
     * @param offset 偏移量
     * @param count  最大行数
     * @return 用户集合
     */
    @Results({
            @Result(property = "applicationId", column = "application_id"),
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid"),
            @Result(property = "createdTime", column = "created_time")
    })
    @Select("select id,application_id,account_id,user_type,`name`,account,mobile,email,remark,is_builtin,is_invalid,created_time " +
            "from user where user_type > 0 and application_id=#{appid} order by created_time limit #{offset},#{count};")
    List<User> getUsersByApp(@Param("appid") String appId, @Param("offset") Integer offset, @Param("count") Integer count);

    /**
     * 根据账户ID查询在指定账户下的用户总数
     *
     * @param accountId 账户ID
     * @return 用户总数
     */
    @Select("select count(*) from user where user_type > 0 and account_id=#{accountId};")
    Integer getUsersCountByAccount(@Param("accountId") String accountId);

    /**
     * 根据账户ID查询在指定账户下的用户集合(分页)
     *
     * @param accountId 账户ID
     * @param offset    偏移量
     * @param count     最大行数
     * @return 用户集合
     */
    @Results({
            @Result(property = "applicationId", column = "application_id"),
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid"),
            @Result(property = "createdTime", column = "created_time")
    })
    @Select("select id,application_id,account_id,user_type,`name`,account,mobile,email,remark,is_builtin,is_invalid,created_time " +
            "from user where user_type > 0 and account_id=#{accountId} order by created_time limit #{offset},#{count};")
    List<User> getUsersByAccount(@Param("accountId") String accountId, @Param("offset") Integer offset, @Param("count") Integer count);

    /**
     * 根据用户组ID查询在指定用户组中的用户总数
     *
     * @param groupId 用户组ID
     * @return 用户总数
     */
    @Select("select count(*) from user u join group_member m on m.user_id=u.id and m.group_id=#{groupid};")
    Integer getUsersCountByGroup(@Param("groupid") String groupId);

    /**
     * 根据用户组ID查询在指定用户组中的用户集合(分页)
     *
     * @param groupId 用户组ID
     * @param offset  偏移量
     * @param count   最大行数
     * @return 用户集合
     */
    @Results({
            @Result(property = "applicationId", column = "application_id"),
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid"),
            @Result(property = "createdTime", column = "created_time")
    })
    @Select("select u.id,u.application_id,u.account_id,u.user_type,u.`name`,u.account,u.mobile,u.email,u.remark,u.is_builtin,u.is_invalid,u.created_time " +
            "from user u join group_member m on m.user_id=u.id where m.group_id=#{groupid} order by u.created_time limit #{offset},#{count};")
    List<User> getUsersByGroup(@Param("groupid") String groupId, @Param("offset") Integer offset, @Param("count") Integer count);

    /**
     * 在用户表中新增一条记录
     *
     * @param user 用户实体数据
     * @return 受影响的行数
     */
    @Insert("insert user(id,application_id,account_id,user_type,code,name,account,mobile,email,password,paypw,remark,is_builtin,is_invalid,created_time) " +
            "values(#{id},#{applicationId},#{accountId},#{userType},#{code},#{name},#{account},#{mobile},#{email},#{password},#{paypw},#{remark},#{builtin},#{invalid},#{createdTime});")
    Integer addUser(User user);

    /**
     * 根据用户ID删除用户
     *
     * @param userId 用户ID
     * @return 受影响的行数
     */
    @Delete("delete from user where id=#{id};")
    Integer deleteUserById(@Param("id") String userId);

    /**
     * 根据用户ID更新指定用户的用户类型
     *
     * @param id       用户ID
     * @param userType 用户类型
     * @return 受影响的行数
     */
    @Update("update user set user_type=#{userType} where id=#{id};")
    Integer updateUserType(@Param("id") String id, @Param("userType") Integer userType);

    /**
     * 根据用户ID更新指定用户的手机号和E-mail
     *
     * @param id     用户ID
     * @param mobile 手机号
     * @param email  E-mail
     * @return 受影响的行数
     */
    @Update("update user set mobile=#{mobile}, email=#{email} where id=#{id};")
    Integer updateAccount(@Param("id") String id, @Param("mobile") String mobile, @Param("email") String email);

    /**
     * 根据用户ID更新指定用户的用户名和备注
     *
     * @param id     用户ID
     * @param name   用户名
     * @param remark 备注
     * @return 受影响的行数
     */
    @Update("update user set name=#{name},remark=#{remark} where id=#{id};")
    Integer updateUserInfo(@Param("id") String id, @Param("name") String name, @Param("remark") String remark);

    /**
     * 根据用户ID更新指定用户的登录密码
     *
     * @param id       用户ID
     * @param password 登录密码
     * @return 受影响的行数
     */
    @Update("update user set password=#{password} where id=#{id};")
    Integer updatePassword(@Param("id") String id, @Param("password") String password);

    /**
     * 根据用户ID更新指定用户的支付密码
     *
     * @param id       用户ID
     * @param password 支付密码
     * @return 受影响的行数
     */
    @Update("update user set paypw=#{password} where id=#{id};")
    Integer updatePayPassword(@Param("id") String id, @Param("password") String password);

    /**
     * 根据用户ID更新指定用户的状态
     *
     * @param id     用户ID
     * @param status 状态(false:有效、true:失效)
     * @return 受影响的行数
     */
    @Update("update user set is_invalid =#{status} where id =#{id};")
    Integer updateStatus(@Param("id") String id, @Param("status") Boolean status);

}

