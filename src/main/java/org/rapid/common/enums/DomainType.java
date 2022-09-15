package org.rapid.common.enums;

public enum DomainType {
    TEMPLATE("template"),
    JAVA("java"),
    TYPESCRIPT("ts"),
    KUDUHIVE("sql"),
    REACT("tsx"),
    VUE("vue");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    DomainType(String name) {
        this.name = name;
    }

    public static DomainType getType(String typeName) {
        DomainType[] enums = DomainType.values();
        for (DomainType type : enums) {
            if (type.getName().equals(typeName)) {
                return type;
            }
        }
        return DomainType.JAVA;
    }
}
