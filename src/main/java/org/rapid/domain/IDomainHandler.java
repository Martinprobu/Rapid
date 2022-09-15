package org.rapid.domain;

import java.io.IOException;

public interface IDomainHandler {

    public String getName();

    public void doHandler(String tableArr) throws IOException;

}
