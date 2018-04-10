package com.insight.usercenter.role;

import com.insight.usercenter.common.Verify;
import com.insight.usercenter.common.entity.Member;
import com.insight.usercenter.common.entity.Role;
import com.insight.usercenter.role.dto.RoleDTO;
import com.insight.util.ReplyHelper;
import com.insight.util.Util;
import com.insight.util.pojo.Reply;
import com.insight.utils.redis.CallManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/15
 * @remark 角色管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/roleapi")
public class RoleController {
    private final RoleService service;
    private final CallManage callManage;

    @Autowired
    public RoleController(RoleService service, CallManage callManage) {
        this.service = service;
        this.callManage = callManage;
    }

    /**
     * 获取指定应用的全部角色
     *
     * @param token 访问令牌
     * @param role  角色查询实体对象
     * @return Reply
     */
    @GetMapping("/v1.1/roles")
    public Reply getRoles(@RequestHeader("Authorization") String token, RoleDTO role) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getRoles");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getRoles(verify.getBasis(), role);
    }

    /**
     * 获取指定的角色
     *
     * @param roleId 角色ID
     * @return Reply
     */
    @GetMapping("/v1.1/roles/{id}")
    public Reply getRole(@RequestHeader("Authorization") String token, @PathVariable("id") String roleId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getRoles");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getRole(verify.getBasis(), roleId);
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
        Verify verify = new Verify(token);
        Reply result = verify.compare("addRole");
        if (!result.getSuccess()) {
            return result;
        }

        // 限流,每客户端每30秒可访问一次
        String key = Util.md5("addRole" + verify.getTokenId());
        Integer surplus = callManage.getSurplus(key, 30);
        if (surplus > 0) {
            return ReplyHelper.tooOften();
        }

        return service.addRole(verify.getBasis(), role);
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/roles/{id}")
    public Reply deleteRole(@RequestHeader("Authorization") String token, @PathVariable("id") String roleId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("deleteRole");
        if (!result.getSuccess()) {
            return result;
        }

        return service.deleteRole(roleId);
    }

    /**
     * 更新角色数据
     *
     * @param role 角色实体数据
     * @return Reply
     */
    @PutMapping("/v1.1/roles/{id}")
    public Reply updateRole(@RequestHeader("Authorization") String token, @RequestBody Role role) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("updateRole");
        if (!result.getSuccess()) {
            return result;
        }

        return service.updateRole(role);
    }

    /**
     * 添加角色成员
     *
     * @param members 成员集合
     * @return Reply
     */
    @PostMapping("/v1.1/roles/{id}/members")
    public Reply addRoleMember(@RequestHeader("Authorization") String token, @RequestBody List<Member> members) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("addRoleMembers");
        if (!result.getSuccess()) {
            return result;
        }

        return service.addRoleMembers(verify.getBasis(), members);
    }

    /**
     * 移除角色成员
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/roles/{id}/members/{userid}")
    public Reply removeRoleMember(@RequestHeader("Authorization") String token, @PathVariable("id") String roleId, @PathVariable("userid") String userId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("removeRoleMember");
        if (!result.getSuccess()) {
            return result;
        }

        return service.removeRoleMember(roleId, userId);
    }

    /**
     * 批量移除角色成员
     *
     * @param list 成员关系ID集合
     * @return Reply
     */
    @DeleteMapping("/v1.1/roles/{id}/members")
    public Reply removeRoleMembers(@RequestHeader("Authorization") String token, @RequestBody List<String> list) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("removeRoleMembers");
        if (!result.getSuccess()) {
            return result;
        }

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
    public Reply getRoleUsersByName(@RequestHeader("Authorization") String token, @RequestParam("appid") String appId, @PathVariable("name") String roleName) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getRoleUsersByName");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getRoleUsersByName(verify.getBasis(), appId, roleName);
    }
}
