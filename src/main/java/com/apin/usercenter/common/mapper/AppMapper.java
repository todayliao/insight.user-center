package com.apin.usercenter.common.mapper;

import com.apin.usercenter.common.entity.App;
import com.apin.usercenter.common.entity.Function;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/30
 * @remark 应用管理相关DAL
 */
@Mapper
public interface AppMapper extends Mapper {

    /**
     * 获取全部应用
     *
     * @return 应用集合
     */
    @Select("select * from application order by created_time;")
    List<App> getApps();

    /**
     * 获取指定应用的全部模块组、模块及功能
     *
     * @param appId 应用ID
     * @return 功能集合
     */
    @Results({
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "invisible", column = "is_invisible")
    })
    @Select("SELECT t.* FROM (" +
            "SELECT id,application_id AS parent_id,1 AS type,`index`,`name`,NULL AS alias,icon,NULL AS url,remark,NULL AS is_invisible " +
            "FROM module_group WHERE application_id=#{id} UNION " +
            "SELECT m.id,g.id AS parent_id,2 AS type,m.`index`,m.`name`,NULL AS alias,m.icon,m.url,m.remark,NULL AS is_invisible " +
            "FROM module_group g JOIN module m ON m.group_id=g.id WHERE g.application_id=#{id} UNION " +
            "SELECT f.id,m.id AS parent_id,3 AS type,f.`index`,f.`name`,f.alias,f.icon,f.url,f.remark,f.is_invisible " +
            "FROM module_group g JOIN module m ON m.group_id=g.id JOIN module_function f ON f.module_id=m.id WHERE g.application_id=#{id}" +
            ") t ORDER BY t.type, t.parent_id, t.`index`;")
    List<Function> getModules(@Param("id") String appId);

    /**
     * 新增应用
     *
     * @param app 应用实体数据
     * @return 受影响行数
     */
    @Insert("insert application (id,`name`,alias,secret,icon,host,creator_user_id,created_time) " +
            "VALUES (#{id},#{name},#{alias},#{secret},#{icon},#{host},#{creatorUserId},#{createdTime});")
    Integer addApp(App app);

    /**
     * 新增模块组
     *
     * @param group 功能实体数据
     * @return 受影响行数
     */
    @Insert("insert module_group (id,application_id,`index`,`name`,icon,remark,creator_user_id,created_time) " +
            "VALUES (#{id},#{parentId},#{index},#{name},#{icon},#{remark},#{creatorUserId},#{createdTime});")
    Integer addModuleGroup(Function group);

    /**
     * 新增模块
     *
     * @param module 功能实体数据
     * @return 受影响行数
     */
    @Insert("insert module (id,group_id,`index`,`name`,icon,url,remark,creator_user_id,created_time) " +
            "VALUES (#{id},#{parentId},#{index},#{name},#{icon},#{url},#{remark},#{creatorUserId},#{createdTime});")
    Integer addModule(Function module);

    /**
     * 新增模块功能
     *
     * @param function 功能实体数据
     * @return 受影响行数
     */
    @Insert("insert module_function (id,module_id,`index`,`name`,alias,icon,url,remark,is_invisible,creator_user_id,created_time) " +
            "VALUES (#{id},#{parentId},#{index},#{name},#{alias},#{icon},#{url},#{remark},#{invisible},#{creatorUserId},#{createdTime});")
    Integer addFunction(Function function);

    /**
     * 删除指定ID的应用
     *
     * @param appId 应用ID
     * @return 受影响行数
     */
    @Delete("DELETE a,g,m,f FROM application a LEFT JOIN module_group g ON g.application_id=a.id " +
            "LEFT JOIN module m ON m.group_id=g.id LEFT JOIN module_function f ON f.module_id=m.id WHERE a.id=#{id};")
    Integer deleteAppById(@Param("id") String appId);

    /**
     * 删除指定ID的模块组
     *
     * @param groupId 模块组ID
     * @return 受影响行数
     */
    @Delete("DELETE g,m,f FROM module_group g LEFT JOIN module m ON m.group_id=g.id " +
            "LEFT JOIN module_function f ON f.module_id=m.id WHERE g.id=#{id};")
    Integer deleteGroupById(@Param("id") String groupId);

    /**
     * 删除指定ID的模块
     *
     * @param moduleId 模块ID
     * @return 受影响行数
     */
    @Delete("DELETE m,f FROM module m LEFT JOIN module_function f ON f.module_id=m.id WHERE m.id=#{id};")
    Integer deleteModuleById(@Param("id") String moduleId);

    /**
     * 删除指定ID的模块功能
     *
     * @param functionId 模块功能ID
     * @return 受影响行数
     */
    @Delete("DELETE FROM module_function WHERE id=#{id};")
    Integer deleteFunctionById(@Param("id") String functionId);

    /**
     * 更新应用
     *
     * @param app 应用数据
     * @return 受影响行数
     */
    @Update("update application set `name`=#{name},alias=#{alias},secret=#{secret},icon=#{icon},host=#{host} where id=#{id};")
    Integer updateApp(App app);

    /**
     * 更新模块组
     *
     * @param group 模块组数据
     * @return 受影响行数
     */
    @Update("update module_group set `index`=#{index},`name`=#{name},icon=#{icon},remark=#{remark} where id=#{id};")
    Integer updateModuleGroup(Function group);

    /**
     * 更新模块
     *
     * @param module 模块数据
     * @return 受影响行数
     */
    @Update("update module set `index`=#{index},`name`=#{name},icon=#{icon},url=#{url},remark=#{remark} where id=#{id};")
    Integer updateModule(Function module);

    /**
     * 更新模块功能
     *
     * @param function 模块功能数据
     * @return 受影响行数
     */
    @Update("update module_function set `index`=#{index},`name`=#{name},alias=#{alias},icon=#{icon},url=#{url},remark=#{remark},is_invisible=#{invisible} where id=#{id};")
    Integer updateFunction(Function function);
}
