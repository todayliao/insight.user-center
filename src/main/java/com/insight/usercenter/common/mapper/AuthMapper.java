package com.insight.usercenter.common.mapper;

import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.Navigator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/13
 * @remark 权限相关DAL
 */
@Mapper
public interface AuthMapper extends Mapper {

    /**
     * 查询指定ID的应用的令牌生命周期小时数
     *
     * @param appId 应用ID
     * @return 应用的令牌生命周期小时数
     */
    @Select("SELECT token_life FROM ucs_application WHERE id=#{appId};")
    Integer getTokenLife(String appId);

    /**
     * 获取用户全部可用功能集合
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @param deptId   登录部门ID
     * @return Function对象集合
     */
    @Select("SELECT f.id,f.alias,IFNULL(f.interfaces,'') AS interfaces FROM ucs_function f JOIN ucr_role_action a ON a.function_id=f.id " +
            "JOIN (SELECT DISTINCT role_id FROM ucv_user_roles WHERE user_id=#{userId} AND tenant_id=#{tenantId} " +
            "AND (dept_id=#{deptId} OR dept_id IS NULL)) r ON r.role_id=a.role_id GROUP BY f.id HAVING min(a.action)> 0;")
    List<Function> getAllFunctions(@Param("tenantId") String tenantId, @Param("userId") String userId, @Param("deptId") String deptId);

    /**
     * 获取用户可用的导航栏
     *
     * @param tenantId 租户ID
     * @param appId    应用程序ID
     * @param userId   用户ID
     * @param deptId   登录部门ID
     * @return Navigation对象集合
     */
    @Select("SELECT * FROM (SELECT DISTINCT g.id,g.parent_id,g.`index`,g.`name`,g.icon,g.url FROM ucs_navigator g " +
            "JOIN ucs_navigator m ON m.parent_id=g.id JOIN ucs_function f ON f.navigator_id=m.id AND f.is_invisible=0 " +
            "JOIN (SELECT DISTINCT a.function_id FROM ucr_role_action a JOIN ucv_user_roles r ON r.role_id=a.role_id " +
            "WHERE user_id=#{userId} AND tenant_id=#{tenantId} AND (dept_id=#{deptId} OR dept_id IS NULL) " +
            "GROUP BY a.function_id HAVING min(a.action)> 0) a ON a.function_id=f.id WHERE g.application_id=#{appId} UNION " +
            "SELECT m.id,m.parent_id,m.`index`,m.`name`,m.icon,m.url FROM ucs_navigator m JOIN ucs_function f ON f.navigator_id=m.id AND f.is_invisible=0 " +
            "JOIN (SELECT DISTINCT a.function_id FROM ucr_role_action a JOIN ucv_user_roles r ON r.role_id=a.role_id " +
            "WHERE user_id=#{userId} AND tenant_id=#{tenantId} AND (dept_id=#{deptId} OR dept_id IS NULL) GROUP BY a.function_id " +
            "HAVING min(a.action)> 0) a ON a.function_id=f.id WHERE m.application_id=#{appId}) l ORDER BY l.parent_id,l.`index`;")
    List<Navigator> getNavigators(@Param("tenantId") String tenantId, @Param("appId") String appId, @Param("userId") String userId, @Param("deptId") String deptId);

    /**
     * 获取指定模块的全部可用功能集合及对指定用户的授权情况
     *
     * @param tenantId 租户ID
     * @param moduleId 模块ID
     * @param userId   用户ID
     * @param deptId   登录部门ID
     * @return Function对象集合
     */
    @Select("SELECT f.id,f.navigator_id AS parent_id,f.`index`,f.`name`,f.icon,f.url,a.permit FROM ucs_function f " +
            "LEFT JOIN (SELECT a.function_id,min(a.action) AS permit FROM ucr_role_action a JOIN ucv_user_roles r " +
            "ON r.role_id=a.role_id AND r.user_id=#{userId} AND r.tenant_id=#{tenantId} AND (r.dept_id=#{deptId} OR r.dept_id IS NULL) " +
            "GROUP BY a.function_id) a ON a.function_id=f.id WHERE f.navigator_id=#{moduleId} AND f.is_invisible=0 ORDER BY f.`index`;")
    List<Function> getModuleFunctions(@Param("tenantId") String tenantId, @Param("moduleId") String moduleId, @Param("userId") String userId, @Param("deptId") String deptId);

    /**
     * 查询指定租户是否绑定了指定的应用
     *
     * @param tenantId 租户ID
     * @param appId    应用ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM ucb_tenant_app WHERE tenant_id=#{tenantId} AND app_id=#{appId};")
    Integer containsApp(@Param("tenantId") String tenantId, @Param("appId") String appId);

    /**
     * 根据角色id查询功能集合
     *
     * @param roleId 角色id
     * @return
     */
    @Select("SELECT f.`id`,f.`alias`,f.`interfaces`,r.`action` FROM ucr_role_action r INNER JOIN ucs_function f ON r.`function_id`=f.`id` WHERE r.role_id=#{roleid} ")
    List<Function> getRoleFunctionByRoleIds(@Param("roleid") String roleId);

    /**
     * 查询模块功能集合
     *
     * @param list 模块功能id集合
     * @return 受影响行数
     */
    @Select("<script>select * FROM ucs_function WHERE id in " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\"> " +
            "#{item} " +
            "</foreach>;</script>")
    List<Function> getFunctions(List<String> list);
}
