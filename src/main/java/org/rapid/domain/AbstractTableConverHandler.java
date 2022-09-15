package org.rapid.domain;

import org.rapid.domain.java.JavaTableConverHandler;
import org.rapid.domain.typescript.TypescriptTableConverHandler;

import static org.rapid.common.enums.DomainType.*;

public abstract class AbstractTableConverHandler implements ITableConverHandler {

    public ITableConverHandler selectPlatForm(String path) {
        String typeName = path.substring(path.lastIndexOf("."));
        switch (getType(typeName)) {
            case JAVA:
                return new JavaTableConverHandler();
            case TYPESCRIPT:
                return new TypescriptTableConverHandler();
            default:
                return new JavaTableConverHandler();
        }
    }
}
