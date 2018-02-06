package com.insight.usercenter.app;

import com.insight.usercenter.common.Verify;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.entity.App;
import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 应用管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/appapi")
public class AppController {
    private final AppService service;

    @Autowired
    public AppController(AppService service) {
        this.service = service;
    }

    /**
     * 获取全部应用
     *
     * @param token 访问令牌
     * @return Reply
     */
    @GetMapping("/v1.1/apps")
    public Reply getApps(@RequestHeader("Authorization") String token) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getApps");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getApps();
    }

    /**
     * 获取指定应用的全部导航及功能
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    @GetMapping("/v1.1/apps/{id}")
    public Reply getApp(@RequestHeader("Authorization") String token, @PathVariable("id") String appId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getApps");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getApp(appId);
    }

    /**
     * 新增应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps")
    public Reply addApp(@RequestHeader("Authorization") String token, @RequestBody App app) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("addApp");
        if (!result.getSuccess()) {
            return result;
        }

        return service.addApp(verify.getBasis(), app);
    }

    /**
     * 新增导航
     *
     * @param token     访问令牌
     * @param navigator 导航数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps/navigators")
    public Reply addNavigator(@RequestHeader("Authorization") String token, @RequestBody Navigator navigator) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("addNavigator");
        if (!result.getSuccess()) {
            return result;
        }

        return service.addNavigator(verify.getBasis(), navigator);
    }

    /**
     * 新增模块功能数据
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps/functions")
    public Reply addFunction(@RequestHeader("Authorization") String token, @RequestBody Function function) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("addFunction");
        if (!result.getSuccess()) {
            return result;
        }

        return service.addFunction(verify.getBasis(), function);
    }

    /**
     * 删除应用
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/{id}")
    public Reply deleteApp(@RequestHeader("Authorization") String token, @PathVariable("id") String appId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("deleteApp");
        if (!result.getSuccess()) {
            return result;
        }

        return service.deleteApp(appId);
    }

    /**
     * 删除导航
     *
     * @param token       访问令牌
     * @param navigatorId 导航ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/navigators/{id}")
    public Reply deleteNavigator(@RequestHeader("Authorization") String token, @PathVariable("id") String navigatorId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("deleteNavigator");
        if (!result.getSuccess()) {
            return result;
        }

        return service.deleteNavigator(navigatorId);
    }

    /**
     * 删除模块功能
     *
     * @param token      访问令牌
     * @param functionId 模块功能ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/functions/{id}")
    public Reply deleteFunction(@RequestHeader("Authorization") String token, @PathVariable("id") String functionId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("deleteFunction");
        if (!result.getSuccess()) {
            return result;
        }

        return service.deleteFunction(functionId);
    }

    /**
     * 更新应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/{id}")
    public Reply updateApp(@RequestHeader("Authorization") String token, @RequestBody App app) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("updateApp");
        if (!result.getSuccess()) {
            return result;
        }

        return service.updateApp(app);
    }

    /**
     * 更新导航
     *
     * @param token     访问令牌
     * @param navigator 导航数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/navigators/{id}")
    public Reply updateNavigator(@RequestHeader("Authorization") String token, @RequestBody Navigator navigator) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("updateNavigator");
        if (!result.getSuccess()) {
            return result;
        }

        return service.updateNavigator(navigator);
    }

    /**
     * 更新模块功能数据
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/functions/{id}")
    public Reply updateFunction(@RequestHeader("Authorization") String token, @RequestBody Function function) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("updateFunction");
        if (!result.getSuccess()) {
            return result;
        }

        return service.updateFunction(function);
    }
}
