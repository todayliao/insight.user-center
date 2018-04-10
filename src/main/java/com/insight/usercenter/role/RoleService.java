package com.insight.usercenter.role;

import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.entity.Member;
import com.insight.usercenter.common.entity.Role;
import com.insight.usercenter.role.dto.RoleDTO;
import com.insight.util.pojo.Reply;

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
     * @param token Token
     * @param role  角色查询实体对象
     * @return Reply
     */
    Reply getRoles(Token token, RoleDTO role);

    /**
     * 获取指定的角色
     *
     * @param token  Token
     * @param roleId 角色ID
     * @return Reply
     */
    Reply getRole(Token token, String roleId);

    /**
     * 新增角色
     *
     * @param token Token
     * @param role  角色实体数据
     * @return Reply
     */
    Reply addRole(Token token, Role role);

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
     * @param token   Token
     * @param members 成员集合
     * @return Reply
     */
    Reply addRoleMembers(Token token, List<Member> members);

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
     * @param list 成员关系ID集合
     * @return Reply
     */
    Reply removeRoleMembers(List<String> list);

    /**
     * 获取指定名称的角色的成员用户
     *
     * @param token    Token
     * @param appId    应用ID
     * @param roleName 角色名称
     * @return Reply
     */
    Reply getRoleUsersByName(Token token, String appId, String roleName);
}
