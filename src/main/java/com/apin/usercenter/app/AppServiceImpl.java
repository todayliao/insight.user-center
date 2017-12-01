package com.apin.usercenter.app;

import com.apin.usercenter.common.entity.App;
import com.apin.usercenter.common.entity.Function;
import com.apin.usercenter.common.mapper.AppMapper;
import com.apin.util.ReplyHelper;
import com.apin.util.pojo.AccessToken;
import com.apin.util.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 应用管理服务实现
 */
@Service
public class AppServiceImpl implements AppService {
    private final AppMapper appMapper;

    /**
     * 构造函数
     *
     * @param appMapper 自动注入的RoleMapper
     */
    @Autowired
    public AppServiceImpl(AppMapper appMapper) {
        this.appMapper = appMapper;
    }

    /**
     * 获取全部应用
     *
     * @return Reply
     */
    @Override
    public Reply getApps() {

        // 读取数据
        List<App> apps = appMapper.getApps();

        return ReplyHelper.success(apps);
    }

    /**
     * 获取指定应用的全部模块组、模块及功能
     *
     * @param appId 应用ID
     * @return Reply
     */
    @Override
    public Reply getModules(String appId) {

        // 读取数据
        List<Function> list = appMapper.getModules(appId);
        list.forEach(i -> i.setUrls(appMapper.getFunctionUrls(i.getAlias())));

        return ReplyHelper.success(list);
    }

    /**
     * 新增应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    @Override
    public Reply addApp(AccessToken token, App app) {

        // 持久化数据
        app.setCreatorUserId(token.getUserId());
        Integer count = appMapper.addApp(app);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能将数据写入数据库!");
    }

    /**
     * 新增模块组
     *
     * @param token 访问令牌
     * @param group 模块组数据
     * @return Reply
     */
    @Override
    public Reply addModuleGroup(AccessToken token, Function group) {

        // 持久化数据
        group.setCreatorUserId(token.getUserId());
        Integer count = appMapper.addModuleGroup(group);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能将数据写入数据库!");
    }

    /**
     * 新增模块
     *
     * @param token  访问令牌
     * @param module 模块数据
     * @return Reply
     */
    @Override
    public Reply addModule(AccessToken token, Function module) {

        // 持久化数据
        module.setCreatorUserId(token.getUserId());
        Integer count = appMapper.addModule(module);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能将数据写入数据库!");
    }

    /**
     * 新增模块功能
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    @Transactional
    @Override
    public Reply addFunction(AccessToken token, Function function) {

        // 持久化数据
        function.setCreatorUserId(token.getUserId());
        Integer count = appMapper.addFunction(function);
        count += appMapper.addFunctionUrl(function.getAlias(), function.getUrls());

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能将数据写入数据库!");
    }

    /**
     * 删除应用
     *
     * @param appId 应用ID
     * @return Reply
     */
    @Override
    public Reply deleteApp(String appId) {

        // 删除数据
        Integer count = appMapper.deleteAppById(appId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的应用!");
    }

    /**
     * 删除模块组
     *
     * @param groupId 模块组ID
     * @return Reply
     */
    @Override
    public Reply deleteModuleGroup(String groupId) {

        // 删除数据
        Integer count = appMapper.deleteGroupById(groupId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的模块组!");
    }

    /**
     * 删除模块
     *
     * @param moduleId 模块ID
     * @return Reply
     */
    @Override
    public Reply deleteModule(String moduleId) {

        // 删除数据
        Integer count = appMapper.deleteModuleById(moduleId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的模块!");
    }

    /**
     * 删除模块功能
     *
     * @param functionId 模块功能ID
     * @return Reply
     */
    @Override
    public Reply deleteFunction(String functionId) {

        // 删除数据
        Integer count = appMapper.deleteFunctionById(functionId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的功能!");
    }

    /**
     * 更新应用
     *
     * @param app 应用数据
     * @return Reply
     */
    @Override
    public Reply updateApp(App app) {

        // 持久化数据
        Integer count = appMapper.updateApp(app);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的应用!");
    }

    /**
     * 更新模块组
     *
     * @param group 模块组数据
     * @return Reply
     */
    @Override
    public Reply updateModuleGroup(Function group) {

        // 持久化数据
        Integer count = appMapper.updateModuleGroup(group);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的模块组!");
    }

    /**
     * 更新模块
     *
     * @param module 模块数据
     * @return Reply
     */
    @Override
    public Reply updateModule(Function module) {

        // 持久化数据
        Integer count = appMapper.updateModule(module);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的模块!");
    }

    /**
     * 更新模块功能
     *
     * @param function 模块功能数据
     * @return Reply
     */
    @Transactional
    @Override
    public Reply updateFunction(Function function) {

        // 持久化数据
        Integer count = appMapper.updateFunction(function);
        count += appMapper.deleteFunctionUrl(function.getAlias());
        count += appMapper.addFunctionUrl(function.getAlias(), function.getUrls());

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的模块功能!");
    }
}
