package com.apin.usercenter.role;

import com.apin.usercenter.common.entity.Member;
import com.apin.usercenter.common.entity.Role;
import com.apin.util.pojo.AccessToken;
import com.apin.util.pojo.Reply;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 角色管理服务接口
 */
public interface RoleService {

    /**
     * 获取指定应用的全部角色
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    Reply getRoles(AccessToken token, String appId);

    /**
     * 获取指定的角色
     *
     * @param roleId 角色ID
     * @return Reply
     */
    Reply getRole(String roleId);

    /**
     * 新增角色
     *
     * @param token  访问令牌
     * @param role   角色实体数据
     * @return Reply
     */
    Reply addRole(AccessToken token, Role role);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return Reply
     */
    Reply deleteRole(String roleId);

    /**
     * 更新角色数据
     *
     * @param role 角色实体数据
     * @return Reply
     */
    Reply updateRole(Role role);

    /**
     * 添加角色成员
     *
     * @param members 成员集合
     * @return Reply
     */
    Reply addRoleMember(List<Member> members);

    /**
     * 移除角色成员
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return Reply
     */
    Reply removeRoleMember(String roleId, String userId);

    /**
     * 批量移除角色成员
     *
     * @param userId 用户Id
     * @return Reply
     */
    Reply removeRoleMemberByUserId(String userId);

    /**
     * 批量移除角色成员
     *
     * @param list 成员关系ID集合
     * @return Reply
     */
    Reply removeRoleMembers(List<String> list);

    /**
     * 获取指定名称的角色的成员用户
     *
     * @param token    访问令牌
     * @param roleName 角色名称
     * @return Reply
     */
    Reply getRoleUsersByName(AccessToken token, String roleName);
}
