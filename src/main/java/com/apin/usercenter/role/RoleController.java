package com.apin.usercenter.role;

import com.apin.usercenter.common.entity.Member;
import com.apin.usercenter.common.entity.Role;
import com.apin.util.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 角色管理服务控制器
 */
@RestController
@RequestMapping("/roleapi")
public class RoleController {
    @Autowired
    RoleService service;

    /**
     * 获取指定应用的全部角色
     *
     * @param token 访问令牌
     * @return Reply
     */
    @GetMapping("/v1.1/roles")
    public Reply getRoles(@RequestHeader("Authorization") String token) throws Exception {
        return service.getRoles(token);
    }

    /**
     * 获取指定的角色
     *
     * @param token 访问令牌
     * @param roleId 角色ID
     * @param secret 验证用的安全码(初始化角色时使用)
     * @return Reply
     */
    @GetMapping("/v1.1/roles/{id}")
    public Reply getRole(@RequestHeader("Authorization") String token, @PathVariable("id") String roleId,
                         @RequestParam(required = false) String secret) throws Exception {
        return service.getRole(token, roleId, secret);
    }

    /**
     * 新增角色
     *
     * @param token  访问令牌
     * @param role   角色实体数据
     * @param secret 验证用的安全码(初始化角色时使用)
     * @return Reply
     */
    @PostMapping("/v1.1/roles")
    public Reply addRole(@RequestHeader("Authorization") String token, @RequestBody Role role,
                         @RequestParam(required = false) String secret) throws Exception {
        return service.addRole(token, role, secret);
    }

    /**
     * 删除角色
     *
     * @param token  访问令牌
     * @param roleId 角色ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/roles/{id}")
    public Reply deleteRole(@RequestHeader("Authorization") String token, @PathVariable("id") String roleId) throws Exception {
        return service.deleteRole(token, roleId);
    }

    /**
     * 更新角色数据
     *
     * @param token 访问令牌
     * @param role  角色实体数据
     * @return Reply
     */
    @PutMapping("/v1.1/roles/{id}")
    public Reply updateRole(@RequestHeader("Authorization") String token, @RequestBody Role role) throws Exception {
        return service.updateRole(token, role);
    }

    /**
     * 添加角色成员
     *
     * @param token   访问令牌
     * @param members 成员集合
     * @return Reply
     */
    @PostMapping("/v1.1/roles/{id}/members")
    public Reply addRoleMember(@RequestHeader("Authorization") String token, @RequestBody List<Member> members) throws Exception {
        return service.addRoleMember(token, members);
    }

    /**
     * 移除角色成员
     *
     * @param token 访问令牌
     * @param list  成员关系ID集合
     * @return Reply
     */
    @DeleteMapping("/v1.1/roles/{id}/members")
    public Reply removeRoleMember(@RequestHeader("Authorization") String token, @RequestBody List<String> list) throws Exception {
        return service.removeRoleMember(token, list);
    }

    /**
     * 获取指定名称的角色的成员用户
     *
     * @param token    访问令牌
     * @param roleName 角色名称
     * @return Reply
     */
    @GetMapping("/v1.1/roles/{name}/users")
    public Reply getRoleUsersByName(@RequestHeader("Authorization") String token, @PathVariable("name") String roleName) throws Exception {
        return service.getRoleUsersByName(token, roleName);
    }
}
