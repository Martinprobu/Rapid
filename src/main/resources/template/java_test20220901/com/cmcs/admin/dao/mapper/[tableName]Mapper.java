package [(${packagePath})].dao.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     操作日志相关
 *     为了兼容db字段的增删改, 传参用的Map
 * </pre>
 * @author [(${author})]
 */
@Mapper
public interface [(${className})]Mapper {

    @Select({"<script>",
            "select * from [(${tableName})]",
            "where status != -1",

            [# th:each="field : ${tableFileds}"]
            "<when test='map.[(${field.key})] != null'>",
            "and [(${field.key})] = #{map.[(${field.key})]}",
            "</when>",
            [/]

            "<when test='map.start_time != null'>",
            "and update_time &gt;= #{map.start_time}",
            "</when>",
            "<when test='map.end_time != null'>",
            "and update_time &lt; #{map.end_time}",
            "</when>",
            "</script>"})
    List<Map> findCustom(@Param("map") Map map);

    @Select("select * from [(${tableName})] ")
    List<Map> findAll();

    @Select("select * from [(${tableName})] where id = #{id} ")
    Map findById(@Param("id") String id);

    @Insert("insert into [(${tableName})] (  name ) values ( #{map.name}) ")
    int insert(@Param("map") Map map);

    @Update({"<script>",
            "update [(${tableName})] set",
            "<when test='map.name != null'>",
            "name = #{map.name} ,",
            "</when>",
            "status = status",
            "where id = #{map.id}",
            "</script>"})
    int update(@Param("map") Map map);

    @Update("update [(${tableName})] set  name = #{map.name}" +
            " where id = #{map.id} ")
    int updateAll(@Param("map") Map map);

    @Update("update [(${tableName})] set status = #{map.status} where id = #{map.id} ")
    int updateStatus(@Param("map") Map map);


}
