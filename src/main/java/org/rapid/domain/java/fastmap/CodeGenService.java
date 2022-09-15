package org.rapid.domain.java.fastmap;

import org.rapid.common.enums.DomainType;
import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.ConfigUtil;
import org.rapid.common.utils.GrammarUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.*;

/**
 * 代码生成业务类
 */
public class CodeGenService {

    public static HashMap<String, String> configMap = ConfigUtil.wrapConfigMap(DomainType.JAVA);
    //配置
    public static final String JDBCPATH = configMap.get("domain.jdbc_path");       //jdbc
    public static final String JDBCUSERNAME = configMap.get("domain.jdbc_username");                       //jdbc
    //    public static final String JDBCUSERNAME = "process_dev";                //jdbc
    public static final String JDBCPSWORD = configMap.get("domain.jdbc_psword");                //jdbc
    //    public static final String JDBCPSWORD = "Azxs1234##";                   //jdbc
    public static final String JDBCCLASS = configMap.get("domain.jdbc_class");         //jdbc

    private static JdbcTemplate jdbcTemplate;

    static {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(JDBCCLASS);
        dataSource.setUrl(JDBCPATH);
        dataSource.setUsername(JDBCUSERNAME);
        dataSource.setPassword(JDBCPSWORD);

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void doExecute(String sql) {
        jdbcTemplate.execute(sql);
    }

    public int doUpdate(String sql) {
        return jdbcTemplate.update(sql);
    }

    /**
     * 列出所有表
     */
    public List showTables(String sql) {
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
        List<String> list = new ArrayList<String>();
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < resultList.size(); i++) {
            map = resultList.get(i);
            Set<String> keys = map.keySet();
            String key = keys.iterator().next();
            list.add(String.valueOf(map.get(key)));
        }
        return list;
    }

    /**
     * 列出表的字段描述
     */
    public List<TableDesc> descTable(String tableName) {
        //id	int(11)	NO	PRI	NULL	auto_increment
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList("show full columns from `" + tableName + "`");
        Map<String, Object> map = new HashMap<String, Object>();
        TableDesc table;
        List<TableDesc> list = new ArrayList<TableDesc>();
        for (int i = 0; i < resultList.size(); i++) {
            map = resultList.get(i);
            table = new TableDesc(
                    GrammarUtil.nameCovert_(map.get("Field").toString(), false),
                    map.get("Type").toString(),
                    map.get("Null").toString(),
                    map.get("Field").toString(),
                    String.valueOf(map.get("Default")),
                    String.valueOf(map.get("Comment")));//map.get("Key").toString(), map.get("Default").toString(), map.get("Extra").toString());
            list.add(table);
            System.out.println(table.toString());
        }

        return list;
    }

    /**
     * 列出表的描述
     */
    public String descTableComment(String databaseName, String tableName) {
        String sql = "SELECT table_comment FROM information_schema.tables " +
                " WHERE table_schema = '" + databaseName + "' AND table_name = " + "'" + tableName + "'";
        String comment = jdbcTemplate.queryForObject(sql, String.class);
        return comment.replace("表", "");
//            return String.valueOf(map.get("table_comment"));
    }

}
