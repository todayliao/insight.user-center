package com.apin.usercenter.common.mapper;

import com.apin.usercenter.common.entity.Function;
import com.apin.usercenter.common.entity.Member;
import com.apin.usercenter.common.entity.Role;
import com.apin.util.pojo.User;
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
     * @param appId 应用ID
     * @return 角色集合
     */
    @Results({@Result(property = "builtin", column = "is_builtin")})
    @Select("SELECT * FROM role WHERE application_id=#{appid} ORDER BY created_time;")
    List<Role> getRoles(@Param("appid") String appId);

    /**
     * 获取指定ID的角色
     *
     * @param id 角色ID
     * @return 角色实体
     */
    @Results({@Result(property = "builtin", column = "is_builtin")})
    @Select("select * from role where id = #{id}")
    Role getRoleById(@Param("id") String id);

    /**
     * 获取角色功能
     *
     * @param appId  应用ID
     * @param roleId 角色ID
     * @return 功能集合
     */
    @Select("SELECT * FROM (" +
            "SELECT g.id,NULL AS parent_id,1 AS type,g.`index`,g.`name`,g.icon,NULL AS url," +
            "CASE WHEN m.max IS NULL THEN NULL WHEN m.max=m.count THEN 1 ELSE 0 END AS action FROM module_group g JOIN (" +
            "SELECT m.group_id,sum(a.action) AS max,count(*) AS count FROM module m " +
            "JOIN module_function f ON f.module_id=m.id " +
            "LEFT JOIN role_action a ON a.function_id=f.id AND role_id=#{id} " +
            "GROUP BY m.group_id) m ON m.group_id=g.id WHERE g.application_id=#{appid} UNION " +
            "SELECT m.id,m.group_id AS parent_id,2 AS type,m.`index`,m.`name`,m.icon,m.url," +
            "CASE WHEN f.max IS NULL THEN NULL WHEN f.max=f.count THEN 1 ELSE 0 END AS action FROM module m " +
            "JOIN module_group g ON g.id=m.group_id AND g.application_id=#{appid} JOIN (" +
            "SELECT module_id,sum(a.action) AS max,count(*) AS count FROM module_function f " +
            "LEFT JOIN role_action a ON a.function_id=f.id AND role_id=#{id} " +
            "WHERE f.is_invisible=0 GROUP BY f.module_id) f ON f.module_id=m.id UNION " +
            "SELECT f.id,module_id AS parent_id,3 AS type,f.`index`,f.`name`,f.icon,f.url,a.action FROM module_function f " +
            "JOIN module m ON m.id=f.module_id JOIN module_group g ON g.id=m.group_id AND g.application_id=#{appid} " +
            "LEFT JOIN role_action a ON a.function_id=f.id AND a.role_id=#{id} WHERE f.is_invisible=0) l " +
            "ORDER BY l.parent_id,l.`index`;")
    List<Function> getRoleFunction(@Param("appid") String appId, @Param("id") String roleId);

    /**
     * 获取角色成员
     *
     * @param roleId 角色ID
     * @return 成员集合
     */
    @Select("SELECT m.id,m.type,m.member_id,u.`name`,u.remark FROM role_member m JOIN `user` u ON u.id=m.member_id " +
            "WHERE m.type=1 AND m.role_id=#{id} UNION " +
            "SELECT m.id,m.type,m.member_id,g.`name`,g.remark FROM role_member m JOIN `group` g ON g.id=m.member_id " +
            "WHERE m.type=2 AND m.role_id=#{id} UNION " +
            "SELECT m.id,m.type,m.member_id,o.`name`,o.remark FROM role_member m JOIN organization o ON o.id=m.member_id " +
            "WHERE m.type=3 AND m.role_id=#{id};")
    List<Member> getRoleMember(String roleId);

    /**
     * 获取指定账户下的指定名称的角色数量
     *
     * @param accountId 账户ID
     * @param name      角色名称
     * @return 角色数量
     */
    @Select("SELECT COUNT(*) FROM role WHERE account_id=#{id} AND `name`=#{name};")
    Integer getRoleCount(@Param("id") String accountId, @Param("name") String name);

    /**
     * 新增角色
     *
     * @param role 角色实体
     * @return 受影响行数
     */
    @Insert("INSERT role (id,application_id,account_id,`name`,remark,is_builtin,creator_user_id) " +
            "VALUES (#{id},#{applicationId},#{accountId},#{name},#{remark},#{builtin},#{creatorUserId});")
    Integer addRole(Role role);

    /**
     * 删除角色及其权限和成员
     *
     * @param roleId 角色ID
     * @return 受影响行数
     */
    @Delete("DELETE r,a,m FROM role r LEFT JOIN role_action a ON a.role_id=r.id LEFT JOIN role_member m ON m.role_id=r.id " +
            "WHERE r.id=#{id}")
    Integer deleteRole(@Param("id") String roleId);

    /**
     * 更新角色数据
     *
     * @param role 角色实体
     * @return 受影响行数
     */
    @Update("UPDATE role SET `name`=#{name},remark=#{remark} WHERE id=#{id}")
    Integer updateRole(Role role);

    /**
     * 添加角色功能
     *
     * @param role
     * @return 受影响行数
     */
    @Insert("<script>INSERT role_action(id,role_id,function_id,action) VALUES " +
            "<foreach collection = \"functions\" item = \"item\" index = \"index\" separator = \",\"> " +
            "(REPLACE(uuid(),'-',''),#{id},#{item.id},#{item.action}) " +
            "</foreach></script>")
    Integer addRoleFunction(Role role);

    /**
     * 移除角色功能
     *
     * @param roleId 角色ID
     * @return 受影响行数
     */
    @Delete("delete from role_action where role_id=#{id}")
    Integer removeRoleFunction(@Param("id") String roleId);

    /**
     * 添加角色成员
     *
     * @param members 成员集合
     * @return 受影响行数
     */
    @Insert("<script>INSERT role_member(id,`type`,role_id,member_id) VALUES " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\"> " +
            "(#{item.id},#{item.type},#{item.parentId},#{item.memberId}) " +
            "</foreach></script>")
    Integer addRoleMember(List<Member> members);

    /**
     * 移除角色成员
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 受影响行数
     */
    @Delete("delete from role_member where role_id=#{roleId} and member_id=#{userId};")
    Integer removeRoleMember(@Param("roleId") String roleId, @Param("userId") String userId);

    /**
     * 移除角色成员
     *
     * @param userId 用户ID
     * @return 受影响行数
     */
    @Delete("delete from role_member where member_id=#{userId};")
    Integer removeRoleMemberByUserId(@Param("userId") String userId);

    /**
     * 批量移除角色成员
     *
     * @param list ID集合
     * @return 受影响行数
     */
    @Delete("<script>delete from role_member where id in" +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\"> " +
            "#{item} " +
            "</foreach></script>")
    Integer removeRoleMembers(List<String> list);

    /**
     * 获取指定名称的角色的成员用户
     *
     * @param appId     应用ID
     * @param accountId 账户ID
     * @param roleName  角色名称
     * @return
     */
    @Select("SELECT u.id,u.`name`,u.account,u.remark FROM role r JOIN role_member m ON m.role_id=r.id " +
            "JOIN `user` u ON u.id=m.member_id WHERE m.type=1 AND r.`name`=#{name} AND r.account_id=#{accountid} AND r.application_id=#{appid} UNION " +
            "SELECT u.id,u.`name`,u.account,u.remark FROM role r JOIN role_member m ON m.role_id=r.id JOIN group_member g ON g.group_id=m.member_id " +
            "JOIN `user` u ON u.id=g.user_id WHERE m.type=2 AND r.`name`=#{name} AND r.account_id=#{accountid} AND r.application_id=#{appid} UNION " +
            "SELECT u.id,u.`name`,u.account,u.remark FROM role r JOIN role_member m ON m.role_id=r.id JOIN post_member o ON o.post_id=m.member_id " +
            "JOIN `user` u ON u.id=o.user_id WHERE m.type=3 AND r.`name`=#{name} AND r.account_id=#{accountid} AND r.application_id=#{appid};")
    List<User> getRoleUsersByName(@Param("appid") String appId, @Param("accountid") String accountId, @Param("name") String roleName);
}
