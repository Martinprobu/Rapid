package org.rapid.domain.objectc;

import org.rapid.common.enums.JdbcType;
import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.GrammarUtil;
import org.rapid.domain.AbstractTableConverHandler;

import java.util.Map;

public class ObjectTableConverHandler extends AbstractTableConverHandler {

    public TableDesc doHandler(Map<String, Object> map)  {
        String type = map.get("Type").toString().substring(0, map.get("Type").toString().indexOf("("));
        JdbcType jType = JdbcType.getType(type);
        switch (jType) {
            case BIGINT:
                type = "Long";
                break;
            case DOUBLE:
                type = "Double";
                break;
            case FLOAT:
                type = "Float";
                break;
            case INT:
                type = "Int";
                break;
            case TINYINT:
                type = "Short";
                break;
            case VARCHAR:
                type = "Char";
                break;
            case CHAR:
                type = "Char";
                break;
            case DECIMAL:
                type = "Char";
                break;
            case DATE:
                type = "Date";
                break;
            case DATETIME:
                type = "DateTime";
                break;
            case TIMESTAMP:
                type = "Timestamp";
                break;
            default:
                type = "char";
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
