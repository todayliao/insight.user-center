package com.insight.usercenter.org;

import com.insight.usercenter.common.dto.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author 郑昊
 * @date 2017/10/30
 * @remark 部门管理服务控制器
 */
@RestController
@RequestMapping("/orgapi")
public class OrgController {
    @Autowired
    OrgService service;

    /**
     * 获取全部部门
     *
     * @return Reply
     */
    @GetMapping("/v1.1/orgs")
    public Reply getOrgs() {
        return service.getOrgs();
    }

    /**
     * 添加成员
     *
     * @param postId 部门职位ID
     * @param userId 成员ID
     * @return Reply
     */
    @PutMapping("/v1.1/orgs/{id}/member")
    public Reply addPostMember(@PathVariable("id") String postId, @RequestBody String userId) {
        return service.addPostMember(postId, userId);
    }

    /**
     * 移除
     *
     * @param postId 部门职位ID
     * @param userId 成员ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/orgs/{id}/member")
    public Reply removePostMember(@PathVariable("id") String postId, @RequestBody String userId) {
        return service.removePostMember(postId, userId);
    }

    /**
     * 用户部门信息
     *
     * @param userId 成员ID
     * @return Reply
     */
    @GetMapping("/v1.1/orgs/userInfo")
    public Reply userInfo(@RequestParam String userId) {
        return service.userInfo(userId);
    }
}
