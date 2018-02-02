package com.insight.usercenter.tenant;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.entity.App;
import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.Role;
import com.insight.usercenter.common.entity.Tenant;
import com.insight.usercenter.common.mapper.RoleMapper;
import com.insight.usercenter.common.mapper.TenantMapper;
import com.insight.usercenter.common.utils.Generator;
import com.insight.usercenter.common.utils.ReplyHelper;
import com.insight.usercenter.tenant.dto.TenantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务实现
 */
@Service
public class TenantServiceImpl implements TenantService {
    private final TenantMapper tenantMapper;
    private final RoleMapper roleMapper;

    /**
     * 构造方法
     *
     * @param tenantMapper 自动注入的TenantMapper
     * @param roleMapper   自动注入的RoleMapper
     */
    @Autowired
    public TenantServiceImpl(TenantMapper tenantMapper, RoleMapper roleMapper) {
        this.tenantMapper = tenantMapper;
        this.roleMapper = roleMapper;
    }

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param tenant 租户查询实体对象
     * @return Reply
     */
    @Override
    public Reply getTenants(TenantDTO tenant) {
        PageHelper.startPage(tenant.getPage(), tenant.getPageSize());
        List<Tenant> tenants = tenantMapper.getTenants(tenant);
        PageInfo<Tenant> pageInfo = new PageInfo<>(tenants);

        return ReplyHelper.success(tenants, pageInfo.getTotal());
    }

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Reply getTenant(String id) {
        Tenant tenant = tenantMapper.getTenant(id);

        return ReplyHelper.success(tenant);
    }

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param tenantId 租户ID
     * @return Reply
     */
    @Override
    public Reply getTenantApps(String tenantId) {
        List<App> apps = tenantMapper.getTenantApps(tenantId);

        return ReplyHelper.success(apps);
    }

    /**
     * 获取当前用户管理的全部租户信息
     *
     * @param userId 用户ID
     * @return Reply
     */
    @Override
    public Reply getMyTenants(String userId) {
        List<Tenant> tenants = tenantMapper.getMyTenants(userId);

        return ReplyHelper.success(tenants);
    }

    /**
     * 查询当前用户管理的指定ID的租户信息
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @Override
    public Reply getMyTenant(Token token, String id) {
        Tenant tenant = tenantMapper.getMyTenant(id, token.getUserId());

        return ReplyHelper.success(tenant);
    }

    /**
     * 新增租户
     *
     * @param token  访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    @Override
    public Reply addTenant(Token token, Tenant tenant) {
        String userId = token.getUserId();
        List<Tenant> tenants = tenantMapper.getMyTenants(userId);
        if (tenants.size() >= 5) {
            return ReplyHelper.fail("用户拥有的租户数量过多!");
        }

        String license = tenant.getLicense();
        if (license != null && tenants.stream().filter(i -> !i.getInvalid()).anyMatch(i -> license.equals(i.getLicense()))) {
            return ReplyHelper.invalidParam("该企业注册号已被注册");
        }

        tenant.setId(Generator.uuid());
        tenant.setManagerUserId(userId);
        tenant.setCreatorUserId(userId);
        Integer count = tenantMapper.addTenant(tenant);

        return count > 0 ? ReplyHelper.success("『" + tenant.getCompanyName() + "』注册成功") : ReplyHelper.error();
    }

    /**
     * 删除指定ID的租户
     *
     * @param id 租户ID
     * @return Reply
     */
    @Override
    public Reply deleteTenant(String id) {
        Integer count = tenantMapper.deleteTenant(id);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新租户数据
     *
     * @param tenant 租户实体数据
     * @return Reply
     */
    @Override
    public Reply updateTenant(Tenant tenant) {
        Tenant data = tenantMapper.getTenant(tenant.getId());
        if (data == null) {
            return ReplyHelper.invalidParam("指定的租户不存在!");
        }

        if (data.getTenantStatus().equals(1)) {
            return ReplyHelper.invalidParam("该租户已通过审核!");
        }

        Integer count = tenantMapper.updateTenant(tenant);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 审核租户
     *
     * @param token  访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    @Override
    @Transactional
    public Reply auditTenant(Token token, Tenant tenant) {
        Tenant data = tenantMapper.getTenant(tenant.getId());
        if (data == null) {
            return ReplyHelper.invalidParam("指定的租户不存在!");
        }

        if (data.getTenantStatus().equals(1)) {
            return ReplyHelper.invalidParam("该租户已通过审核!");
        }

        tenant.setAuditUserId(token.getUserId());

        // 更新审核信息,如未通过审核,则结束流程
        Integer count = tenantMapper.auditTenant(tenant);
        if (!tenant.getTenantStatus().equals(1)) {
            return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
        }

        // 关联创建人
        String userId = tenant.getCreatorUserId();
        count += tenantMapper.addUserToTenant(tenant.getId(), userId);

        // 根据类型初始化内置角色
        List<Role> roles = roleMapper.getRoleTemplates(tenant.getTenantType());
        for (Role role : roles) {
            role.setId(Generator.uuid());
            role.setTenantId(tenant.getId());
            role.setRemark(null);
            role.setCreatorUserId(token.getUserId());
            count += roleMapper.addRole(role);

            List<Function> list = roleMapper.getTemplateFunctions(role.getId());
            if (!list.isEmpty()) {
                count += roleMapper.addRoleFunction(role.getId(), list);
            }

            if (role.getBuiltin()) {
                count += roleMapper.addRoleMember(role.getId(), userId);
            }
        }

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 设置租户管理人
     *
     * @param token  访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    @Override
    public Reply divertTenant(Token token, Tenant tenant) {
        Tenant data = tenantMapper.getTenant(tenant.getId());
        if (data == null) {
            return ReplyHelper.invalidParam("指定的租户不存在!");
        }

        if (!data.getManagerUserId().equals(token.getUserId()) && !data.getCreatorUserId().equals(token.getUserId())) {
            return ReplyHelper.invalidParam("您无权设置该租户的管理人!");
        }

        tenant.setCreatorUserId(token.getUserId());
        Integer count = tenantMapper.divertTenant(tenant);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    @Override
    @Transactional
    public Reply addAppsToTenant(String id, List<String> appIds) {
        Integer count = tenantMapper.removeAppsFromTenant(id);
        count += tenantMapper.addAppsToTenant(id, appIds);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 建立当前用户与指定ID的租户的绑定关系
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @Override
    public Reply addUserToTenant(Token token, String id) {
        String userId = token.getUserId();
        Integer count = tenantMapper.getTenantUser(id, userId);
        if (count > 0) {
            return ReplyHelper.success("已与" + token.getUserName() + "绑定");
        }

        count = tenantMapper.addUserToTenant(id, userId);

        return count > 0 ? ReplyHelper.success(token.getUserName() + "绑定成功") : ReplyHelper.error();
    }

    /**
     * 解除当前用户与指定ID的租户的绑定关系
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    @Override
    public Reply removeUserFromTenant(Token token, String id) {
        String userId = token.getUserId();
        Integer count = tenantMapper.getTenantUser(id, userId);
        if (count.equals(0)) {
            return ReplyHelper.invalidParam("未与" + token.getUserName() + "绑定");
        }

        count = tenantMapper.removeUserFromTenant(id, userId);

        return count > 0 ? ReplyHelper.success(token.getUserName() + "解除绑定成功") : ReplyHelper.error();
    }
}
