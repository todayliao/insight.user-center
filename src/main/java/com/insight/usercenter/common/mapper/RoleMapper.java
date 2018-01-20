package com.insight.usercenter.common.mapper;

import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.Member;
import com.insight.usercenter.common.entity.Role;
import com.insight.usercenter.role.dto.RoleDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/11
 * @remark 角色相关DAL
 */
@Mapper
public interface RoleMapper extends Mapper {

    /**
     * 获取指定应用的全部角色
     *
     * @param role 角色查询实体对象
     * @return 角色集合
     */
    @Select("<script>SELECT * FROM ucr_role WHERE tenant_id=#{tenantId} " +
            "<if test='appId!=null'>AND application_id=#{appId} </if>" +
            "<if test='name!=null'>AND name LIKE '%${name}%' </if>" +
            "<if test='startDate!=null'>AND created_time >= #{startDate} </if>" +
            "<if test='endDate!=null'>AND created_time &lt; DATE_ADD(#{endDate},INTERVAL 1 day) </if>" +
            "ORDER BY created_time DESC LIMIT #{offset},#{pageSize};</script>")
    List<Role> getRoles(RoleDTO role);

    /**
     * 获取指定ID的角色
     *
     * @param id 角色ID
     * @return 角色实体
     */
    @Select("SELECT * FROM ucr_role WHERE id=#{id}")
    Role getRole(String id);

    /**
     * 获取角色功能
     *
     * @param appId  应用ID
     * @param roleId 角色ID
     * @return 功能集合
     */
    @Select("SELECT * FROM (SELECT g.id,g.parent_id,1 AS type,g.`index`,g.`name`,g.icon,g.url," +
            "CASE WHEN m.max IS NULL THEN NULL WHEN m.max=m.count THEN 1 ELSE 0 END AS action " +
            "FROM ucs_navigator g JOIN (SELECT m.parent_id,sum(a.action) AS max,count(*) AS count " +
            "FROM ucs_navigator m JOIN ucs_function f ON f.navigator_id=m.id " +
            "LEFT JOIN ucr_role_action a ON a.function_id=f.id AND role_id=#{roleId} " +
            "WHERE m.application_id=#{appId} GROUP BY m.parent_id) m ON m.parent_id=g.id UNION " +
            "SELECT m.id,m.parent_id,2 AS type,m.`index`,m.`name`,m.icon,m.url," +
            "CASE WHEN f.max IS NULL THEN NULL WHEN f.max=f.count THEN 1 ELSE 0 END AS action " +
            "FROM ucs_navigator m JOIN (SELECT navigator_id,sum(a.action) AS max,count(*) AS count " +
            "FROM ucs_function f LEFT JOIN ucr_role_action a ON a.function_id=f.id AND role_id=#{roleId} " +
            "GROUP BY f.navigator_id) f ON f.navigator_id=m.id WHERE m.application_id=#{appId} UNION " +
            "SELECT f.id,f.navigator_id AS parent_id,3 AS type,f.`index`,f.`name`,f.icon,f.url,a.action " +
            "FROM ucs_function f LEFT JOIN ucr_role_action a ON a.function_id=f.id AND a.role_id=#{roleId} " +
            "WHERE f.application_id=#{appId}) l ORDER BY l.type,l.parent_id,l.`index`;")
    List<Function> getRoleFunction(@Param("appId") String appId, @Param("roleId") String roleId);

    /**
     * 获取角色成员
     *
     * @param roleId 角色ID
     * @return 成员集合
     */
    @Select("SELECT m.id,m.type,m.member_id,u.`name`,u.remark FROM ucr_role_member m JOIN ucb_user u ON u.id=m.member_id " +
            "WHERE m.type=1 AND m.role_id=#{roleId} UNION " +
            "SELECT m.id,m.type,m.member_id,g.`name`,g.remark FROM ucr_role_member m JOIN ucg_group g ON g.id=m.member_id " +
            "WHERE m.type=2 AND m.role_id=#{roleId} UNION " +
            "SELECT m.id,m.type,m.member_id,o.`name`,o.remark FROM ucr_role_member m JOIN uco_organization o ON o.id=m.member_id " +
            "WHERE m.type=3 AND m.role_id=#{roleId}")
    List<Member> getRoleMember(String roleId);

