package org.rapid.domain.golang;

import org.rapid.common.enums.JdbcType;
import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.GrammarUtil;
import org.rapid.domain.AbstractTableConverHandler;

import java.util.Map;

public class GolangTableConverHandler extends AbstractTableConverHandler {

    public TableDesc doHandler(Map<String, Object> map)  {
        String type = map.get("Type").toString().substring(0, map.get("Type").toString().indexOf("("));
        JdbcType jType = JdbcType.getType(type);
        switch (jType) {
            case BIGINT:
                type = "int64";
                break;
            case DOUBLE:
                type = "float64";
                break;
            case FLOAT:
                type = "float32";
                break;
            case INT:
                type = "int32";
                break;
            case TINYINT:
                type = "int8";
                break;
            case VARCHAR:
                type = "string";
                break;
            case CHAR:
                type = "string";
                break;
            case DECIMAL:
                type = "string";
                break;
            case DATE:
                type = "Time";
                break;
            case DATETIME:
                type = "Time";
                break;
            case TIMESTAMP:
                type = "Time";
                break;
            default:
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
