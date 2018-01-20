package com.insight.usercenter.tag;

import com.insight.usercenter.common.Verify;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.entity.Tag;
import com.insight.usercenter.tag.dto.TagDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author luwenbao
 * @date 2018/1/4.
 * @remark 标签管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/tagapi")
public class TagController {
    @Autowired
    private TagService tagService;

    /**
     * 获取指定标签
     *
     * @param tagId 标签ID
     * @return Reply
     */
    @GetMapping("/v1.1/tags/{id}")
    public Reply getTag(@RequestHeader("Authorization") String token, @PathVariable("id") String tagId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getTags");
        if (!result.getSuccess()) {
            return result;
        }

        return tagService.getTag(tagId);
    }

    /**
     * 新增用户标签
     *
     * @param token 访问令牌
     * @param tag   标签数据
     * @return Reply
     */
    @PostMapping("/v1.1/tags")
    public Reply addApp(@RequestHeader("Authorization") String token, @RequestBody Tag tag) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("addTag");
        if (!result.getSuccess()) {
            return result;
        }

        return tagService.addTag(tag);
    }

    /**
     * 删除用户标签
     *
     * @param token 访问令牌
     * @param tagId 标签ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/tags/{id}")
    public Reply deleteApp(@RequestHeader("Authorization") String token, @PathVariable("id") String tagId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("deleteTag");
        if (!result.getSuccess()) {
            return result;
        }

        return tagService.deleteTag(tagId);
    }

    /**
     * 更新用户标签数据
     *
     * @param tag 标签数据
     * @return Reply
     */
    @PutMapping("/v1.1/tags/{id}")
    public Reply updateApp(@RequestHeader("Authorization") String token, @RequestBody Tag tag) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("updateTag");
        if (!result.getSuccess()) {
            return result;
        }

        return tagService.updateTag(tag);
    }

    /**
     * 根据条件获取在线设备id,离线用户id集合
     *
     * @param tag 查询条件
     * @return Reply
     */
    @PostMapping("/v1.1/tags/devices")
    public Reply getDeviceIdAndUserId(@RequestBody TagDTO tag) {
        return tagService.getDeviceAndUserId(tag);
    }

    /**
     * 根据标签集合和用户ID集合查询无重复的用户ID集合
     *
     * @param tag
     * @return Reply
     */
    @PostMapping("/v1.1/tags/users")
    public Reply getNoRepeatUserId(@RequestBody TagDTO tag) {
        return tagService.getNoRepeatUserId(tag);
    }
}
