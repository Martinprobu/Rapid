package [(${packagePath})]/model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;


/**
 * @author: [(${author})]
 * @desc: [(${tableComment})]
 * @version: [(${version})]
 **/
public class [(${className})] {

    @Id
    private Long id;

    [# th:each="field : ${tableFileds}"]
    /** [(${field.extra})]  */
    @Field("[(${field.key})]")
    private [(${field.type})] [(${field.field})];

    [/]
    /**
     * 默认:0可用， 1不可用，-1已删除, -2旧版本
     */
    @Field("status")
    private Integer status;


    /**
     * 创建时间
     */
    @Field("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Field("update_time")
    private Date updateTime;
}
