package com.insight.usercenter.app;


import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.entity.App;
import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.Navigator;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 应用管理服务接口
 */
public interface AppService {

    /**
     * 获取全部应用
     *
     * @return Reply
     */
    Reply getApps();

    /**
     * 获取指定应用的全部导航及功能
     *
     * @param appId 应用ID
     * @return Reply
     */
    Reply getApp(String appId);

    /**
     * 新增应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    Reply addApp(Token token, App app);

    /**
     * 新增导航
     *
     * @param token     访问令牌
     * @param navigator 导航数据
     * @return Reply
     */
    Reply addNavigator(Token token, Navigator navigator);

    /**
     * 新增模块功能
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    Reply addFunction(Token token, Function function);

    /**
     * 删除应用
     *
     * @param appId 应用ID
     * @return Reply
     */
    Reply deleteApp(String appId);

    /**
     * 删除导航
     *
     * @param navigatorId 模块组ID
     * @return Reply
     */
    Reply deleteNavigator(String navigatorId);

    /**
     * 删除模块功能
     *
     * @param functionId 模块功能ID
     * @return Reply
     */
    Reply deleteFunction(String functionId);

    /**
     * 更新应用
     *
     * @param app 应用数据
     * @return Reply
     */
    Reply updateApp(App app);

    /**
     * 更新导航
     *
     * @param navigator 模块组数据
     * @return Reply
     */
    Reply updateNavigator(Navigator navigator);

    /**
     * 更新模块功能
     *
     * @param function 模块功能数据
     * @return Reply
     */
    Reply updateFunction(Function function);
}
