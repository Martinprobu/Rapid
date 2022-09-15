package [(${packagePath})].service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * order
 * @author: [(${author})]
 * @verson: [(${version})]
 */
@Service("[(${objectName})]Service")
public class [(${className})]ServiceImpl {

    @Resource
    private [(${className})]Mapper [(${objectName})]Mapper;

    /**
     * 根据页面条件分页查询列表
     */
    public com.github.pagehelper.PageInfo<Map> findByPageCustom(Map map) {

        PageHelper.startPage(MapUtil.getPageInex(map), MapUtil.getPageSize(map));
        List<Map> list = [(${objectName})]Mapper.findCustom(map);

        List<Column> columnList = [(${objectName})]TableColumns.ORDER_TABLE;

        return common.dto.PageInfo.build(new PageInfo(list), columnList);
    }

    /**
     * 查找单个对象
     */
    public Map findById(String id) {
        return [(${objectName})]Mapper.findById(id);
    }

    /**
     * 添加单个对象
     */
    public int save(Map map) {
        return [(${objectName})]Mapper.insert(map);
    }

    /**
     * 更改整个对象
     */
    public int updateAll(Map map) {
        return [(${objectName})]Mapper.updateAll(map);
    }

    /**
     * 更改一个对象的某字段
     */
    public int update(Map map) {
        return [(${objectName})]Mapper.update(map);
    }

    /**
     * 更改单个状态
     */
    public int updateStatus(Map map) {
        return [(${objectName})]Mapper.updateStatus(map);
    }

    /**
     * 删除一个对象
     */
    public int del(Map map) {
        map.put("status", "-1");
        return [(${objectName})]Mapper.updateStatus(map);
    }
    /**
     * 添加单个对象
     */
    public int saveBatch(List<Map<String, String>> list) {
        int result = list.stream().mapToInt(map -> [(${objectName})]Mapper.insert(map)).sum();
        return result;
    }

}