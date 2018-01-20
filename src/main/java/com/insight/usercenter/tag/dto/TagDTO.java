package com.insight.usercenter.tag.dto;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2018/1/10
 * @remark 设备ID查询类
 */
public class TagDTO {

    private Boolean isOnline;

    private List<String> tags;

    private List<String> userIds;

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
