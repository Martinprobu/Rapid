package org.rapid.domain.kuduhive;

import org.rapid.common.enums.JdbcType;
import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.GrammarUtil;
import org.rapid.domain.AbstractTableConverHandler;

import java.util.Map;

public class KuduTableConverHandler extends AbstractTableConverHandler {

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
                type = "Integer";
                break;
            case TINYINT:
                type = "Integer";
                break;
            case VARCHAR:
                type = "String";
                break;
            case CHAR:
                type = "String";
                break;
            case DECIMAL:
                type = "Decimal";
                break;
            case DATE:
                type = "Date";
                break;
            case DATETIME:
                type = "Datetime";
                break;
            case TIMESTAMP:
                type = "Timestamp";
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
