package [(${packagePath})].web.controller;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import [(${packagePath})].service.impl.[(${className})]ServiceImpl;

/**
 * <pre>
 *     [(${className})]
 *
 *     https://github.com/Martinprobu/Rapid
 *
 *     生成的<pre>标签可用于生成swagger等文档
 *
 * </pre>
 * @author [(${author})]
 * @since
 * @version [(${version})]
 */


@RestController
@RequestMapping("/[(${objectName})]")

public class [(${className})]Controller extends BaseController {

    @Resource
    private [(${className})]ServiceImpl [(${objectName})]Service;

    /**
     * <pre>
     *     根据页面条件分页列表
     *
     *     demo: http://127.0.0.1:8091/dc/[(${objectName})]/list
     *
     *     request:
     *
     *     data = {"pageNum":"1", "pageSize":"20", "user_id":"101", "user_name":"测试员", "start_time":"2018-01-01", "end_time":"2021-01-01"}
     *
     *     objDemo : {"id":"","name":""}
     *
     *     response:
     *      total, pages,
     *      [{
     *         "id": "",  //
     *         "name": "",  //
     *     }, .. ]
     *
     * </pre>
     */
    @GetMapping("/list")
    public BaseResponse findListCustom(@RequestParam(required = false, defaultValue = "{}") String data) {
        Map map = MapUtil.parseQueryString2Map(data);
        return BaseResponse.build(ReturnCode.SUCCESS, [(${objectName})]Service.findByPageCustom(map));
    }

    /**
     * <pre>
     *     查看单个对象
     *
     *     demo: http://127.0.0.1:8091/dc/[(${objectName})]/detail
     *
     *     request: id=123
     *
     *     objDemo : {"id":"","name":""}
     *     response:
     *     {
     *         "id": "",  //
     *         "name": "",  //
     *     }
     *
     * </pre>
     */
    @GetMapping("/detail")
    public BaseResponse findById(String id) {
        return BaseResponse.build(ReturnCode.SUCCESS, [(${objectName})]Service.findById(id));
    }

    /**
     * <pre>
     *     添加单个对象
     *
     *     demo: http://127.0.0.1:8091/dc/[(${objectName})]/save
     *
     *     objDemo : {"id":"","name":""}
     *
     *     request:
     *     {
     *         "id": "",  //
     *         "name": "",  //
     *     }
     *
     *     response:
     *
     *
     * </pre>
     */
    @PostMapping("/save")
    public BaseResponse save(@RequestParam String data) {
        Map map = MapUtil.parseQueryString2Map(data);
        return BaseResponse.build(ReturnCode.SUCCESS, [(${objectName})]Service.save(map));
    }

    /**
     * <pre>
     *     更改对象的某字段
     *
     *     demo: http://127.0.0.1:8091/dc/[(${objectName})]/update
     *
     * </pre>
     */
    @PostMapping("/update")
    public BaseResponse update(@RequestParam String data) {
        Map map = MapUtil.parseQueryString2Map(data);
        return BaseResponse.build(ReturnCode.SUCCESS, [(${objectName})]Service.update(map));
    }

    /**
     * <pre>
     *     更改整个对象
     *
     *     demo: http://127.0.0.1:8091/dc/[(${objectName})]/updateAll
     *
     * </pre>
     */
    @PostMapping("/updateAll")
    public BaseResponse updateAll(@RequestParam String data) {
        Map map = MapUtil.parseQueryString2Map(data);
        return BaseResponse.build(ReturnCode.SUCCESS, [(${objectName})]Service.updateAll(map));
    }

    /**
     * <pre>
     *     更改单个对象状态
     *
     *     demo: http://127.0.0.1:8091/dc/[(${objectName})]/updateStatus
     *
     * </pre>
     */
    @PostMapping("/updateStatus")
    public BaseResponse updateStatus(@RequestParam String data) {
        Map map = MapUtil.parseQueryString2Map(data);
        return BaseResponse.build(ReturnCode.SUCCESS, [(${objectName})]Service.updateStatus(map));
    }

    /**
     * <pre>
     *     删除单个对象
     *
     *     demo: http://127.0.0.1:8091/dc/order/del
     *
     * </pre>
     */
    @PostMapping("/del")
    public BaseResponse delById(@RequestParam String data) {
        Map map = MapUtil.parseQueryString2Map(data);
        return BaseResponse.build(ReturnCode.SUCCESS, [(${objectName})]Service.del(map));
    }

    /**
     * <pre>
     *     批量添加对象
     *
     *     demo: http://127.0.0.1:8091/dc/[(${objectName})]/saveBatch
     *
     * </pre>
     */
    @PostMapping("/saveBatch")
    public BaseResponse saveBatch(@RequestParam String data) {
        List<Map<String, String>> list = MapUtil.parseQueryString2List(data);
        return BaseResponse.build(ReturnCode.SUCCESS, [(${objectName})]Service.saveBatch(list));
    }

}