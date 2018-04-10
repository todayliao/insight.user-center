package com.insight.usercenter.tenant;

import com.insight.usercenter.common.Verify;
import com.insight.usercenter.common.entity.Tenant;
import com.insight.usercenter.tenant.dto.TenantDTO;
import com.insight.util.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/tenantapi")
public class TenantController {
    private final TenantService service;

    @Autowired
    public TenantController(TenantService service) {
        this.service = service;
    }

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param token  访问令牌
     * @param tenant 租户查询实体对象
     * @return Reply
     */
    @GetMapping("/v1.1/tenants")
    public Reply getTenants(@RequestHeader("Authorization") String token, TenantDTO tenant) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getTenants");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getTenants(tenant);
    }

    /**
     * 查询指定ID的租户信息
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @GetMapping("/v1.1/tenants/{id}")
    public Reply getTenant(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getTenants");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getTenant(id);
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @GetMapping("/v1.1/tenants/{id}/apps")
    public Reply getTenantApps(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getTenants");
        if (!result.getSuccess()) {
            return result;
        }

        return service.getTenantApps(id);
    }

    /**
     * 获取当前用户管理的全部租户信息
     *
     * @param token 访问令牌
     * @return Reply
     */
    @GetMapping("/v1.1/tenants/my")
    public Reply getMyTenants(@RequestHeader("Authorization") String token) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return service.getMyTenants(verify.getBasis().getUserId());
    }

    /**
     * 查询当前用户管理的指定ID的租户信息
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @GetMapping("/v1.1/tenants/my/{id}")
    public Reply getMyTenant(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return service.getMyTenant(verify.getBasis(), id);
    }

    /**
     * 新增租户
     *
     * @param token  访问令牌
     * @param tenant 租户实体对象
     * @return Reply
     */
    @PostMapping("/v1.1/tenants")
    public Reply addTenant(@RequestHeader("Authorization") String token, @RequestBody Tenant tenant) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return service.addTenant(verify.getBasis(), tenant);
    }

    /**
     * 删除指定ID的租户
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/tenants/{id}")
    public Reply deleteTenant(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("deleteTenant");
        if (!result.getSuccess()) {
            return result;
        }

        return service.deleteTenant(id);
    }

    /**
     * 更新租户数据
     *
     * @param token  访问令牌
     * @param tenant 租户实体对象
     * @return Reply
     */
    @PutMapping("/v1.1/tenants/{id}")
    public Reply updateTenant(@RequestHeader("Authorization") String token, @RequestBody Tenant tenant) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return service.updateTenant(tenant);
    }

    /**
     * 审核租户
     *
     * @param token  访问令牌
     * @param tenant 租户实体对象
     * @return Reply
     */
    @PutMapping("/v1.1/tenants/{id}/status")
    public Reply auditTenant(@RequestHeader("Authorization") String token, @Valid @RequestBody Tenant tenant) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("auditTenant");
        if (!result.getSuccess()) {
            return result;
        }

        return service.auditTenant(verify.getBasis(), tenant);
    }

    /**
     * 设置租户管理人
     *
     * @param token  访问令牌
     * @param tenant 租户实体对象
     * @return Reply
     */
    @PutMapping("/v1.1/tenants/{id}/manager")
    public Reply divertTenant(@RequestHeader("Authorization") String token, @RequestBody Tenant tenant) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return service.divertTenant(verify.getBasis(), tenant);
    }

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param token  访问令牌
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    @PostMapping("/v1.1/tenants/{id}/apps")
    public Reply addAppsToTenant(@RequestHeader("Authorization") String token, @PathVariable("id") String id, @RequestBody List<String> appIds) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("addAppsToTenant");
        if (!result.getSuccess()) {
            return result;
        }

        return service.addAppsToTenant(id, appIds);
    }

    /**
     * 建立当前用户与指定ID的租户的绑定关系
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @PostMapping("/v1.1/tenants/{id}/users")
    public Reply addUserToTenant(@RequestHeader("Authorization") String token, @PathVariable String id) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return service.addUserToTenant(verify.getBasis(), id);
    }

    /**
     * 解除当前用户与指定ID的租户的绑定关系
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/tenants/{id}/users")
    public Reply removeUserFromTenant(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return service.removeUserFromTenant(verify.getBasis(), id);
    }
}
