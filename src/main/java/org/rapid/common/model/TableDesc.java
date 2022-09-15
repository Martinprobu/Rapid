package org.rapid.common.model;

public class TableDesc {
    private String field; // orderType
    private String type;
    private String allowNull;
    private String key; // order_type
    private String defaultValue;
    private String extra;

    public TableDesc() {
    }

    public TableDesc(String field, String type, String allowNull, String key, String defaultValue, String extra) {
        this.field = field;
        this.type = type;
        this.allowNull = allowNull;
        this.key = key;
        this.defaultValue = defaultValue;
        this.extra = extra;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAllowNull() {
        return allowNull;
    }

    public void setAllowNull(String allowNull) {
        this.allowNull = allowNull;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "field=" + this.field + "," +
                "type=" + this.type + "," +
                "null=" + this.allowNull + "," +
                "key=" + this.key + "," +
                "default=" + this.defaultValue + "," +
                "extra=" + this.extra;
    }
}
