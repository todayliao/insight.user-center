package com.insight.usercenter.common.mapper;

import com.insight.usercenter.common.entity.App;
import com.insight.usercenter.common.entity.Member;
import com.insight.usercenter.common.entity.Tenant;
import com.insight.usercenter.tenant.dto.TenantDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/13
 * @remark 租户相关DAL
 */
@Mapper
public interface TenantMapper extends Mapper {

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param tenant 租户查询实体对象
     * @return 租户数据集合
     */
    @Select("<script>SELECT * FROM ucb_tenant WHERE is_invalid=0 " +
            "<if test='name!=null'>AND company_name LIKE '%${name}%' </if>" +
            "<if test='contact!=null'>AND contact_name LIKE '%${contact}%' </if>" +
            "<if test='phone!=null'>AND contact_phone LIKE '%${phone}%' </if>" +
            "<if test='status!=null'>AND tenant_status = #{status} </if>" +
            "<if test='startDate!=null'>AND created_time >= #{startDate} </if>" +
            "<if test='endDate!=null'>AND created_time &lt; DATE_ADD(#{endDate},INTERVAL 1 day) </if>" +
            "ORDER BY created_time DESC LIMIT #{offset},#{pageSize};</script>")
    List<Tenant> getTenants(TenantDTO tenant);

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return 租户实体数据
     */
    @Select("SELECT * FROM ucb_tenant WHERE is_invalid=0 AND id=#{id};")
    Tenant getTenant(@Param("id") String id);

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param tenantId 租户ID
     * @return 应用集合
     */
    @Select("SELECT a.* FROM ucs_application a JOIN ucb_tenant_app r ON r.app_id=a.id AND r.tenant_id=#{tenantId};")
    List<App> getTenantApps(String tenantId);

    /**
     * 获取当前用户管理的全部租户信息
     *
     * @param userId 用户ID
     * @return 租户数据集合
     */
    @Select("SELECT * FROM ucb_tenant WHERE is_invalid=0 AND (manager_user_id=#{userId} OR creator_user_id=#{userId});")
    List<Tenant> getMyTenants(String userId);

    /**
     * 查询当前用户管理的指定ID的租户信息
     *
     * @param id     租户ID
     * @param userId 用户ID
     * @return 租户实体数据
     */
    @Select("SELECT * FROM ucb_tenant WHERE is_invalid=0 AND id=#{id} AND (manager_user_id=#{userId} OR creator_user_id=#{userId});")
    Tenant getMyTenant(@Param("id") String id, @Param("userId") String userId);

    /**
     * 新增租户
     *
     * @param tenant 租户实体数据
     * @return 受影响行数
     */
    @Insert("INSERT ucb_tenant(id,tenant_type,company_name,alias,logo,province,city,county,address,license," +
            "license_image,contact_name,contact_phone,contact_mailbox,manager_user_id,creator_user_id) " +
            "VALUES (#{id},#{tenantType},#{companyName},#{alias},#{logo},#{province},#{city},#{county},#{address},#{license}," +
            "#{licenseImage},#{contactName},#{contactPhone},#{contactMailbox},#{managerUserId},#{creatorUserId});")
    Integer addTenant(Tenant tenant);

    /**
     * 删除指定ID的租户
     *
     * @param id 租户ID
     * @return 受影响行数
     */
    @Update("UPDATE ucb_tenant SET is_invalid=1 WHERE id=#{id};")
    Integer deleteTenant(String id);

    /**
     * 更新租户数据
     *
     * @param tenant 租户实体数据
     * @return 受影响行数
     */
    @Update("UPDATE ucb_tenant SET company_name=#{companyName},alias=#{alias},logo=#{logo},province=#{province},city=#{city},county=#{county},address=#{address}," +
            "license=#{license},license_image=#{licenseImage},contact_name=#{contactName},contact_phone=#{contactPhone},contact_mailbox=#{contactMailbox},description=#{description} " +
            "WHERE is_invalid=0 AND id=#{id};")
    Integer updateTenant(Tenant tenant);

