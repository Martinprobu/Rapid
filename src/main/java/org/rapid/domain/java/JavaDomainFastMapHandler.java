package org.rapid.domain.java;

import org.rapid.common.enums.DomainType;
import org.rapid.domain.IDomainHandler;
import org.rapid.domain.java.fastmap.CodeGen;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.logging.Logger;

public class JavaDomainFastMapHandler implements IDomainHandler {
    Logger log = Logger.getGlobal();
    @Value("/")
    private String packagepath;
    @Override
    public String getName() {
        return DomainType.JAVA.getName();
    }

    @Override
    public void doHandler() {
        log.info("Java FastMap doHandler");

        CodeGen code = new CodeGen();

        List<String> list = code.readTables();
        list.clear();
        list.add("order");
        System.out.println("Please choose which table need to gen the code, number or table_name is allowable.");
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Table_id: " + i + "\tTable_name: " + list.get(i));
        }
        for (int i = 0; i < list.size(); i++) {
            code.readTable(list.get(i));
        }


    }
}
