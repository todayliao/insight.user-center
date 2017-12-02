package com.insight.usercenter.role;

import com.insight.usercenter.common.CallManage;
import com.insight.usercenter.common.dto.AccessToken;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.dto.User;
import com.insight.usercenter.common.entity.Member;
import com.insight.usercenter.common.entity.Role;
import com.insight.usercenter.common.mapper.RoleMapper;
import com.insight.usercenter.common.utils.Generator;
import com.insight.usercenter.common.utils.ReplyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 角色管理服务实现
 */
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final CallManage callManage;

    /**
     * 构造函数
     *
     * @param roleMapper 自动注入的RoleMapper
     * @param callManage 自动注入的CallManage
     */
    @Autowired
    public RoleServiceImpl(RoleMapper roleMapper, CallManage callManage) {
        this.roleMapper = roleMapper;
        this.callManage = callManage;
    }

    /**
     * 获取指定应用的全部角色
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    @Override
    public Reply getRoles(AccessToken token, String appId) {
        List<Role> roles = roleMapper.getRoles(appId == null ? token.getAppId() : appId);

        return ReplyHelper.success(roles);
    }

    /**
     * 获取指定的角色
     *
     * @param roleId 角色ID
     * @return Reply
     */
    @Override
    public Reply getRole(String roleId) {

        // 查询角色数据
        Role role = roleMapper.getRoleById(roleId);
        role.setFunctions(roleMapper.getRoleFunction(role.getApplicationId(), roleId));
        role.setMembers(roleMapper.getRoleMember(roleId));

        return ReplyHelper.success(role);
    }

    /**
     * 新增角色
     *
     * @param token 访问令牌
     * @param role  角色实体数据
     * @return Reply
     */
    @Override
    @Transactional
    public Reply addRole(AccessToken token, Role role) {
        if (role.getFunctions() == null || role.getFunctions().isEmpty()) {
            return ReplyHelper.invalidParam("缺少角色授权功能集合");
        }

        if (roleMapper.getRoleCount(role.getAccountId(), role.getName()) > 0) {
            return ReplyHelper.invalidParam("角色已存在");
        }

        // 限流,每客户端每30秒可访问一次
        String key = Generator.md5("addRole" + token.getId());
        Integer surplus = callManage.getSurplus(key, 30);
        if (surplus > 0) {
            return ReplyHelper.tooOften(surplus);
        }

        // 初始化角色数据
        if (role.getApplicationId() == null || role.getApplicationId().isEmpty()) {
            role.setApplicationId(token.getAppId());
        }

        if (role.getAccountId() == null || role.getAccountId().isEmpty()) {
            role.setAccountId(token.getAccountId());
        }

        role.setBuiltin(false);
        role.setCreatorUserId(token.getUserId());

        // 持久化角色对象
        Integer count = roleMapper.addRole(role);
        count += roleMapper.addRoleFunction(role);
        if (role.getMembers() != null) {
            count += roleMapper.addRoleMember(role.getMembers());
        }

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return Reply
     */
    @Override
    public Reply deleteRole(String roleId) {
        Integer count = roleMapper.deleteRole(roleId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新角色数据
     *
     * @param role 角色实体数据
     * @return Reply
     */
    @Override
    @Transactional
    public Reply updateRole(Role role) {
        if (role.getFunctions() == null || role.getFunctions().isEmpty()) {
            return ReplyHelper.invalidParam("缺少角色授权功能集合");
        }

        Integer count = roleMapper.updateRole(role);
        count += roleMapper.removeRoleFunction(role.getId());
        count += roleMapper.addRoleFunction(role);
        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 添加角色成员
     *
     * @param members 成员集合
     * @return Reply
     */
    @Override
    public Reply addRoleMember(List<Member> members) {
        Integer count = members == null || members.size() == 0 ? 1 : roleMapper.addRoleMember(members);
        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 移除角色成员
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return Reply
     */
    @Override
    public Reply removeRoleMember(String roleId, String userId) {
        Integer count = roleMapper.removeRoleMember(roleId, userId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 批量移除角色成员
     *
     * @param userId 用户ID
     * @return Reply
     */
    @Override
    public Reply removeRoleMemberByUserId(String userId) {
        Integer count = roleMapper.removeRoleMemberByUserId(userId);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }


    /**
     * 批量移除角色成员
     *
     * @param list 成员关系ID集合
     * @return Reply
     */
    @Override
    public Reply removeRoleMembers(List<String> list) {
        Integer count = roleMapper.removeRoleMembers(list);

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
    public Reply getRoleUsersByName(AccessToken token, String roleName) {
        List<User> users = roleMapper.getRoleUsersByName(token.getAppId(), token.getAccountId(), roleName);

        return ReplyHelper.success(users);
    }
}
