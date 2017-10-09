package com.apin.usercenter.app;

import com.apin.usercenter.common.entity.App;
import com.apin.usercenter.common.entity.Function;
import com.apin.util.pojo.Reply;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 应用管理服务接口
 */
public interface AppService {

    /**
     * 获取全部应用
     *
     * @param token 访问令牌
     * @return Reply
     */
    Reply getApps(String token);

    /**
     * 获取指定应用的全部模块组、模块及功能
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    Reply getModules(String token, String appId);

    /**
     * 新增应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    Reply addApp(String token, App app);

    /**
     * 新增模块组
     *
     * @param token 访问令牌
     * @param group 模块组数据
     * @return Reply
     */
    Reply addModuleGroup(String token, Function group);

    /**
     * 新增模块
     *
     * @param token  访问令牌
     * @param module 模块数据
     * @return Reply
     */
    Reply addModule(String token, Function module);

    /**
     * 新增模块功能
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    Reply addFunction(String token, Function function);

    /**
     * 删除应用
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    Reply deleteApp(String token, String appId);

    /**
     * 删除模块组
     *
     * @param token   访问令牌
     * @param groupId 模块组ID
     * @return Reply
     */
    Reply deleteModuleGroup(String token, String groupId);

    /**
     * 删除模块
     *
     * @param token    访问令牌
     * @param moduleId 模块ID
     * @return Reply
     */
    Reply deleteModule(String token, String moduleId);

    /**
     * 删除模块功能
     *
     * @param token      访问令牌
     * @param functionId 模块功能ID
     * @return Reply
     */
    Reply deleteFunction(String token, String functionId);

    /**
     * 更新应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    Reply updateApp(String token, App app);

    /**
     * 更新模块组
     *
     * @param token 访问令牌
     * @param group 模块组数据
     * @return Reply
     */
    Reply updateModuleGroup(String token, Function group);

    /**
     * 更新模块
     *
     * @param token  访问令牌
     * @param module 模块数据
     * @return Reply
     */
    Reply updateModule(String token, Function module);

    /**
     * 更新模块功能
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    Reply updateFunction(String token, Function function);
}
