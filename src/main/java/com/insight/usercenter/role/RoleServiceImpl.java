package com.insight.usercenter.role;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.common.entity.Member;
import com.insight.usercenter.common.entity.Role;
import com.insight.usercenter.common.mapper.RoleMapper;
import com.insight.usercenter.common.mapper.TenantMapper;
import com.insight.usercenter.role.dto.RoleDTO;
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
 * @remark 角色管理服务实现
 */
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final TenantMapper tenantMapper;

    /**
     * 构造函数
     *
     * @param roleMapper   自动注入的RoleMapper
     * @param tenantMapper 自动注入的TenantMapper
     */
    @Autowired
    public RoleServiceImpl(RoleMapper roleMapper, TenantMapper tenantMapper) {
        this.roleMapper = roleMapper;
        this.tenantMapper = tenantMapper;
    }

    /**
     * 获取指定应用的全部角色
     *
     * @param token Token
     * @param role  角色查询实体对象
     * @return Reply
     */
    @Override
    public Reply getRoles(Token token, RoleDTO role) {
        role.setTenantId(token.getTenantId());

        PageHelper.startPage(role.getPage(), role.getPageSize());
        List<Role> roles = roleMapper.getRoles(role);
        PageInfo<Role> pageInfo = new PageInfo<>(roles);

        return ReplyHelper.success(roles, pageInfo.getTotal());
    }

    /**
     * 获取指定的角色
     *
     * @param token  Token
     * @param roleId 角色ID
     * @return Reply
     */
    @Override
    public Reply getRole(Token token, String roleId) {
        Role role = roleMapper.getRole(roleId);
        if (role == null) {
            return ReplyHelper.invalidParam("角色不存在");
        }

        if (!role.getTenantId().equals(token.getTenantId())) {
            return ReplyHelper.invalidParam();
        }

        // 查询角色数据
        role.setFunctions(roleMapper.getRoleFunction(role.getApplicationId(), roleId));
        role.setMembers(roleMapper.getRoleMember(roleId));

        return ReplyHelper.success(role);
    }

    /**
     * 新增角色
     *
     * @param token Token
     * @param role  角色实体数据
     * @return Reply
     */
    @Override
    @Transactional
    public Reply addRole(Token token, Role role) {
        if (roleMapper.getRoleCount(role.getTenantId(), role.getName()) > 0) {
            return ReplyHelper.invalidParam("角色已存在");
        }

        // 初始化角色数据
        role.setId(Generator.uuid());
        role.setTenantId(token.getTenantId());
        role.setBuiltin(false);
        role.setCreatorUserId(token.getUserId());

        // 持久化角色对象
        Integer count = roleMapper.addRole(role);

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
        Integer count = roleMapper.updateRole(role);
        if (role.getFunctions() != null && !role.getFunctions().isEmpty()) {
            count += roleMapper.addRoleFunction(role.getId(), role.getFunctions());
        }

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 添加角色成员
     *
     * @param token   Token
     * @param members 成员集合
     * @return Reply
     */
    @Override
    public Reply addRoleMembers(Token token, List<Member> members) {
        if (members == null || members.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        // 持久化成员关系
        Integer count = roleMapper.addRoleMembers(members);

        // 为未绑定租户的用户自动绑定租户-用户关系
        List<String> list = tenantMapper.getUnbindingUser(token.getTenantId(), members);
        if (list != null && !list.isEmpty()) {
            count += tenantMapper.addUsersToTenant(token.getTenantId(), list);
        }

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
     * @param token    Token
     * @param appId    应用ID
     * @param roleName 角色名称
     * @return Reply
     */
    @Override
    public Reply getRoleUsersByName(Token token, String appId, String roleName) {
        List<UserDTO> users = roleMapper.getRoleUsersByName(appId, token.getTenantId(), roleName);

        return ReplyHelper.success(users);
    }
}
