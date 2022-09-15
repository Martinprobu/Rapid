package org.rapid.core.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import org.rapid.common.enums.DomainType;
import org.rapid.domain.IDomainHandler;
import org.springframework.stereotype.Component;

@Component
public class V1Service implements IExecService {

    Logger log = Logger.getGlobal();
//    @Override
    public void handle(DomainType type, String tableArr) throws IOException {

        log.info("V1Service service");

        // do it using spi
        ServiceLoader<IDomainHandler> s = ServiceLoader.load(IDomainHandler.class);
        Iterator<IDomainHandler> iterator = s.iterator();
        while (iterator.hasNext()) {
            IDomainHandler handler =  iterator.next();
            if (handler.getName().equals(type.getName())) {
                handler.doHandler(tableArr);
            }
        }

    }

}
