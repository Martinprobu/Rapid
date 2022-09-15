package org.rapid.domain.typescript;

import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.GrammarUtil;
import org.rapid.domain.AbstractTableConverHandler;

import java.util.Map;

public class TypescriptTableConverHandler extends AbstractTableConverHandler {

    public TableDesc doHandler(Map<String, Object> map)  {
        String type = map.get("Type").toString().substring(0, map.get("Type").toString().indexOf("("));
        if (type.equals("bigint")) {
            type = "number";
        }
        if (type.equals("double")) {
            type = "number";
        }
        if (type.equals("float")) {
            type = "number";
        }
        if (type.equals("int") || type.equals("tinyint")) {
            type = "number";
        }
        if (type.equals("varchar") || type.equals("char")) {
            type = "string";
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
