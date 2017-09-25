package com.apin.usercenter.common.mapper;

import com.apin.usercenter.common.entity.Function;
import com.apin.usercenter.common.entity.Navigator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/13
 * @remark
 */
@Mapper
public interface AuthMapper extends Mapper {

    /**
     * 获取用户全部可用功能集合
     *
     * @param appId  应用ID
     * @param userId 用户ID
     * @param deptId 登录部门ID
     * @return Function对象集合
     */
    @Select("SELECT f.id,f.alias,u.url FROM module_function f JOIN module m ON m.id=f.module_id " +
            "JOIN module_group g ON g.id=m.group_id AND g.application_id=#{appid} JOIN role_action a ON a.function_id=f.id " +
            "JOIN (SELECT m.role_id FROM role_member m WHERE m.member_id=#{userid} AND m.type=1 UNION " +
            "SELECT m.role_id FROM role_member m JOIN group_member g ON g.group_id=m.member_id WHERE g.user_id=#{userid} AND m.type=2 UNION " +
            "SELECT m.role_id FROM role_member m JOIN post_member p ON p.post_id=m.member_id JOIN organization o ON o.id=p.post_id " +
            "WHERE p.user_id=#{userid} AND o.parent_id=#{deptid} AND m.type=3) r ON r.role_id=a.role_id " +
            "LEFT JOIN function_url u ON u.`function`=f.alias WHERE f.is_invisible=0 GROUP BY f.id,f.url HAVING min(a.action)> 0;")
    List<Function> getAllFunctions(@Param("appid") String appId, @Param("userid") String userId, @Param("deptid") String deptId);

    /**
     * 获取用户可用的导航栏
     *
     * @param appId  应用程序ID
     * @param userId 用户ID
     * @param deptId 登录部门ID
     * @return Navigation对象集合
     */
    @Select("SELECT*FROM (SELECT g.id,NULL AS parent_id,g.`index`,g.`name`,g.icon,NULL AS url FROM module_group g " +
            "JOIN (SELECT m.group_id FROM module m JOIN module_function f ON f.module_id=m.id " +
            "JOIN (SELECT a.function_id FROM role_action a " +
            "JOIN (SELECT m.role_id FROM role_member m WHERE m.member_id=#{userid} AND m.type=1 UNION " +
            "SELECT m.role_id FROM role_member m JOIN group_member g ON g.group_id=m.member_id WHERE g.user_id=#{userid} AND m.type=2 UNION " +
            "SELECT m.role_id FROM role_member m JOIN post_member p ON p.post_id=m.member_id JOIN organization o ON o.id=p.post_id " +
            "WHERE p.user_id=#{userid} AND o.parent_id=#{deptid} AND m.type=3) r ON r.role_id=a.role_id " +
            "GROUP BY a.function_id HAVING min(a.action)> 0) a ON a.function_id=f.id " +
            "WHERE f.is_invisible=0 GROUP BY m.group_id) m ON m.group_id=g.id WHERE g.application_id=#{appid} UNION " +
            "SELECT m.id,g.id AS parent_id,m.`index`,m.`name`,m.icon,m.url FROM module m " +
            "JOIN module_group g ON g.id=m.group_id AND g.application_id=#{appid} " +
            "JOIN (SELECT f.module_id FROM module_function f JOIN (SELECT a.function_id FROM role_action a " +
            "JOIN (SELECT m.role_id FROM role_member m WHERE m.member_id=#{userid} AND m.type=1 UNION " +
            "SELECT m.role_id FROM role_member m JOIN group_member g ON g.group_id=m.member_id WHERE g.user_id=#{userid} AND m.type=2 UNION " +
            "SELECT m.role_id FROM role_member m JOIN post_member p ON p.post_id=m.member_id JOIN organization o ON o.id=p.post_id " +
            "WHERE p.user_id=#{userid} AND o.parent_id=#{deptid} AND m.type=3) r ON r.role_id=a.role_id " +
            "GROUP BY a.function_id HAVING min(a.action)> 0) a ON a.function_id=f.id " +
            "WHERE f.is_invisible=0 GROUP BY f.module_id) f ON f.module_id=m.id) l ORDER BY l.parent_id,l.`index`;")
    List<Navigator> getNavigators(@Param("appid") String appId, @Param("userid") String userId, @Param("deptid") String deptId);

    /**
     * 获取指定模块的全部可用功能集合及对指定用户的授权情况
     *
     * @param moduleId 模块ID
     * @param userId   用户ID
     * @param deptId   登录部门ID
     * @return Function对象集合
     */
    @Select("SELECT f.id,f.module_id,f.`index`,f.`name`,f.icon,f.url,a.permit FROM module_function f " +
            "JOIN module m ON m.id=f.module_id JOIN module_group g ON g.id=m.group_id " +
            "LEFT JOIN (SELECT a.function_id,min(a.action) AS permit FROM role_action a " +
            "JOIN (SELECT m.role_id FROM role_member m WHERE m.member_id=#{userid} AND m.type=1 UNION " +
            "SELECT m.role_id FROM role_member m JOIN group_member g ON g.group_id=m.member_id WHERE g.user_id=#{userid} AND m.type=2 UNION " +
            "SELECT m.role_id FROM role_member m JOIN post_member p ON p.post_id=m.member_id JOIN organization o ON o.id=p.post_id " +
            "WHERE p.user_id=#{userid} AND o.parent_id=#{deptid} AND m.type=3) r ON r.role_id=a.role_id " +
            "GROUP BY a.function_id) a ON a.function_id=f.id WHERE f.module_id=#{moduleid} AND f.is_invisible=0;")
    List<Function> getModuleFunctions(@Param("moduleid") String moduleId, @Param("userid") String userId, @Param("deptid") String deptId);

}
