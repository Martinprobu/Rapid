package org.rapid.domain.typescript;

import org.rapid.common.enums.JdbcType;
import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.GrammarUtil;
import org.rapid.domain.AbstractTableConverHandler;

import java.util.Map;

public class TypescriptTableConverHandler extends AbstractTableConverHandler {

    public TableDesc doHandler(Map<String, Object> map)  {
        String type = map.get("Type").toString().substring(0, map.get("Type").toString().indexOf("("));
        JdbcType jType = JdbcType.getType(type);
        switch (jType) {
            case BIGINT:
                type = "number";
                break;
            case DOUBLE:
                type = "number";
                break;
            case FLOAT:
                type = "number";
                break;
            case INT:
                type = "number";
                break;
            case TINYINT:
                type = "number";
                break;
            case VARCHAR:
                type = "string";
                break;
            case CHAR:
                type = "string";
                break;
            case DECIMAL:
                type = "number";
                break;
            case DATE:
                type = "Date";
                break;
            case DATETIME:
                type = "Date";
                break;
            case TIMESTAMP:
                type = "number";
                break;
            default:
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
