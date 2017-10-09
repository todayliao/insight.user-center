package com.apin.usercenter.role;

import com.apin.usercenter.common.entity.Member;
import com.apin.usercenter.common.entity.Role;
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
     * @return Reply
     */
    Reply getRoles(String token);

    /**
     * 获取指定的角色
     *
     * @param token  访问令牌
     * @param roleId 角色ID
     * @param secret 验证用的安全码(初始化角色时使用)
     * @return Reply
     */
    Reply getRole(String token, String roleId, String secret);

    /**
     * 新增角色
     *
     * @param token  访问令牌
     * @param role   角色实体数据
     * @param secret 验证用的安全码(初始化角色时使用)
     * @return Reply
     */
    Reply addRole(String token, Role role, String secret);

    /**
     * 删除角色
     *
     * @param token  访问令牌
     * @param roleId 角色ID
     * @return Reply
     */
    Reply deleteRole(String token, String roleId);

    /**
     * 更新角色数据
     *
     * @param token 访问令牌
     * @param role  角色实体数据
     * @return Reply
     */
    Reply updateRole(String token, Role role);

    /**
     * 添加角色成员
     *
     * @param token   访问令牌
     * @param members 成员集合
     * @return Reply
     */
    Reply addRoleMember(String token, List<Member> members);

    /**
     * 移除角色成员
     *
     * @param token 访问令牌
     * @param list  成员关系ID集合
     * @return Reply
     */
    Reply removeRoleMember(String token, List<String> list);

    /**
     * 获取指定名称的角色的成员用户
     *
     * @param token    访问令牌
     * @param roleName 角色名称
     * @return Reply
     */
    Reply getRoleUsersByName(String token, String roleName);
}
