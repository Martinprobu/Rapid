package org.rapid.domain.kuduhive;

import org.rapid.common.enums.DomainType;
import org.rapid.domain.IDomainHandler;

import java.util.logging.Logger;

@Deprecated
public class KudoHiveDomainDefaultHandler implements IDomainHandler {
    Logger log = Logger.getGlobal();
    @Override
    public String getName() {
        return DomainType.KUDUHIVE.getName();
    }

    @Override
    public void doHandler() {
        log.info("KudoHive doHandler");
    }
}
