package com.insight.usercenter.org;

import com.insight.usercenter.common.entity.Organization;
import com.insight.usercenter.common.mapper.OrgMapper;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 郑昊
 * @date 2017/10/30
 * @remark 角色管理服务接口
 */
@Service
public class OrgServiceImpl implements OrgService {
    private final OrgMapper orgMapper;

    /**
     * 构造函数
     *
     * @param orgMapper 自动注入的OrgMapper
     */
    @Autowired
    public OrgServiceImpl(OrgMapper orgMapper) {
        this.orgMapper = orgMapper;
    }

    /**
     * 获取全部部门
     *
     * @return Reply
     */
    @Override
    public Reply getOrgs() {
        List<Organization> departments = orgMapper.getOrgs();

        return ReplyHelper.success(departments);
    }

    /**
     * 添加成员
     *
     * @param postId 部门ID
     * @param userId 成员ID
     * @return Reply
     */
    @Override
    public Reply addPostMember(String postId, String userId) {

        // 添加成员
        Integer count = orgMapper.addPostMember(postId, userId);

        return count > 0 ? ReplyHelper.success(count) : ReplyHelper.error();
    }

    /**
     * 移除成员
     *
     * @param postId 部门ID
     * @param userId 成员ID
     * @return Reply
     */
    @Override
    public Reply removePostMember(String postId, String userId) {

        // 删除成员
        Integer count = orgMapper.removePostMember(postId, userId);

        return count > 0 ? ReplyHelper.success(count) : ReplyHelper.error();
    }

    /**
     * 用户部门信息
     *
     * @param userId 成员ID
     * @return Reply
     */
    @Override
    public Reply userInfo(String userId) {

        // 读取数据
        List<Organization> orgs = orgMapper.getOrg(userId);

        return ReplyHelper.success(orgs);
    }
}
