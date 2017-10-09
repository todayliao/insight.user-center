package com.apin.usercenter.role;

import com.apin.usercenter.common.Verify;
import com.apin.usercenter.common.entity.Member;
import com.apin.usercenter.common.entity.Role;
import com.apin.usercenter.common.mapper.RoleMapper;
import com.apin.usercenter.component.Core;
import com.apin.util.ReplyHelper;
import com.apin.util.pojo.Reply;
import com.apin.util.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 角色管理服务实现
 */
@Service
public class RoleServiceImpl implements RoleService {
    private final Core core;
    private final StringRedisTemplate redis;
    private final RoleMapper roleMapper;
    private final Logger logger;

    /**
     * 构造函数
     *
     * @param core       自动注入的Core
     * @param redis      自动注入的StringRedisTemplate
     * @param roleMapper 自动注入的RoleMapper
     */
    @Autowired
    public RoleServiceImpl(Core core, StringRedisTemplate redis, RoleMapper roleMapper) {
        this.core = core;
        this.redis = redis;
        this.roleMapper = roleMapper;

        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 获取指定应用的全部角色
     *
     * @param token 访问令牌
     * @return Reply
     */
    @Override
    public Reply getRoles(String token) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("ListRoles");
        if (!reply.getSuccess()) return reply;

        List<Role> roles = roleMapper.getRoles(verify.getAppId());

        long time = new Date().getTime() - now.getTime();
        logger.info("getRoles耗时:" + time + "毫秒...");

        return ReplyHelper.success(roles);
    }

    /**
     * 获取指定的角色
     *
     * @param token  访问令牌
     * @param roleId 角色ID
     * @param secret 验证用的安全码(初始化角色时使用)
     * @return Reply
     */
    @Override
    public Reply getRole(String token, String roleId, String secret) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("ListRoles");
        if (!reply.getSuccess()) return reply;

        if (secret != null) {
            String userId = redis.opsForValue().get(secret);
            if (userId == null || !userId.equals(verify.getUserId())) {
                return ReplyHelper.invalidParam();
            }
        }

        // 查询角色数据
        Role role = roleMapper.getRoleById(roleId);
        role.setFunctions(roleMapper.getRoleFunction(verify.getAppId(), roleId));
        role.setMembers(roleMapper.getRoleMember(roleId));

        long time = new Date().getTime() - now.getTime();
        logger.info("getRoles耗时:" + time + "毫秒...");

        return ReplyHelper.success(role);
    }

    /**
     * 新增角色
     *
     * @param token  访问令牌
     * @param role   角色实体数据
     * @param secret 验证用的安全码(初始化角色时使用)
     * @return Reply
     */
    @Override
    @Transactional
    public Reply addRole(String token, Role role, String secret) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("AddRole");
        if (!reply.getSuccess() && secret == null) return reply;

        if (secret != null) {
            String userId = redis.opsForValue().get(secret);
            if (userId == null || !userId.equals(verify.getUserId())) {
                return ReplyHelper.invalidParam();
            }
        }

        // 初始化角色数据
        role.setApplicationId(verify.getAppId());
        role.setAccountId(verify.getAccountId());
        role.setBuiltin(secret != null);
        role.setCreatorUserId(verify.getUserId());
        role.setCreatedTime(new Date());

        // 持久化角色对象
        Integer count = roleMapper.addRole(role);
        if (role.getFunctions() == null) return ReplyHelper.invalidParam("缺少角色授权功能集合");

        count += roleMapper.addRoleFunction(role);
        if (role.getMembers() != null) count += roleMapper.addRoleMember(role.getMembers());

        long time = new Date().getTime() - now.getTime();
        logger.info("addRole耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 删除角色
     *
     * @param token  访问令牌
     * @param roleId 角色ID
     * @return Reply
     */
    @Override
    public Reply deleteRole(String token, String roleId) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("DeleteRole");
        if (!reply.getSuccess()) return reply;

        Integer count = roleMapper.deleteRole(roleId);

        long time = new Date().getTime() - now.getTime();
        logger.info("deleteRole耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新角色数据
     *
     * @param token 访问令牌
     * @param role  角色实体数据
     * @return Reply
     */
    @Override
    @Transactional
    public Reply updateRole(String token, Role role) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("EditRole");
        if (!reply.getSuccess()) return reply;

        Integer count = roleMapper.updateRole(role);
        count += roleMapper.removeRoleFunction(role.getId());
        count += roleMapper.addRoleFunction(role);

        long time = new Date().getTime() - now.getTime();
        logger.info("updateRole耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 添加角色成员
     *
     * @param token   访问令牌
     * @param members 成员集合
     * @return Reply
     */
    @Override
    public Reply addRoleMember(String token, List<Member> members) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("AddRoleMember");
        if (!reply.getSuccess()) return reply;

        Integer count = roleMapper.addRoleMember(members);

        long time = new Date().getTime() - now.getTime();
        logger.info("addRoleMember耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 移除角色成员
     *
     * @param token 访问令牌
     * @param list  成员关系ID集合
     * @return Reply
     */
    @Override
    public Reply removeRoleMember(String token, List<String> list) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("RemoveRoleMember");
        if (!reply.getSuccess()) return reply;

        Integer count = roleMapper.removeRoleMember(list);

        long time = new Date().getTime() - now.getTime();
        logger.info("removeRoleMember耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 获取指定名称的角色的成员用户
     *
     * @param token    访问令牌
     * @param roleName 角色名称
     * @return Reply
     */
    @Override
    public Reply getRoleUsersByName(String token, String roleName) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        List<User> users = roleMapper.getRoleUsersByName(verify.getAppId(), verify.getAccountId(), roleName);

        long time = new Date().getTime() - now.getTime();
        logger.info("getRoleUsersByName耗时:" + time + "毫秒...");

        return ReplyHelper.success(users);
    }
}
