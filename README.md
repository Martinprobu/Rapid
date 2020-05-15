/**
 * <pre>
 * 生成精简的层代码，batis层以Map交互，铲掉pojo，保留少量dto
 *
 *
 * 基本配置:
 * packagePath = com.xxx.yyy
 * domainName = order
 * serverPath = http://127.0.0.1:8080/be
 * jdbcPath =
 *
 * 生成以下层:
 * com.xxx.yyy.web.controller.OrderController
 * com.xxx.yyy.service.impl.OrderService
 * com.xxx.yyy.dao.mapper.custom.OrderMapper
 * com.xxx.yyy.commons.consts.TableColumns  <可忽略>
 *
 * 依赖(也可忽略):
 * com.xxx.yyy.commons.models.dto.table.ChildrenColumn
 * com.xxx.yyy.commons.models.dto.table.Column
 * com.xxx.yyy.commons.models.dto.PageInfo<T>
 * com.xxx.yyy.commons.models.dto.ResponseData
 * com.xxx.yyy.commons.utils.MapUtils
 * com.xxx.yyy.commons.enums.ReturnCode
 * com.xxx.yyy.commons.consts.TableColumns
 * 以及PageHelper插件
 *
 *
 * <<会生成于当前目录下，生成后开发者需要手工copy需要的文件于对应的目录下面。>>
 *
 * 依懒以下pom
 * <dependency>
 * 			<groupId>mysql</groupId>
 * 			<artifactId>mysql-connector-java</artifactId>
 * 			<version>5.1.18</version>
 * 		</dependency>
 * 	<dependency>
 * 			<groupId>commons-io</groupId>
 * 			<artifactId>commons-io</artifactId>
 * 			<version>1.4</version>
 * 		</dependency>
 * 	<dependency>
 * 			<groupId>org.codehaus.jackson</groupId>
 * 			<artifactId>jackson-core-asl</artifactId>
 * 			<version>1.9.2</version>
 * 		</dependency>
 * 	<dependency>
 * 			<groupId>org.codehaus.jackson</groupId>
 * 			<artifactId>jackson-mapper-asl</artifactId>
 * 			<version>1.9.2</version>
 * 		</dependency>
 * </pre>
 *
 * @author Bill
 * @since 2020-05-05
 * @version 1.0
 */
