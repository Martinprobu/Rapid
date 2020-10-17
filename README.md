/**
 * <pre>
 * 生成精简的层代码，batis层以Map交互，铲掉pojo，保留少量dto
 * 目前仅支持MySql
 *
 * 基本配置:
 * PACKAGEPATH = "com.xx.yy";                       //基本的包名路径
 * SERVERPATH = "http://127.0.0.1:8080/api";        //接口服务器路径, 填充api接口的文档
 * JDBCPATH = "jdbc:mysql://127.0.0.1:3306/db_dev"; //jdbc
 * JDBCUSERNAME = "root";                           //jdbc
 * JDBCPSWORD = "123456";                           //jdbc
 * JDBCCLASS = "com.mysql.jdbc.Driver";             //jdbc
 * JDBCPORT = "3306";                               //jdbc
 * AUTHOR = "Bill";                                 //作者
 * VERSION = "1.0";                                 //版本
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
 * 		<groupId>mysql</groupId>
 * 		<artifactId>mysql-connector-java</artifactId>
 * 		<version>5.1.18</version>
 * </dependency>
 * <dependency>
 *      <groupId>org.springframework</groupId>
 *      <artifactId>spring-jdbc</artifactId>
 *      <version>5.1.5.RELEASE</version>
 * </dependency>
 *
 *
 * 下个版本TODO:
 *  生成Vue前端代码
 *  支持Oracle
 *
 *
 *
 *   添加swagger_doc支持
 *
 *   1.依懒以下pom, 本地jar
 *   <!-- SwaggerDoc 依赖-->
 *      <dependency>
 *          <groupId>com.gitee.largerfish</groupId>
 *          <artifactId>swagger-doc-starter</artifactId>
 *          <version>1.4-SNAPSHOT</version>
 *          <scope>system</scope>
 *          <systemPath>${project.basedir}/libs/swagger-doc-starter.jar</systemPath>
 *          <!--去除不必要的日志依赖,解决日志冲突-->
 *          <exclusions>
 *              <exclusion>
 *                  <groupId>org.slf4j</groupId>
 *                  <artifactId>slf4j-log4j12</artifactId>
 *              </exclusion>
 *              <exclusion>
 *                  <groupId>log4j</groupId>
 *                  <artifactId>log4j</artifactId>
 *              </exclusion>
 *          </exclusions>
 *      </dependency>
 *
 *   2.添加swagger_doc配置，则就会自动通过javadoc生成对应的swaggerdoc
 *
 *  @Configuration
 *@EnableSwaggerDoc
 *public class SwaggerDocConfig {
 *
 *  @Bean
 *  public FilterRegistrationBean logFilterRegistrationBean() {
 *      FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
 *      filterRegistrationBean.setFilter(new FilterInterceptor());
 *      filterRegistrationBean.setName("logFilterRegistrationBean");
 *      filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
 *      filterRegistrationBean.setOrder(1);
 *      return filterRegistrationBean;
 *  }
 *
 *  @Bean
 *  public SwaggerDoc swaggerDoc() {
 *      Contact contact = new Contact();
 *      Info info = new Info();
 *      info.setTitle("供应商管理客户端接口文档");
 *      contact.setEmail("linwl5@gdii-yueyun.com");
 *      contact.setName("linwl");
 *      contact.setUrl("");
 *      info.setDescription("供应商管理客户端接口");
 *      info.setContact(contact);
 *      return new SwaggerDoc.SwaggerDocBuilder().addSkipAnnotations(SessionAttribute.class).withDoc("doc")
 *               .withDoc("供应商管理客户端接口文档").withInfo(info).withHost("127.0.0.1:8091/dc").addIgnoreControllers("swaggerController", "basicErrorController").build();
 *  }
 * }
 *
 *
 * </pre>
 *
 * @author Bill
 * @since 2020-05-05
 * @version 1.0
 */
