package org.rapid.domain.template;

import org.rapid.common.enums.DomainType;
import org.rapid.domain.IDomainHandler;
import org.rapid.domain.template.service.CodeGen;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class TemplateDomainDefaultHandler implements IDomainHandler {
    Logger log = Logger.getGlobal();
    @Override
    public String getName() {
        return DomainType.TEMPLATE.getName();
    }

    @Override
    public void doHandler() throws IOException {
        log.info("Template doHandler");

        CodeGen code = new CodeGen();
        List<String> list = code.readTables();
        list.clear();
        list.add("order");
        System.out.println("Please choose which table need to gen the code, number or table_name is allowable.");

        // iterator the template files
        String configPath = "template";
        String realPath = this.getClass().getClassLoader().getResource("").getPath() + configPath;
        File dir = new File(realPath);
        File[] directoryListing = dir.listFiles();
        // the first level
        if (directoryListing != null) {
            for (File child1 : directoryListing) {
                if (child1.isDirectory()) {
                    iteraFolder(child1, list, code);
                }
                /*
                else {
                    for (int i = 0; i < list.size(); i++) {
//                        log.info("Table_id: " + i + "\tTable_name: " + tableList.get(i));
                        code.readTable2(list.get(i), child1);
                    }
                }
                */
            }
        }
    }

    public void iteraFolder(File child, List<String> tableList, CodeGen code) throws IOException {
        if (child.isDirectory()) {
            for (File child2 : child.listFiles()) {
                iteraFolder(child2, tableList, code);
            }
        } else {
            for (int i = 0; i < tableList.size(); i++) {
                log.info("Table_id: " + i + "\tTable_name: " + tableList.get(i));
                code.readTable(tableList.get(i), child);
            }
        }
    }
}
