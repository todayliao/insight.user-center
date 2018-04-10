package com.insight.usercenter.org;


import com.insight.util.pojo.Reply;

/**
 * @author 郑昊
 * @date 2017/10/30
 * @remark 角色管理服务接口
 */
public interface OrgService {

    /**
     * 获取全部部门
     *
     * @return Reply
     */
    Reply getOrgs();

    /**
     * 添加成员
     *
     * @param postId 部门ID
     * @param userId 成员ID
     * @return Reply
     */
    Reply addPostMember(String postId, String userId);

    /**
     * 移除成员
     *
     * @param postId 部门ID
     * @param userId 成员ID
     * @return Reply
     */
    Reply removePostMember(String postId, String userId);

    /**
     * 用户部门信息
     *
     * @param userId 成员ID
     * @return Reply
     */
    Reply userInfo(String userId);
}
