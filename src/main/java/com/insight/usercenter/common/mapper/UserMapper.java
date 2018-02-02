package com.insight.usercenter.common.mapper;

import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.common.entity.Device;
import com.insight.usercenter.common.entity.User;
import com.insight.usercenter.common.entity.UserOpenId;
import com.insight.usercenter.user.dto.QueryUserDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/11
 * @remark 用户相关DAL
 */
@Mapper
public interface UserMapper extends Mapper {

    /**
     * 根据条件查询用户集合(分页)
     *
     * @param user 用户查询对象实体
     * @return 用户集合
     */
    @Results({@Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid")})
    @Select("<script>SELECT id,user_type,code,`name`,account,mobile,open_id,email,remark,is_builtin,is_invalid,created_time " +
            "FROM ucb_user WHERE 0=0 " +
            "<if test='key!=null'>AND (name LIKE '%${key}%' OR account LIKE '%${key}%' OR mobile LIKE '%${key}%' OR email LIKE '%${key}%') </if>" +
            "<if test='name!=null'>AND name LIKE '${name}%' </if>" +
            "<if test='account!=null'>AND account LIKE '${account}%' </if>" +
            "<if test='mobile!=null'>AND mobile LIKE '${mobile}%' </if>" +
            "<if test='email!=null'>AND email LIKE '${email}%' </if>" +
            "<if test='status!=null'>AND is_invalid = #{status} </if>" +
            "<if test='startDate!=null'>AND created_time >= #{startDate} </if>" +
            "<if test='endDate!=null'>AND created_time &lt; DATE_ADD(#{endDate},INTERVAL 1 day) </if>" +
            "ORDER BY created_time DESC</script>")
    List<User> getUsers(QueryUserDTO user);

    /**
     * 根据ID查询用户数据
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    @Results({@Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid")})
    @Select("SELECT id,user_type,`code`,`name`,account,mobile,open_id,email,head_img,remark,is_builtin,is_invalid,created_time " +
            "FROM ucb_user WHERE id=#{userId};")
    User getUserById(String userId);

    /**
     * 根据ID查询用户数据
     *
     * @param userId 用户ID
     * @param appId  微信AppID
     * @return 用户实体
     */
    @Results({@Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid")})
    @Select("SELECT u.id,u.user_type,u.`code`,u.`name`,u.account,u.mobile,o.id AS open_id,u.email,u.head_img,u.remark,u.is_builtin,u.is_invalid,u.created_time " +
            "FROM ucb_user u LEFT JOIN (SELECT id,user_id,max(created_time) FROM ucb_user_openid " +
            "WHERE app_id=#{appId} GROUP BY user_id) o ON o.user_id=u.id WHERE u.id=#{userId};")
    User getUserWithAppId(@Param("userId") String userId, @Param("appId") String appId);

    /**
     * 根据登录账号查询用户数据
     *
     * @param account 登录账号(账号、手机号、E-mail)
     * @return 用户实体
     */
    @Results({@Result(property = "builtin", column = "is_builtin"),
            @Result(property = "invalid", column = "is_invalid")})
    @Select("SELECT * FROM ucb_user WHERE account=#{account} OR mobile=#{account} OR email=#{account} OR open_id=#{account} LIMIT 1;")
    User getUser(String account);

    /**
     * 获取指定ID的用户关联的租户ID集合
     *
     * @param userId 用户ID
     * @return 租户ID集合
     */
    @Select("SELECT tenant_id FROM ucb_tenant_user WHERE user_id=#{userId};")
    List<String> getUserTenant(String userId);

    /**
     * 查询指定账号、手机号、E-mail的用户数量
     *
     * @param account 登录账号
     * @param mobile  手机号
     * @param openId  微信OpenID
     * @param email   E-mail
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM ucb_user WHERE account=#{account} OR mobile=#{mobile} OR open_id=#{openId} OR email=#{email};")
    Integer getExistedUserCount(@Param("account") String account, @Param("mobile") String mobile, @Param("openId") String openId, @Param("email") String email);

    /**
     * 在用户表中新增一条记录
     *
     * @param user 用户实体数据
     * @return 受影响的行数
     */
    @Insert("INSERT ucb_user (id,user_type,code,name,account,mobile,open_id,email,password,paypw,head_img,remark) " +
            "VALUES (#{id},#{userType},#{code},#{name},#{account},#{mobile},#{openId},#{email},#{password},#{paypw},#{headImg},#{remark});")
    Integer addUser(UserDTO user);

    /**
     * 记录用户绑定的微信OpenID
     *
     * @param userOpenId 用户实体数据
     * @return 受影响的行数
     */
    @Insert("REPLACE ucb_user_openid (id,user_id,app_id) VALUES (#{id},#{userId},#{appId});")
    Integer addUserOpenId(UserOpenId userOpenId);

    /**
     * 根据用户ID删除用户
     *
     * @param userId 用户ID
     * @return 受影响的行数
     */
    @Delete("DELETE FROM ucb_user WHERE id=#{userId};")
    Integer deleteUserById(String userId);

    /**
     * 根据用户ID更新指定用户的用户类型
     *
     * @param id       用户ID
     * @param userType 用户类型
     * @return 受影响的行数
     */
    @Update("UPDATE ucb_user SET user_type=#{userType} WHERE id=#{id};")
    Integer updateUserType(@Param("id") String id, @Param("userType") Integer userType);

    /**
     * 根据用户ID更新指定用户的用户名和备注
     *
     * @param id     用户ID
     * @param code   用户编码
     * @param name   用户名
     * @param remark 备注
     * @return 受影响的行数
     */
    @Update("UPDATE ucb_user SET code=#{code}, name=#{name},remark=#{remark} WHERE id=#{id};")
    Integer updateUserInfo(@Param("id") String id, @Param("code") String code, @Param("name") String name, @Param("remark") String remark);

    /**
     * 根据用户ID更新指定用户的手机号、微信OpenID和E-mail
     *
     * @param id     用户ID
     * @param mobile 手机号
     * @return 受影响的行数
     */
    @Update("UPDATE ucb_user SET mobile=#{mobile} WHERE id=#{id};")
    Integer updateMobile(@Param("id") String id, @Param("mobile") String mobile);

    /**
     * 根据用户ID更新指定用户的E-mail
     *
     * @param id    用户ID
     * @param email E-mail
     * @return 受影响的行数
     */
    @Update("UPDATE ucb_user SET email=#{email} WHERE id=#{id};")
    Integer updateEmail(@Param("id") String id, @Param("email") String email);

    /**
     * 更新用户昵称/OpenID/头像
     *
     * @param user 用户对象实体
     */
    @Update("UPDATE ucb_user SET name=#{name},open_id=#{openId},head_img=#{headImg} WHERE id=#{id}")
    void updateWeChatInfo(UserDTO user);

    /**
     * 根据用户ID更新指定用户的登录密码
     *
     * @param id       用户ID
     * @param password 登录密码
     * @return 受影响的行数
     */
    @Update("UPDATE ucb_user SET password=#{password} WHERE id=#{id};")
    Integer updatePassword(@Param("id") String id, @Param("password") String password);

    /**
     * 根据用户ID更新指定用户的支付密码
     *
     * @param id       用户ID
     * @param password 支付密码
     * @return 受影响的行数
     */
    @Update("UPDATE ucb_user SET paypw=#{password} WHERE id=#{id};")
    Integer updatePayPassword(@Param("id") String id, @Param("password") String password);

    /**
     * 根据用户ID更新指定用户的状态
     *
     * @param id     用户ID
     * @param status 状态(false:有效、true:失效)
     * @return 受影响的行数
     */
    @Update("UPDATE ucb_user SET is_invalid =#{status} WHERE id =#{id};")
    Integer updateStatus(@Param("id") String id, @Param("status") Boolean status);

    /**
     * 查询用户绑定设备数据信息
     *
     * @param id 设备ID
     * @return 设备数据信息
     */
    @Select("SELECT * FROM ucb_user_device WHERE id = #{id}")
    Device getDevice(String id);

    /**
     * 查询用户当前使用的设备ID
     *
     * @param userId 用户ID
     * @return 设备ID
     */
    @Select("SELECT id FROM ucb_user_device WHERE is_invalid=0 AND user_id=#{userId};")
    String getUsingDevice(String userId);

    /**
     * 新增用户绑定设备数据
     *
     * @param device 设备数据实体
     */
    @Insert("Insert INTO ucb_user_device (id,user_id,device_model,is_invalid,update_time,created_time)" +
            "VALUES (#{id},#{userId},#{deviceModel},#{isInvalid},#{updateTime},#{createdTime});")
    void addDevice(Device device);

    /**
     * 修改用户绑定设备数据
     *
     * @param device 设备数据实体
     */
    @Update("UPDATE ucb_user_device SET device_model=#{deviceModel},user_id=#{userId},is_invalid=0 WHERE id=#{id};")
    void updateDevice(Device device);

    /**
     * 使在线用户绑定设备失效
     *
     * @param userId 用户ID
     */
    @Update("UPDATE ucb_user_device SET is_invalid=1 WHERE user_id=#{userId}")
    void logOffUser(String userId);

    /**
     * 获取用户关联的全部租户ID
     *
     * @param userId 用户ID
     * @return 租户ID集合
     */
    @Select("SELECT a.id FROM ucb_tenant a JOIN ucb_tenant_user u ON u.tenant_id=a.id AND u.user_id=#{userId} WHERE a.is_invalid=0 AND a.tenant_status=1;")
    List<String> getTenantIds(String userId);

    /**
     * 查询用户所属部门ID集合
     *
     * @param userId 用户ID
     * @return 部门ID集合
     */
    @Select("SELECT DISTINCT o.parent_id FROM uco_organization o JOIN uco_post_member m ON m.post_id=o.id WHERE m.user_id=#{userId};")
    List<String> getDeptIds(String userId);

    /**
     * 获取用户当前有效角色ID集合
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @param deptId   登录部门ID
     * @return 角色集合
     */
    @Select("SELECT DISTINCT role_id FROM ucv_user_roles WHERE user_id=#{userId} AND tenant_id=#{tenantId} AND (dept_id=#{deptId} OR dept_id IS NULL)")
    List<String> getRoleIds(@Param("userId") String userId, @Param("tenantId") String tenantId, @Param("deptId") String deptId);

    /**
     * 查询指定用户的角色ID集合
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    @Select("SELECT DISTINCT role_id FROM ucv_user_roles WHERE user_id = #{userId}")
    List<String> getRoleIdByMemberId(String userId);
}
