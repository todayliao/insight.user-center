package com.insight.usercenter.tag;

import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.entity.Device;
import com.insight.usercenter.common.entity.Tag;
import com.insight.usercenter.common.mapper.TagMapper;
import com.insight.usercenter.common.utils.Generator;
import com.insight.usercenter.common.utils.ReplyHelper;
import com.insight.usercenter.tag.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luwenbao
 * @date 2018/1/4.
 * @remark 标签管理服务接口实现
 */
@Service
public class TagServiceImpl implements TagService {
    private final TagMapper tagMapper;

    /**
     * 构造函数
     *
     * @param tagMapper 自动注入的TagMapper
     */
    @Autowired
    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    /**
     * 获取指定的标签
     *
     * @param tagId 标签ID
     * @return Reply
     */
    @Override
    public Reply getTag(String tagId) {
        Tag tag = tagMapper.getTag(tagId);

        return ReplyHelper.success(tag);
    }

    /**
     * 根据条件获取在线设备id，离线用户id
     *
     * @param tag 标签集合
     * @return 设备id集合
     */
    @Override
    public Reply getDeviceAndUserId(TagDTO tag) {
        Map<String, Object> map = new HashMap<>(16);
        if (tag.getOnline()) {
            List<String> deviceIdList = tagMapper.getDeviceId(tag.getUserIds(), false);
            map.put("deviceIdList", Optional.ofNullable(deviceIdList).orElse(new ArrayList<>()));
            List<String> offLineUserIdList = tagMapper.getOffLineUserId(tag.getUserIds());
            map.put("userIdList", Optional.ofNullable(offLineUserIdList).orElse(new ArrayList<>()));
        } else {
            List<Device> deviceList = tagMapper.getAllDeviceId(tag.getUserIds());
            map.put("deviceIdList", deviceList.stream().map(Device::getId).collect(Collectors.toList()));
            map.put("userIdList", new ArrayList<>());
        }

        return ReplyHelper.success(map);
    }

    /**
     * 根据标签集合和用户ID集合查询无重复的用户ID集合
     *
     * @param tag
     * @return
     */
    @Override
    public Reply getNoRepeatUserId(TagDTO tag) {
        Map<String, Object> map = new HashMap<>(16);
        Boolean isExists = (null == tag.getTags() || tag.getTags().size() == 0) && (null == tag.getUserIds() || tag.getUserIds().size() == 0);
        List<String> resultList;
        if (isExists) {
            resultList = tagMapper.getAllDeviceUser();
        } else {
            resultList = tagMapper.getNoRepeatUserId(tag.getUserIds(), tag.getTags());
        }
        map.put("userIdList", Optional.ofNullable(resultList).orElse(new ArrayList<>()));
        return ReplyHelper.success(map);
    }

    /**
     * 新增标签
     *
     * @param tag 标签实体数据
     * @return Reply
     */
    @Override
    public Reply addTag(Tag tag) {
        Tag dbTag = tagMapper.getTagByUserIdAndTag(tag.getUserId(), tag.getTag());
        if (dbTag == null) {
            tag.setId(Generator.uuid());
            tag.setCreatorUserId(StringUtils.isBlank(tag.getCreatorUserId()) ? "test" : tag.getCreatorUserId());
            tag.setCreatedTime(new Date());
            Integer count = tagMapper.addTag(tag);
            return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能将数据写入数据库!");
        }
        return ReplyHelper.fail("已存在该用户标签！");
    }

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @return Reply
     */
    @Override
    public Reply deleteTag(String tagId) {
        Integer count = tagMapper.deleteTag(tagId);
        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能删除指定的标签!");
    }

    /**
     * 更新标签数据
     *
     * @param tag 标签实体数据
     * @return Reply
     */
    @Override
    public Reply updateTag(Tag tag) {
        Integer count = tagMapper.updateTag(tag);
        return count > 0 ? ReplyHelper.success() : ReplyHelper.fail("未能更新指定的标签!");
    }
}