    /**
     * 获取指定ID的角色的成员用户
     *
     * @param roleId 角色ID
     * @return
     */
    @Select("SELECT u.id,u.`name`,u.account,u.remark FROM ucr_role r JOIN ucr_role_member m ON m.role_id=r.id " +
            "JOIN ucb_user u ON u.id=m.member_id WHERE m.type=1 AND r.id=#{roleId} UNION " +
            "SELECT u.id,u.`name`,u.account,u.remark FROM ucr_role r JOIN ucr_role_member m ON m.role_id=r.id JOIN ucg_group_member g ON g.group_id=m.member_id " +
            "JOIN ucb_user u ON u.id=g.user_id WHERE m.type=2 AND r.id=#{roleId} UNION " +
            "SELECT u.id,u.`name`,u.account,u.remark FROM ucr_role r JOIN ucr_role_member m ON m.role_id=r.id JOIN uco_post_member o ON o.post_id=m.member_id " +
            "JOIN ucb_user u ON u.id=o.user_id WHERE m.type=3 AND r.id=#{roleId};")
    List<UserDTO> getRoleUsers(String roleId);

    /**
     * 获取指定名称的角色的成员用户
     *
     * @param appId    应用ID
     * @param tenantId 租户ID
     * @param roleName 角色名称
     * @return
     */
    @Select("SELECT u.id,u.`name`,u.account,u.remark FROM ucr_role r JOIN ucr_role_member m ON m.role_id=r.id " +
            "JOIN ucb_user u ON u.id=m.member_id WHERE m.type=1 AND r.`name`=#{name} AND r.tenant_id=#{tenantid} AND r.application_id=#{appid} UNION " +
            "SELECT u.id,u.`name`,u.account,u.remark FROM ucr_role r JOIN ucr_role_member m ON m.role_id=r.id JOIN ucg_group_member g ON g.group_id=m.member_id " +
            "JOIN ucb_user u ON u.id=g.user_id WHERE m.type=2 AND r.`name`=#{name} AND r.tenant_id=#{tenantid} AND r.application_id=#{appid} UNION " +
            "SELECT u.id,u.`name`,u.account,u.remark FROM ucr_role r JOIN ucr_role_member m ON m.role_id=r.id JOIN uco_post_member o ON o.post_id=m.member_id " +
            "JOIN ucb_user u ON u.id=o.user_id WHERE m.type=3 AND r.`name`=#{name} AND r.tenant_id=#{tenantid} AND r.application_id=#{appid};")
    List<UserDTO> getRoleUsersByName(@Param("appid") String appId, @Param("tenantid") String tenantId, @Param("name") String roleName);

    /**
     * 获取指定租户下的指定名称的角色数量
     *
     * @param tenantId 租户ID
     * @param name     角色名称
     * @return 角色数量
     */
    @Select("SELECT COUNT(*) FROM ucr_role WHERE tenant_id=#{id} AND `name`=#{name};")
    Integer getRoleCount(@Param("id") String tenantId, @Param("name") String name);

    /**
     * 新增角色
     *
     * @param role 角色实体
     * @return 受影响行数
     */
    @Insert("INSERT ucr_role (id,tenant_id,application_id,`name`,remark,is_builtin,creator_user_id) " +
            "VALUES (#{id},#{tenantId},#{applicationId},#{name},#{remark},#{builtin},#{creatorUserId});")
    Integer addRole(Role role);

    /**
     * 删除角色及其权限和成员
     *
     * @param roleId 角色ID
     * @return 受影响行数
     */
    @Delete("DELETE r,a,m FROM ucr_role r LEFT JOIN ucr_role_action a ON a.role_id=r.id LEFT JOIN ucr_role_member m ON m.role_id=r.id " +
            "WHERE r.id=#{id}")
    Integer deleteRole(@Param("id") String roleId);

