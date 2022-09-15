package org.rapid.domain.vue;

import org.rapid.common.enums.DomainType;
import org.rapid.domain.IDomainHandler;

import java.util.logging.Logger;

@Deprecated
public class VueDomainDefaultHandler implements IDomainHandler {
    Logger log = Logger.getGlobal();
    @Override
    public String getName() {
        return DomainType.VUE.getName();
    }

    @Override
    public void doHandler(String tableArr) {
        log.info("Vue doHandler");
    }
}
