package org.rapid.domain;

import org.rapid.common.model.TableDesc;

import java.io.IOException;
import java.util.Map;

public interface ITableConverHandler {

    public TableDesc doHandler(Map<String, Object> map) ;

}
