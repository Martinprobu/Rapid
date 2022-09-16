package org.rapid.domain.rust;

import org.rapid.common.enums.JdbcType;
import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.GrammarUtil;
import org.rapid.domain.AbstractTableConverHandler;

import java.util.Map;

public class RustTableConverHandler extends AbstractTableConverHandler {

    public TableDesc doHandler(Map<String, Object> map)  {
        String type = map.get("Type").toString().substring(0, map.get("Type").toString().indexOf("("));
        JdbcType jType = JdbcType.getType(type);
        switch (jType) {
            case BIGINT:
                type = "Integer";
                break;
            case DOUBLE:
                type = "Integer";
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
                type = "char";
                break;
            case CHAR:
                type = "char";
                break;
            case DECIMAL:
                type = "char";
                break;
            case DATE:
                type = "Date";
                break;
            case DATETIME:
                type = "DateTime";
                break;
            case TIMESTAMP:
                type = "Time";
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