    /**
     * 审核租户
     *
     * @param tenant 租户实体数据
     * @return 受影响行数
     */
    @Update("UPDATE ucb_tenant SET description=#{description},bd_charger=#{bdCharger},cs_charger=#{csCharger},tenant_status=#{tenantStatus}," +
            "audit_user_id=#{auditUserId},audit_time=NOW() WHERE is_invalid=0 AND id=#{id};")
    Integer auditTenant(Tenant tenant);

    /**
     * 设置租户管理人
     *
     * @param tenant 租户实体数据
     * @return 受影响行数
     */
    @Update("UPDATE ucb_tenant SET manager_user_id=#{managerUserId} WHERE is_invalid=0 AND id=#{id} " +
            "AND (manager_user_id=#{creatorUserId} OR creator_user_id=#{creatorUserId});")
    Integer divertTenant(Tenant tenant);

    /**
     * 批量新增租户-应用关系
     *
     * @param tenantId 租户ID
     * @param appIds   应用ID集合
     * @return 受影响行数
     */
    @Insert("<script>INSERT ucb_tenant_app (id,tenant_id,app_id) VALUES " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "(REPLACE(UUID(),'-',''),#{tenantId},#{item})" +
            "</foreach>;</script>")
    Integer addAppsToTenant(@Param("tenantId") String tenantId, @Param("list") List<String> appIds);

    /**
     * 获取未绑定指定租户的用户ID
     *
     * @param tenantId 租户ID
     * @param members  角色成员
     * @return 用户ID集合
     */
    @Select("<script>SELECT u.id FROM ucb_user u LEFT JOIN ucb_tenant_user r ON r.user_id=u.id AND r.tenant_id=#{tenantId} " +
            "WHERE r.id IS NULL AND u.id IN " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\"> " +
            "#{item} " +
            "</foreach>;</script>")
    List<String> getUnbindingUser(@Param("tenantId") String tenantId, @Param("list") List<Member> members);

    /**
     * 新增租户-用户关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 受影响行数
     */
    @Insert("INSERT ucb_tenant_user(id,tenant_id,user_id) VALUES (REPLACE(UUID(),'-',''),#{tenantId},#{userId});")
    Integer addUserToTenant(@Param("tenantId") String tenantId, @Param("userId") String userId);

    /**
     * 批量新增租户-用户关系
     *
     * @param tenantId 租户ID
     * @param userIds  用户ID集合
     * @return 受影响行数
     */
    @Insert("<script>INSERT ucb_tenant_user(id,tenant_id,user_id) VALUES " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "(REPLACE(UUID(),'-',''),#{tenantId},#{item})" +
            "</foreach>;</script>")
    Integer addUsersToTenant(@Param("tenantId") String tenantId, @Param("list") List<String> userIds);

    /**
     * 删除指定租户ID的全部应用绑定关系
     *
     * @param tenantId 租户ID
     * @return 受影响行数
     */
    @Delete("DELETE FROM ucb_tenant_app WHERE tenant_id=#{tenantId};")
    Integer removeAppsFromTenant(String tenantId);

    /**
     * 删除指定ID的用户与指定ID的租户的绑定关系
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 受影响行数
     */
    @Delete("DELETE FROM ucb_tenant_user WHERE tenant_id=#{tenantId} AND user_id=#{userId};")
    Integer removeUserFromTenant(@Param("tenantId") String tenantId, @Param("userId") String userId);

    /**
     * 指定ID的用户与指定ID的租户的绑定关系是否存在
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 符合条件的数据行数
     */
    @Select("SELECT COUNT(*) FROM ucb_tenant_user WHERE tenant_id=#{tenantId} AND user_id=#{userId};")
    Integer getTenantUser(@Param("tenantId") String tenantId, @Param("userId") String userId);
}
