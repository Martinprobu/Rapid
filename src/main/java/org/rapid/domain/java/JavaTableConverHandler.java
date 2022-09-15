package org.rapid.domain.java;

import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.GrammarUtil;
import org.rapid.domain.AbstractTableConverHandler;
import java.util.Map;

public class JavaTableConverHandler extends AbstractTableConverHandler {

    public TableDesc doHandler(Map<String, Object> map)  {
        String type = map.get("Type").toString().substring(0, map.get("Type").toString().indexOf("("));
        if (type.equals("bigint")) {
            type = "Long";
        }
        if (type.equals("double")) {
            type = "Double";
        }
        if (type.equals("float")) {
            type = "Float";
        }
        if (type.equals("int") || type.equals("tinyint")) {
            type = "Integer";
        }
        if (type.equals("varchar") || type.equals("char")) {
            type = "String";
        }
        return new TableDesc(
                GrammarUtil.nameCovert_(map.get("Field").toString(), false),
                type,
                map.get("Null").toString(),
                map.get("Field").toString(),
                String.valueOf(map.get("Default")),
                String.valueOf(map.get("Comment")));
    }
}
