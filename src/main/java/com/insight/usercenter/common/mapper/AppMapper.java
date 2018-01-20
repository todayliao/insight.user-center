package com.insight.usercenter.common.mapper;

import com.insight.usercenter.common.entity.App;
import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.Navigator;
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
    @Select("SELECT * FROM ucs_application ORDER BY created_time DESC;")
    List<App> getApps();

    /**
     * 查询指定ID的应用信息
     *
     * @param appId 应用ID
     * @return 应用信息
     */
    @Select("SELECT * FROM ucs_application WHERE id=#{appId};")
    App getApp(String appId);

    /**
     * 获取指定应用的全部导航及功能
     *
     * @param appId 应用ID
     * @return 功能集合
     */
    @Select("SELECT * FROM (SELECT id,parent_id,type,`index`,`name`,NULL AS alias,icon,url, NULL as interfaces,remark,NULL AS is_invisible " +
            "FROM ucs_navigator WHERE application_id=#{appId} UNION " +
            "SELECT id,navigator_id AS parent_id,3 AS type,`index`,`name`,alias,icon,url,interfaces,remark,is_invisible " +
            "FROM ucs_function WHERE application_id=#{appId}) l ORDER BY type,parent_id,`index`;")
    List<Function> getNavigators(String appId);

    /**
     * 新增应用
     *
     * @param app 应用实体数据
     * @return 受影响行数
     */
    @Insert("INSERT ucs_application (id,`name`,alias,icon,host,creator_user_id) " +
            "VALUES (#{id},#{name},#{alias},#{icon},#{host},#{creatorUserId});")
    Integer addApp(App app);

    /**
     * 新增导航
     *
     * @param navigator 导航数据
     * @return 受影响行数
     */
    @Insert("INSERT ucs_navigator (id,application_id,parent_id,type,`index`,`name`,icon,url,remark,creator_user_id) " +
            "VALUES (#{id},#{applicationId},#{parentId},#{type},#{index},#{name},#{icon},#{url},#{remark},#{creatorUserId});")
    Integer addNavigator(Navigator navigator);

    /**
     * 查询模块功能
     *
     * @param functionId 模块功能id
     * @return 受影响行数
     */
    @Select("SELECT * from ucs_function WHERE id=#{functionId}")
    Function getFunction(String functionId);

    /**
     * 新增模块功能
     *
     * @param function 功能实体数据
     * @return 受影响行数
     */
    @Insert("INSERT ucs_function (id,application_id,navigator_id,`index`,`name`,alias,icon,url,interfaces,remark,begin_group,hide_text,is_invisible,creator_user_id) " +
            "VALUES (#{id},#{applicationId},#{parentId},#{index},#{name},#{alias},#{icon},#{url},#{interfaces},#{remark},#{beginGroup},#{hideText},#{invisible},#{creatorUserId});")
    Integer addFunction(Function function);

    /**
     * 删除指定ID的应用
     *
     * @param appId 应用ID
     * @return 受影响行数
     */
    @Delete("DELETE a,n,f FROM ucs_application a LEFT JOIN ucs_navigator n ON n.application_id=a.id " +
            "LEFT JOIN ucs_function f ON f.navigator_id=n.id WHERE a.id=#{appId};")
    Integer deleteAppById(String appId);

    /**
     * 删除指定ID的导航
     *
     * @param navigatorId 导航ID
     * @return 受影响行数
     */
    @Delete("DELETE n,f FROM ucs_navigator n LEFT JOIN ucs_function f ON f.navigator_id=n.id WHERE n.id=#{navigatorId};")
    Integer deleteNavigatorById(String navigatorId);

    /**
     * 删除指定ID的模块功能
     *
     * @param functionId 模块功能ID
     * @return 受影响行数
     */
    @Delete("DELETE FROM ucs_function WHERE id=#{functionId};")
    Integer deleteFunctionById(String functionId);

    /**
     * 更新应用
     *
     * @param app 应用数据
     * @return 受影响行数
     */
    @Update("UPDATE ucs_application SET `name`=#{name},alias=#{alias},icon=#{icon},host=#{host} WHERE id=#{id};")
    Integer updateApp(App app);

    /**
     * 更新导航
     *
     * @param navigator 导航数据
     * @return 受影响行数
     */
    @Update("UPDATE ucs_navigator SET application_id=#{applicationId},parent_id=#{parentId},type=#{type}," +
            "`index`=#{index},`name`=#{name},icon=#{icon},url=#{url},remark=#{remark} WHERE id=#{id};")
    Integer updateNavigator(Navigator navigator);

    /**
     * 更新模块功能
     *
     * @param function 模块功能数据
     * @return 受影响行数
     */
    @Update("UPDATE ucs_function SET application_id=#{applicationId},navigator_id=#{parentId},`index`=#{index}," +
            "`name`=#{name},alias=#{alias},icon=#{icon},url=#{url},interfaces=#{interfaces},remark=#{remark}," +
            "begin_group=#{beginGroup},hide_text=#{hideText},is_invisible=#{invisible} WHERE id=#{id};")
    Integer updateFunction(Function function);
}
