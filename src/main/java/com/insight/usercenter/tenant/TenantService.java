package com.insight.usercenter.tenant;


import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.entity.Tenant;
import com.insight.usercenter.tenant.dto.TenantDTO;
import com.insight.util.pojo.Reply;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户服务接口
 */
public interface TenantService {

    /**
     * 根据设定的条件查询租户信息(分页)
     *
     * @param tenant 租户查询实体对象
     * @return Reply
     */
    Reply getTenants(TenantDTO tenant);

    /**
     * 查询指定ID的租户信息
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply getTenant(String id);

    /**
     * 查询指定ID的租户绑定的应用集合
     *
     * @param tenantId 租户ID
     * @return Reply
     */
    Reply getTenantApps(String tenantId);

    /**
     * 获取当前用户管理的全部租户信息
     *
     * @param userId 用户ID
     * @return Reply
     */
    Reply getMyTenants(String userId);

    /**
     * 查询当前用户管理的指定ID的租户信息
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    Reply getMyTenant(Token token, String id);

    /**
     * 新增租户
     *
     * @param token  访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    Reply addTenant(Token token, Tenant tenant);

    /**
     * 删除指定ID的租户
     *
     * @param id 租户ID
     * @return Reply
     */
    Reply deleteTenant(String id);

    /**
     * 更新租户数据
     *
     * @param tenant 租户实体数据
     * @return Reply
     */
    Reply updateTenant(Tenant tenant);

    /**
     * 审核租户
     *
     * @param token  访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    Reply auditTenant(Token token, Tenant tenant);

    /**
     * 设置租户管理人
     *
     * @param token  访问令牌
     * @param tenant 租户实体数据
     * @return Reply
     */
    Reply divertTenant(Token token, Tenant tenant);

    /**
     * 设置应用与指定ID的租户的绑定关系
     *
     * @param id     租户ID
     * @param appIds 应用ID集合
     * @return Reply
     */
    Reply addAppsToTenant(String id, List<String> appIds);

    /**
     * 建立当前用户与指定ID的租户的绑定关系
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    Reply addUserToTenant(Token token, String id);

    /**
     * 解除当前用户与指定ID的租户的绑定关系
     *
     * @param token 访问令牌
     * @param id    租户ID
     * @return Reply
     */
    Reply removeUserFromTenant(Token token, String id);
}
