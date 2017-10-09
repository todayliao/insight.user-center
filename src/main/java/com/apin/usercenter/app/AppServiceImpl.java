package com.apin.usercenter.app;

import com.apin.usercenter.common.Verify;
import com.apin.usercenter.common.entity.App;
import com.apin.usercenter.common.entity.Function;
import com.apin.usercenter.common.mapper.AppMapper;
import com.apin.usercenter.component.Core;
import com.apin.util.ReplyHelper;
import com.apin.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 应用管理服务实现
 */
@Service
public class AppServiceImpl implements AppService {
    private final Core core;
    private final StringRedisTemplate redis;
    private final AppMapper appMapper;
    private final Logger logger;

    /**
     * 构造函数
     *
     * @param core      自动注入的Core
     * @param redis     自动注入的StringRedisTemplate
     * @param appMapper 自动注入的RoleMapper
     */
    @Autowired
    public AppServiceImpl(Core core, StringRedisTemplate redis, AppMapper appMapper) {
        this.core = core;
        this.redis = redis;
        this.appMapper = appMapper;

        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 获取全部应用
     *
     * @param token 访问令牌
     * @return Reply
     */
    @Override
    public Reply getApps(String token) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("ListApps");
        if (!reply.getSuccess()) return reply;

        // 读取数据
        List<App> apps = appMapper.getApps();

        long time = new Date().getTime() - now.getTime();
        logger.info("getApps耗时:" + time + "毫秒...");

        return ReplyHelper.success(apps);
    }

    /**
     * 获取指定应用的全部模块组、模块及功能
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    @Override
    public Reply getModules(String token, String appId) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("ListApps");
        if (!reply.getSuccess()) return reply;

        // 读取数据
        List<Function> list = appMapper.getModules(appId);

        long time = new Date().getTime() - now.getTime();
        logger.info("getModules耗时:" + time + "毫秒...");

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
    public Reply addApp(String token, App app) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("AddApps");
        if (!reply.getSuccess()) return reply;

        // 持久化数据
        app.setCreatorUserId(verify.getUserId());
        app.setCreatedTime(now);
        Integer count = appMapper.addApp(app);

        long time = new Date().getTime() - now.getTime();
        logger.info("addApp耗时:" + time + "毫秒...");

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
    public Reply addModuleGroup(String token, Function group) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("AddModuleGroup");
        if (!reply.getSuccess()) return reply;

        // 持久化数据
        group.setCreatorUserId(verify.getUserId());
        group.setCreatedTime(now);
        Integer count = appMapper.addModuleGroup(group);

        long time = new Date().getTime() - now.getTime();
        logger.info("addModuleGroup耗时:" + time + "毫秒...");

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
    public Reply addModule(String token, Function module) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("AddModule");
        if (!reply.getSuccess()) return reply;

        // 持久化数据
        module.setCreatorUserId(verify.getUserId());
        module.setCreatedTime(now);
        Integer count = appMapper.addModule(module);

        long time = new Date().getTime() - now.getTime();
        logger.info("addModule耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能将数据写入数据库!");
    }

    /**
     * 新增模块功能
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    @Override
    public Reply addFunction(String token, Function function) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("AddFunction");
        if (!reply.getSuccess()) return reply;

        // 持久化数据
        function.setCreatorUserId(verify.getUserId());
        function.setCreatedTime(now);
        Integer count = appMapper.addFunction(function);

        long time = new Date().getTime() - now.getTime();
        logger.info("addFunction耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能将数据写入数据库!");
    }

    /**
     * 删除应用
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    @Override
    public Reply deleteApp(String token, String appId) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("DeleteApps");
        if (!reply.getSuccess()) return reply;

        // 删除数据
        Integer count = appMapper.deleteAppById(appId);

        long time = new Date().getTime() - now.getTime();
        logger.info("deleteApp耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的应用!");
    }

    /**
     * 删除模块组
     *
     * @param token   访问令牌
     * @param groupId 模块组ID
     * @return Reply
     */
    @Override
    public Reply deleteModuleGroup(String token, String groupId) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("DeleteModuleGroup");
        if (!reply.getSuccess()) return reply;

        // 删除数据
        Integer count = appMapper.deleteGroupById(groupId);

        long time = new Date().getTime() - now.getTime();
        logger.info("deleteModuleGroup耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的模块组!");
    }

    /**
     * 删除模块
     *
     * @param token    访问令牌
     * @param moduleId 模块ID
     * @return Reply
     */
    @Override
    public Reply deleteModule(String token, String moduleId) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("DeleteModule");
        if (!reply.getSuccess()) return reply;

        // 删除数据
        Integer count = appMapper.deleteModuleById(moduleId);

        long time = new Date().getTime() - now.getTime();
        logger.info("deleteModule耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的模块!");
    }

    /**
     * 删除模块功能
     *
     * @param token      访问令牌
     * @param functionId 模块功能ID
     * @return Reply
     */
    @Override
    public Reply deleteFunction(String token, String functionId) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("DeleteFunction");
        if (!reply.getSuccess()) return reply;

        // 删除数据
        Integer count = appMapper.deleteFunctionById(functionId);

        long time = new Date().getTime() - now.getTime();
        logger.info("deleteFunction耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的功能!");
    }

    /**
     * 更新应用
     *
     * @param token 访问令牌
     * @param app   应用数据
     * @return Reply
     */
    @Override
    public Reply updateApp(String token, App app) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("EditApps");
        if (!reply.getSuccess()) return reply;

        // 持久化数据
        Integer count = appMapper.updateApp(app);

        long time = new Date().getTime() - now.getTime();
        logger.info("updateApp耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的应用!");
    }

    /**
     * 更新模块组
     *
     * @param token 访问令牌
     * @param group 模块组数据
     * @return Reply
     */
    @Override
    public Reply updateModuleGroup(String token, Function group) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("EditModuleGroup");
        if (!reply.getSuccess()) return reply;

        // 持久化数据
        Integer count = appMapper.updateModuleGroup(group);

        long time = new Date().getTime() - now.getTime();
        logger.info("updateModuleGroup耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的模块组!");
    }

    /**
     * 更新模块
     *
     * @param token  访问令牌
     * @param module 模块数据
     * @return Reply
     */
    @Override
    public Reply updateModule(String token, Function module) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("EditModule");
        if (!reply.getSuccess()) return reply;

        // 持久化数据
        Integer count = appMapper.updateModule(module);

        long time = new Date().getTime() - now.getTime();
        logger.info("updateModule耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的模块!");
    }

    /**
     * 更新模块功能
     *
     * @param token    访问令牌
     * @param function 模块功能数据
     * @return Reply
     */
    @Override
    public Reply updateFunction(String token, Function function) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("EditFunction");
        if (!reply.getSuccess()) return reply;

        // 持久化数据
        Integer count = appMapper.updateFunction(function);

        long time = new Date().getTime() - now.getTime();
        logger.info("updateFunction耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的模块功能!");
    }
}
