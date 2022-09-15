package org.rapid.core;

import org.rapid.common.enums.DomainType;

import java.io.IOException;

/**
 * inteface of the main entrance
 * @author martinprobu
 * @version 1.0
 */
public interface IMain {

    /**
     * run the all domain
     */
    public void exec(String tableArr);

    /**
     * run the specical domain, like typescripe, java, react ...
     */
    public void exec(DomainType type, String tableArr) throws IOException;

}
