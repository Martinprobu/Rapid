package org.rapid.core.service;

import org.rapid.common.enums.DomainType;

import java.io.IOException;

public interface IExecService {
    public void handle(DomainType type, String tableArr) throws IOException;
}
