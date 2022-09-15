package org.rapid.domain.react;

import org.rapid.common.enums.DomainType;
import org.rapid.domain.IDomainHandler;

import java.util.logging.Logger;

@Deprecated
public class ReactDomainDefaultHandler implements IDomainHandler {
    Logger log = Logger.getGlobal();
    @Override
    public String getName() {
        return DomainType.REACT.getName();
    }

    @Override
    public void doHandler() {
        log.info("React doHandler");
    }
}
