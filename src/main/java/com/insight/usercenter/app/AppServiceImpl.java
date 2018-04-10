package com.insight.usercenter.app;

import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.entity.App;
import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.Navigator;
import com.insight.usercenter.common.mapper.AppMapper;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Reply;
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
    private final AppMapper mapper;

    /**
     * 构造函数
     *
     * @param mapper 自动注入的AppMapper
     */
    @Autowired
    public AppServiceImpl(AppMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 获取全部应用
     *
     * @return Reply
     */
    @Override
    public Reply getApps() {
        List<App> apps = mapper.getApps();

        return ReplyHelper.success(apps);
    }

    /**
     * 获取指定应用的全部导航及功能
     *
     * @param appId 应用ID
     * @return Reply
     */
    @Override
    public Reply getApp(String appId) {
        List<Function> list = mapper.getNavigators(appId);

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
    public Reply addApp(Token token, App app) {
        app.setId(Generator.uuid());
        app.setCreatorUserId(token.getUserId());

        // 持久化数据
        Integer count = mapper.addApp(app);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能将数据写入数据库!");
    }

    /**
     * 新增导航
     *
     * @param token     访问令牌
     * @param navigator 导航数据
     * @return Reply
     */
    @Override
    public Reply addNavigator(Token token, Navigator navigator) {
        navigator.setId(Generator.uuid());
        navigator.setCreatorUserId(token.getUserId());

        // 持久化数据
        Integer count = mapper.addNavigator(navigator);

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
    public Reply addFunction(Token token, Function function) {
        function.setId(Generator.uuid());
        function.setCreatorUserId(token.getUserId());

        // 持久化数据
        Integer count = mapper.addFunction(function);

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
        Integer count = mapper.deleteAppById(appId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的应用!");
    }

    /**
     * 删除导航
     *
     * @param navigatorId 导航ID
     * @return Reply
     */
    @Override
    public Reply deleteNavigator(String navigatorId) {

        // 删除数据
        Integer count = mapper.deleteNavigatorById(navigatorId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的模块组!");
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
        Integer count = mapper.deleteFunctionById(functionId);

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
        Integer count = mapper.updateApp(app);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的应用!");
    }

    /**
     * 更新导航
     *
     * @param navigator 导航数据
     * @return Reply
     */
    @Override
    public Reply updateNavigator(Navigator navigator) {

        // 持久化数据
        Integer count = mapper.updateNavigator(navigator);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的模块组!");
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
        Integer count = mapper.updateFunction(function);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的模块功能!");
    }
}
