package com.insight.usercenter.common.dto;

import static com.insight.usercenter.common.annotation.ColumnName.Policy;

/**
 * @author 宣炳刚
 * @date 2017/11/10
 * @remark 字段属性信息类
 */
public class FieldInfo {

    /**
     * 字段名
     */
    private String filedName;

    /**
     * 字段类型名称
     */
    private String typeName;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列日期格式
     */
    private String dateFormat;

    /**
     * 列策略
     */
    private Policy columnPolicy;

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Policy getColumnPolicy() {
        return columnPolicy;
    }

    public void setColumnPolicy(Policy columnPolicy) {
        this.columnPolicy = columnPolicy;
    }
}
