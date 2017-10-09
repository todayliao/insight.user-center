package com.apin.usercenter.app;

import com.apin.usercenter.common.entity.App;
import com.apin.usercenter.common.entity.Function;
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
     * @param token 访问令牌
     * @return Reply
     */
    @GetMapping("/v1.1/apps")
    public Reply getApps(@RequestHeader("Authorization") String token) throws Exception {
        return service.getApps(token);
    }

    /**
     * 获取指定应用的全部模块组、模块及功能
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    @GetMapping("/v1.1/apps/{id}/modules")
    public Reply getModules(@RequestHeader("Authorization") String token, @PathVariable("id") String appId) throws Exception {
        return service.getModules(token, appId);
    }

    /**
     * 新增应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps")
    public Reply addApp(@RequestHeader("Authorization") String token, @RequestBody App app) throws Exception {
        return service.addApp(token, app);
    }

    /**
     * 新增模块组
     *
     * @param token 访问令牌
     * @param group 模块组数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps/groups")
    public Reply addModuleGroup(@RequestHeader("Authorization") String token, @RequestBody Function group) throws Exception {
        return service.addModuleGroup(token, group);
    }

    /**
     * 新增模块
     *
     * @param token  访问令牌
     * @param module 模块数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps/modules")
    public Reply addModule(@RequestHeader("Authorization") String token, @RequestBody Function module) throws Exception {
        return service.addModule(token, module);
    }

    /**
     * 新增模块功能数据
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    @PostMapping("/v1.1/apps/functions")
    public Reply addFunction(@RequestHeader("Authorization") String token, @RequestBody Function function) throws Exception {
        return service.addFunction(token, function);
    }

    /**
     * 删除应用
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/{id}")
    public Reply deleteApp(@RequestHeader("Authorization") String token, @PathVariable("id") String appId) throws Exception {
        return service.deleteApp(token, appId);
    }

    /**
     * 删除模块组
     *
     * @param token   访问令牌
     * @param groupId 模块组ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/groups/{id}")
    public Reply deleteModuleGroup(@RequestHeader("Authorization") String token, @PathVariable("id") String groupId) throws Exception {
        return service.deleteModuleGroup(token, groupId);
    }

    /**
     * 删除模块
     *
     * @param token    访问令牌
     * @param moduleId 模块ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/modules/{id}")
    public Reply deleteModule(@RequestHeader("Authorization") String token, @PathVariable("id") String moduleId) throws Exception {
        return service.deleteModule(token, moduleId);
    }

    /**
     * 删除模块功能
     *
     * @param token      访问令牌
     * @param functionId 模块功能ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/apps/functions/{id}")
    public Reply deleteFunction(@RequestHeader("Authorization") String token, @PathVariable("id") String functionId) throws Exception {
        return service.deleteFunction(token, functionId);
    }

    /**
     * 更新应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/{id}")
    public Reply updateApp(@RequestHeader("Authorization") String token, @RequestBody App app) throws Exception {
        return service.updateApp(token, app);
    }

    /**
     * 更新模块组
     *
     * @param token 访问令牌
     * @param group 模块组数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/groups/{id}")
    public Reply updateModuleGroup(@RequestHeader("Authorization") String token, @RequestBody Function group) throws Exception {
        return service.updateModuleGroup(token, group);
    }

    /**
     * 更新模块
     *
     * @param token  访问令牌
     * @param module 模块数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/modules/{id}")
    public Reply updateModule(@RequestHeader("Authorization") String token, @RequestBody Function module) throws Exception {
        return service.updateModule(token, module);
    }

    /**
     * 更新模块功能数据
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    @PutMapping("/v1.1/apps/functions/{id}")
    public Reply updateFunction(@RequestHeader("Authorization") String token, @RequestBody Function function) throws Exception {
        return service.updateFunction(token, function);
    }
}
