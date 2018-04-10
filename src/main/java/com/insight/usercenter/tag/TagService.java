package com.insight.usercenter.tag;


import com.insight.usercenter.common.entity.Tag;
import com.insight.usercenter.tag.dto.TagDTO;
import com.insight.util.pojo.Reply;

/**
 * @author luwenbao
 * @date 2018/1/4.
 * @remark 标签管理服务接口
 */
public interface TagService {

    /**
     * 获取指定的标签
     *
     * @param tagId 标签ID
     * @return Reply
     */
    Reply getTag(String tagId);

    /**
     * 根据条件获取在线设备id，离线用户id
     *
     * @param tag 标签集合
     * @return Reply
     */
    Reply getDeviceAndUserId(TagDTO tag);

    /**
     * 根据标签集合和用户ID集合查询无重复的用户ID集合
     *
     * @param tag
     * @return
     */
    Reply getNoRepeatUserId(TagDTO tag);

    /**
     * 新增标签
     *
     * @param tag 标签实体数据
     * @return Reply
     */
    Reply addTag(Tag tag);

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @return Reply
     */
    Reply deleteTag(String tagId);

    /**
     * 更新标签数据
     *
     * @param tag 标签实体数据
     * @return Reply
     */
    Reply updateTag(Tag tag);


}
