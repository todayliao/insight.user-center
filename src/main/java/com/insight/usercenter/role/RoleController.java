package com.insight.usercenter.role;

import com.insight.usercenter.common.dto.AccessToken;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.entity.Member;
import com.insight.usercenter.common.entity.Role;
import com.insight.usercenter.common.utils.JsonUtils;
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
     * @param appId 应用ID
     * @return Reply
     */
    @GetMapping("/v1.1/roles")
    public Reply getRoles(@RequestHeader("Authorization") String token, @RequestParam(value = "appid", required = false) String appId) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return service.getRoles(accessToken, appId);
    }

    /**
     * 获取指定的角色
     *
     * @param roleId 角色ID
     * @return Reply
     */
    @GetMapping("/v1.1/roles/{id}")
    public Reply getRole(@PathVariable("id") String roleId) {
        return service.getRole(roleId);
    }

    /**
     * 新增角色
     *
     * @param token 访问令牌
     * @param role  角色实体数据
     * @return Reply
     */
    @PostMapping("/v1.1/roles")
    public Reply addRole(@RequestHeader("Authorization") String token, @RequestBody Role role) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return service.addRole(accessToken, role);
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/roles/{id}")
    public Reply deleteRole(@PathVariable("id") String roleId) {
        return service.deleteRole(roleId);
    }

    /**
     * 更新角色数据
     *
     * @param role 角色实体数据
     * @return Reply
     */
    @PutMapping("/v1.1/roles/{id}")
    public Reply updateRole(@RequestBody Role role) {
        return service.updateRole(role);
    }

    /**
     * 添加角色成员
     *
     * @param members 成员集合
     * @return Reply
     */
    @PostMapping("/v1.1/roles/{id}/members")
    public Reply addRoleMember(@RequestBody List<Member> members) {
        return service.addRoleMember(members);
    }

    /**
     * 移除角色成员
     *
     * @param userId 用户ID
     * @return Reply
     * @Auth 郑昊
     */
    @DeleteMapping("/v1.1/roles/members/{userid}")
    public Reply removeRoleMember(@PathVariable("userid") String userId) {
        return service.removeRoleMemberByUserId(userId);
    }

    /**
     * 移除角色成员
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/roles/{id}/members/{userid}")
    public Reply removeRoleMember(@PathVariable("id") String roleId, @PathVariable("userid") String userId) {
        return service.removeRoleMember(roleId, userId);
    }

    /**
     * 批量移除角色成员
     *
     * @param list 成员关系ID集合
     * @return Reply
     */
    @DeleteMapping("/v1.1/roles/{id}/members")
    public Reply removeRoleMembers(@RequestBody List<String> list) {
        return service.removeRoleMembers(list);
    }

    /**
     * 获取指定名称的角色的成员用户
     *
     * @param token    访问令牌
     * @param roleName 角色名称
     * @return Reply
     */
    @GetMapping("/v1.1/roles/{name}/users")
    public Reply getRoleUsersByName(@RequestHeader("Authorization") String token, @PathVariable("name") String roleName) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return service.getRoleUsersByName(accessToken, roleName);
    }
}
