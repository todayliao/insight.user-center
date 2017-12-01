package com.apin.usercenter.app;

import com.apin.usercenter.common.entity.App;
import com.apin.usercenter.common.entity.Function;
import com.apin.util.JsonUtils;
import com.apin.util.pojo.AccessToken;
import com.apin.util.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 应用管理服务控制器
 */
@RestController
@RequestMapping("/appapi")
public class AppController {
    @Autowired
    AppService service;

    /**
     * 获取全部应用
     *
     * @return Reply
     */
    @GetMapping("/v1.1/apps")
    public Reply getApps() {
        return service.getApps();
    }

    /**
     * 获取指定应用的全部模块组、模块及功能
     *
     * @param appId 应用ID
     * @return Reply
     */
    @GetMapping("/v1.1/apps/{id}/modules")
    public Reply getModules(@PathVariable("id") String appId) {
        return service.getModules(appId);
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
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return service.addApp(accessToken, app);
    }

    /**
     * 新增模块组
     *
     * @param token 访问令牌
     * @param group 模块组数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps/groups")
    public Reply addModuleGroup(@RequestHeader("Authorization") String token, @RequestBody Function group) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return service.addModuleGroup(accessToken, group);
    }

    /**
     * 新增模块
     *
     * @param token  访问令牌
     * @param module 模块数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps/modules")
    public Reply addModule(@RequestHeader("Authorization") String token, @RequestBody Function module) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return service.addModule(accessToken, module);
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
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return service.addFunction(accessToken, function);
    }

    /**
     * 删除应用
     *
     * @param appId 应用ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/{id}")
    public Reply deleteApp(@PathVariable("id") String appId) {
        return service.deleteApp(appId);
    }

    /**
     * 删除模块组
     *
     * @param groupId 模块组ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/groups/{id}")
    public Reply deleteModuleGroup(@PathVariable("id") String groupId) {
        return service.deleteModuleGroup(groupId);
    }

    /**
     * 删除模块
     *
     * @param moduleId 模块ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/modules/{id}")
    public Reply deleteModule(@PathVariable("id") String moduleId) {
        return service.deleteModule(moduleId);
    }

    /**
     * 删除模块功能
     *
     * @param functionId 模块功能ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/functions/{id}")
    public Reply deleteFunction(@PathVariable("id") String functionId) {
        return service.deleteFunction(functionId);
    }

    /**
     * 更新应用
     *
     * @param app 应用数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/{id}")
    public Reply updateApp(@RequestBody App app) {
        return service.updateApp(app);
    }

    /**
     * 更新模块组
     *
     * @param group 模块组数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/groups/{id}")
    public Reply updateModuleGroup(@RequestBody Function group) {
        return service.updateModuleGroup(group);
    }

    /**
     * 更新模块
     *
     * @param module 模块数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/modules/{id}")
    public Reply updateModule(@RequestBody Function module) {
        return service.updateModule(module);
    }

    /**
     * 更新模块功能数据
     *
     * @param function 模块功能数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/functions/{id}")
    public Reply updateFunction(@RequestBody Function function) {
        return service.updateFunction(function);
    }
}
