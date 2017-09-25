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
     * @return Reply
     */
    Reply getRole(String token, String roleId);

    /**
     * 新增角色
     *
     * @param token 访问令牌
     * @param role  角色实体数据
     * @return Reply
     */
    Reply addRole(String token, Role role);

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
}
