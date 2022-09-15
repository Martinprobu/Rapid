package org.rapid.domain.typescript;

import org.rapid.common.enums.DomainType;
import org.rapid.domain.IDomainHandler;

import java.util.logging.Logger;

@Deprecated
public class TypescriptDomainDefaultHandler implements IDomainHandler {
    Logger log = Logger.getGlobal();
    @Override
    public String getName() {
        return DomainType.REACT.getName();
    }

    @Override
    public void doHandler() {
        log.info("Typescript doHandler");
    }
}