    /**
     * 更新角色数据
     *
     * @param role 角色实体
     * @return 受影响行数
     */
    @Update("UPDATE ucr_role SET `name`=#{name},remark=#{remark} WHERE id=#{id}")
    Integer updateRole(Role role);

    /**
     * 添加角色功能
     *
     * @param roleId    角色ID
     * @param functions 功能集合
     * @return 受影响行数
     */
    @Insert("<script>INSERT ucr_role_action(id,role_id,function_id,action) VALUES " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "(REPLACE(uuid(),'-',''),#{roleId},#{item.id},#{item.action}) " +
            "</foreach>;</script>")
    Integer addRoleFunction(@Param("roleId") String roleId, @Param("list") List<Function> functions);

    /**
     * 移除角色功能
     *
     * @param roleId 角色ID
     * @return 受影响行数
     */
    @Delete("delete FROM ucr_role_action WHERE role_id=#{id}")
    Integer removeRoleFunction(@Param("id") String roleId);

    /**
     * 添加角色成员
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 受影响行数
     */
    @Insert("INSERT ucr_role_member(id,`type`,role_id,member_id) VALUES (REPLACE(uuid(),'-',''),1,#{roleId},#{userId});")
    Integer addRoleMember(@Param("roleId") String roleId, @Param("userId") String userId);

    /**
     * 批量添加角色成员
     *
     * @param members 成员集合
     * @return 受影响行数
     */
    @Insert("<script>INSERT ucr_role_member(id,`type`,role_id,member_id) VALUES " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "(REPLACE(uuid(),'-',''),#{item.type},#{item.parentId},#{item.memberId}) " +
            "</foreach>;</script>")
    Integer addRoleMembers(List<Member> members);

    /**
     * 移除角色成员
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 受影响行数
     */
    @Delete("delete FROM ucr_role_member WHERE role_id=#{roleId} and member_id=#{userId};")
    Integer removeRoleMember(@Param("roleId") String roleId, @Param("userId") String userId);

    /**
     * 批量移除角色成员
     *
     * @param list ID集合
     * @return 受影响行数
     */
    @Delete("<script>delete FROM ucr_role_member WHERE id in " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\"> " +
            "#{item} " +
            "</foreach></script>")
    Integer removeRoleMembers(List<String> list);

    /**
     * 根据id查询ucr_role_member 数据集
     *
     * @param list ID集合
     * @return 受影响行数
     */
    @Select("<script>select member_id,role_id AS parent_id FROM ucr_role_member WHERE id in " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\"> " +
            "#{item} " +
            "</foreach></script>")
    List<Member> getRoleMemberByIds(List<String> list);

    /**
     * 根据模块功能id查询角色id和功能相关信息集合
     *
     * @param functionId 模块功能id
     * @return
     */
    @Select("SELECT a.role_id AS id,a.action,b.alias,b.interfaces " +
            " FROM ucr_role_action a LEFT JOIN ucs_function b ON a.function_id = b.`id` WHERE a.function_id=#{functionId}")
    List<Function> getRoleFunctionByFunctionId(String functionId);

    /**
     * 查询ucr_role_action表中所有分配功能function的roleId
     *
     * @return
     */
    @Select("SELECT DISTINCT role_id FROM ucr_role_action a INNER JOIN ucr_role b ON a.role_id=b.id")
    List<String> getAllRoleFromAction();

    /**
     * 获取指定租户类型的角色模板集合
     *
     * @param tenantType 租户类型
     * @return 角色模板集合
     */
    @Select("SELECT * FROM ucr_role WHERE tenant_id IS NULL AND tenant_type=#{tenantType};")
    List<Role> getRoleTemplates(Integer tenantType);

    /**
     * 获取指定ID的角色模板的授权功能ID集合
     *
     * @param roleId 角色ID
     * @return 功能ID集合
     */
    @Select("SELECT function_id AS id, 1 AS action FROM ucr_role_action WHERE role_id=#{roleId};")
    List<Function> getTemplateFunctions(String roleId);
}
