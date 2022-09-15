package org.rapid.domain.java;

import org.rapid.common.enums.DomainType;
import org.rapid.domain.IDomainHandler;
import java.util.logging.Logger;

public class JavaDomainDefaultHandler implements IDomainHandler {
    Logger log = Logger.getGlobal();
    @Override
    public String getName() {
        return DomainType.JAVA.getName();
    }

    @Override
    public void doHandler() {
        log.info("Java doHandler");
    }
}
