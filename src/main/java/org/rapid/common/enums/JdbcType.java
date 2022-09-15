package org.rapid.common.enums;

public enum JdbcType {
    BIGINT("bigint"),
    DOUBLE("double"),
    FLOAT("float"),
    INT("int"),
    TINYINT("tinyint"),
    VARCHAR("varchar"),
    CHAR("char"),
    DATE("date"),
    DATETIME("datetime"),
    TIMESTAMP("timestamp"),
    DECIMAL("decimal");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    JdbcType(String name) {
        this.name = name;
    }

    public static JdbcType getType(String typeName) {
        JdbcType[] enums = JdbcType.values();
        for (JdbcType type : enums) {
            if (type.getName().equals(typeName)) {
                return type;
            }
        }
        return JdbcType.VARCHAR;
    }
}
