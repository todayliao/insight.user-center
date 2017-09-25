package com.apin.usercenter.common.entity;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2017/9/13
 * @remark 导航实体类
 */
public class Navigator implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 模块/模块组ID
     */
    private String id;

    /**
     * 所属模块组ID(模块组该值为空)
     */
    private String groupId;

    /**
     * 索引,排序用
     */
    private Integer index;

    /**
     * 模块/模块组名称
     */
    private String name;

    /**
     * 模块/模块组图标(URL)
     */
    private String icon;

    /**
     * 模块路由URL(模块组该值为空)
     */
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
