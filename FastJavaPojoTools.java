package tools;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
 * </pre>
 *
 * @author Bill
 * @since 2020-05-05
 * @version 1.0
 */
public class FastJavaPojoTools {

    //配置
    public static final String PACKAGEPATH = "com.yy.xx";                 //基本的包名路径
    public static final String SERVERPATH = "http://127.0.0.1:8080/api";     //接口服务器路径, 填充api接口的文档
    public static final String JDBCPATH = "jdbc:mysql://47.106.221.251:3306/process_dev";     //jdbc
//    public static final String JDBCPATH = "jdbc:mysql://10.18.69.231:3306/process_dev";       //jdbc
    public static final String JDBCUSERNAME = "root";                       //jdbc
//    public static final String JDBCUSERNAME = "process_dev";                //jdbc
    public static final String JDBCPSWORD = "Miyin#root123";                //jdbc
//    public static final String JDBCPSWORD = "Azxs1234##";                   //jdbc
    public static final String JDBCCLASS = "com.mysql.jdbc.Driver";         //jdbc
    public static final String JDBCPORT = "3306";                           //jdbc
    public static final String AUTHOR = "Bill";                             //作者
    public static final String VERSION = "1.0";                             //版本

    //以下配置基本可以不变
    public static final String CONTROLLER_PACKAGE = "/web/controller";      //controller层package
    public static final String CONTROLLER_SUFFIX = "Controller.java";       //controller层类后缀, //以下均是相同作用
    public static final String SERVICE_PACKAGE = "/service/impl";
    public static final String SERVICE_SUFFIX = "ServiceImpl.java";
    public static final String MAPPER_PACKAGE = "/dao/mapper/custom";
    public static final String MAPPER_SUFFIX = "Mapper.java";
    public static final String COLUMNS_PACKAGE = "/commons/consts/tables";
    public static final String COLUMNS_SUFFIX = "TableColumns.java";


    public static void main(String[] args) {

        System.out.println("FastJavaTools Start ======================================================== ");
        CodeGen code = new CodeGen(PACKAGEPATH, SERVERPATH,
                                    JDBCPATH,
                                    AUTHOR, VERSION);

        List<String> list = code.readTables();
        System.out.println("Please choose which table need to gen the code, number or table_name is allowable.");
        for(int i = 0; i < list.size(); i++) {
            System.out.println("Table_id: " + i + "\tTable_name: " + list.get(i));
        }
        Scanner input = new Scanner(System.in);

        while(true) {
            System.out.println("Please Enter the talbe name or table id, type \"ALL\" will gen all the tables above.");
            String command = input.nextLine();

            if("all".equals(command.toLowerCase())) {
                for(int i = 0; i < list.size(); i++) {
                    code.readTable(list.get(i));
                }
                break;
            } else {
                try {
                    code.readTable(command);    //只支持输入表名
                    break;
                } catch (Exception e) {
//                    code.readTable(command);
                    e.printStackTrace();
                    break;
                }
            }
        }

        System.out.println("FastJavaTools End =========================================================== ");

    }


    /**
     * 代码生成主类
     */
    public static class CodeGen {

        private String packagePath;
        private String serverPath;
        private String jdbcPath;
        private String author;
        private String version;

        private String basePath = getBasePath();
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        private String dateTime = sdf.format(new Date());
        private CodeGenService service = new CodeGenService();
        private String tableComment;                        //表描述
        private String tableName;
        private List<CodeGenService.TableDesc> tableFileds; //表字段描述
        private String className;
        private String objectName;
        private String domainName;

        /**
         * 构造传参
         */
        public CodeGen() { }
        public CodeGen(String packagePath, String serverPath,
                       String jdbcPath,
                       String author, String version) {
            this.packagePath = packagePath;
            this.serverPath = serverPath;
            this.jdbcPath = jdbcPath;
            this.author = author;
            this.version = version;
        }

        /**
         * 根据某个表生成pojo对象及curd逻辑代码
         */
        public void readTable(String tableName) {
            this.tableComment = service.descTableComment(this.jdbcPath.substring(this.jdbcPath.lastIndexOf("/") + 1), tableName);
            this.tableFileds = service.descTable(tableName);
            this.className = nameCovert_(tableName, true);      //类名字, 首字母大写
            this.objectName = nameCovert_(tableName, false);    //对象名字, 首字母小写
            this.domainName = nameDomainCovert(tableName);                //领域名字, 就是类名的简写，用于放url,api生成等
            this.tableName = tableName.toLowerCase();
//            genPojoObj(table, list);
//            genDaoObj(table);
//            genDaoObjXml(table, list);

            System.out.println("FastJavaTools Generate Controller ========================================== ");
            genController(CONTROLLER_PACKAGE, CONTROLLER_SUFFIX);

            System.out.println("FastJavaTools Generate Service ============================================= ");
            genService(SERVICE_PACKAGE, SERVICE_SUFFIX);

            System.out.println("FastJavaTools Generate Mapper ============================================== ");
            genMapper(MAPPER_PACKAGE, MAPPER_SUFFIX);

            System.out.println("FastJavaTools Generate TableColumns ======================================== ");
            genColumn(COLUMNS_PACKAGE, COLUMNS_SUFFIX);

        }


        /**
         * 生成Controller 代码
         */
        public void genController(String subPath, String suffix) {
            StringBuilder sb = new StringBuilder();
            String myPackagePath = this.packagePath + subPath.replace("/", ".");
            sb.append("package " + myPackagePath + ";");
            sb.append("\n");
            sb.append("\n");
            sb.append("import org.springframework.web.bind.annotation.*;");
            sb.append("\n");
            sb.append("import io.swagger.annotations.Api;");
            sb.append("\n");
            sb.append("import io.swagger.annotations.ApiOperation;");
            sb.append("\n");
            sb.append("import javax.annotation.Resource;");
            sb.append("\n");
            sb.append("import java.util.List;");
            sb.append("\n");
            sb.append("import java.util.Map;");
            sb.append("\n");
            sb.append("import " + this.packagePath + ".commons.enums.ReturnCode;");
            sb.append("\n");
            sb.append("import " + this.packagePath + ".commons.models.dto.ResponseData;");
            sb.append("\n");
            sb.append("import " + this.packagePath + ".commons.utils.MapUtils;");
            sb.append("\n");
            sb.append("import " + this.packagePath + ".service.impl." + this.className + "ServiceImpl;");
            sb.append("\n");
            sb.append("\n");

            sb.append("/**\n");
            sb.append(" * <pre>\n");
            sb.append(" *     " + this.tableComment + "\n");
            sb.append(" *\n");
            sb.append(" *     Controller,ServiceImpl,Mapper均是工具生成 直接用Map交互 (兼顾db字段的改动, 没有pojo了, 只有少量dto)\n");
            sb.append(" *     https://github.com/BillWuSQL/FastJavaTools.git\n");
            sb.append(" *\n");
            sb.append(" *     生成的<pre>标签可用于生成swagger等文档\n");
            sb.append(" *\n");
            sb.append(" * </pre>\n");
            sb.append(" * @author " + this.author + "\n");
            sb.append(" * @since " + this.dateTime + "\n");
            sb.append(" * @version " + this.version + "\n");
            sb.append(" */\n");
            sb.append("\n");
            sb.append("\n");

            sb.append("@Api(tags = \"" + this.tableComment + "\")\n");
            sb.append("@RestController\n");
            sb.append("@RequestMapping(\"/" + domainName + "\")");
            sb.append("\n");
            sb.append("\n");
            sb.append("public class " + this.className + "Controller extends BaseController {");
            sb.append("\n");
            sb.append("\n");
            sb.append("\t@Resource\n");
            sb.append("\tprivate " + this.className + "ServiceImpl " + this.objectName + "Service;");
            sb.append("\n");
            sb.append("\n");

            //list方法-pre风格注释
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     根据页面条件分页列表\n");
            sb.append("\t*\n");
            sb.append("\t*     demo: " + this.serverPath + "/" + this.domainName + "/list\n");
            sb.append("\t*\n");
            sb.append("\t*     request:\n");
            sb.append("\t*\n");
            sb.append("\t*     data = {\"pageNum\":\"1\", \"pageSize\":\"20\", \"user_id\":\"101\", \"user_name\":\"测试员\", \"start_time\":\"2018-01-01\", \"end_time\":\"2021-01-01\"}\n");
            sb.append("\t*\n");
            sb.append("\t*     objDemo : {");
            StringBuilder sb2 = new StringBuilder();
            this.tableFileds.forEach( col -> {
                sb2.append(",\"" + col.getField() + "\":\"\"");
            });
            sb.append(sb2.toString().substring(1));
            sb2.setLength(0);
            sb.append("}\n");
            sb.append("\t*\n");
            sb.append("\t*     response:\n");
            sb.append("\t*      total, pages,\n");
            sb.append("\t*      [{\n");
            //迭代表字段
            this.tableFileds.forEach( col -> {
                sb.append("\t*         \"" + col.getField() + "\": \"\",  //" + col.getExtra() + "\n");
            });
            sb.append("\t*     }, .. ]\n");
            sb.append("\t*\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            //list方法-代码
            sb.append("\t@GetMapping(\"/list\")\n");
            sb.append("\t@ApiOperation(value = \"根据页面条件分页列表\")\n");
            sb.append("\tpublic ResponseData findListCustom(@RequestParam(required = false, defaultValue = \"{}\") String data) {\n");
            sb.append("\t\tMap map = MapUtils.parseQueryString2Map(data);\n");
            sb.append("\t\treturn ResponseData.build(ReturnCode.SUCCESS, " + objectName + "Service.findByPageCustom(map));\n");
            sb.append("\t}\n");
            sb.append("\n");

            //get方法-注释
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     查看单个对象\n");
            sb.append("\t*\n");
            sb.append("\t*     demo: " + this.serverPath + "/" + this.domainName + "/get\n");
            sb.append("\t*\n");
            sb.append("\t*     request: id=123\n");
            sb.append("\t*\n");
            sb.append("\t*     objDemo : {");
            this.tableFileds.forEach( col -> {
                sb2.append(",\"" + col.getField() + "\":\"\"");
            });
            sb.append(sb2.toString().substring(1));
            sb2.setLength(0);
            sb.append("}\n");
            sb.append("\t*     response:\n");
            sb.append("\t*     {\n");
            //迭代表字段
            this.tableFileds.forEach( col -> {
                sb.append("\t*         \"" + col.getField() + "\": \"\",  //" + col.getExtra() + "\n");
            });
            sb.append("\t*     }\n");
            sb.append("\t*\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            //get方法-代码
            sb.append("\t@GetMapping(\"/get\")\n");
            sb.append("\t@ApiOperation(value = \"查看单个对象\")\n");
            sb.append("\tpublic ResponseData findById(String id) {\n");
            sb.append("\t\treturn ResponseData.build(ReturnCode.SUCCESS, " + objectName +  "Service.findById(id));\n");
            sb.append("\t}\n");
            sb.append("\n");

            //添加单个对象方法-注释
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     添加单个对象\n");
            sb.append("\t*\n");
            sb.append("\t*     demo: " + this.serverPath + "/" + this.domainName + "/save\n");
            sb.append("\t*\n");
            sb.append("\t*     objDemo : {");
            this.tableFileds.forEach( col -> {
                sb2.append(",\"" + col.getField() + "\":\"\"");
            });
            sb.append(sb2.toString().substring(1));
            sb2.setLength(0);
            sb.append("}\n");
            sb.append("\t*\n");
            sb.append("\t*     request:\n");
            sb.append("\t*     {\n");
            //迭代表字段
            this.tableFileds.forEach( aa -> {
                sb.append("\t*         \"" + aa.getField() + "\": \"\",  //" + aa.getExtra() + "\n");
            });
            sb.append("\t*     }\n");
            sb.append("\t*\n");
            sb.append("\t*     response:\n");
            sb.append("\t*\n");
            sb.append("\t*\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            //添加单个对象方法-代码
            sb.append("\t@PostMapping(\"/save\")\n");
            sb.append("\t@ApiOperation(value = \"添加单个对象\")\n");
            sb.append("\tpublic ResponseData save(@RequestParam String data) {\n");
            sb.append("\t\tMap map = MapUtils.parseQueryString2Map(data);\n");
            sb.append("\t\treturn ResponseData.build(ReturnCode.SUCCESS, " + objectName + "Service.save(map));\n");
            sb.append("\t}\n");
            sb.append("\n");

            //更改对象的某字段-注释
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     更改对象的某字段\n");
            sb.append("\t*\n");
            sb.append("\t*     demo: " + this.serverPath + "/" + this.domainName + "/update\n");
            sb.append("\t*\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            //更改对象的某字段-代码
            sb.append("\t@PostMapping(\"/update\")\n");
            sb.append("\t@ApiOperation(value = \"更改对象的某字段\")\n");
            sb.append("\tpublic ResponseData update(@RequestParam String data) {\n");
            sb.append("\t\tMap map = MapUtils.parseQueryString2Map(data);\n");
            sb.append("\t\treturn ResponseData.build(ReturnCode.SUCCESS, " + objectName + "Service.update(map));\n");
            sb.append("\t}\n");
            sb.append("\n");

            //更改整个对象-注释
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     更改整个对象\n");
            sb.append("\t*\n");
            sb.append("\t*     demo: " + this.serverPath + "/" + this.domainName + "/updateAll\n");
            sb.append("\t*\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            //更改整个对象-代码
            sb.append("\t@PostMapping(\"/updateAll\")\n");
            sb.append("\t@ApiOperation(value = \"更改整个对象\")\n");
            sb.append("\tpublic ResponseData updateAll(@RequestParam String data) {\n");
            sb.append("\t\tMap map = MapUtils.parseQueryString2Map(data);\n");
            sb.append("\t\treturn ResponseData.build(ReturnCode.SUCCESS, " + objectName + "Service.updateAll(map));\n");
            sb.append("\t}\n");
            sb.append("\n");

            //更改单个对象状态-注释
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     更改单个对象状态\n");
            sb.append("\t*\n");
            sb.append("\t*     demo: " + this.serverPath + "/" + this.domainName + "/updateStatus\n");
            sb.append("\t*\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            //更改单个对象状态-代码
            sb.append("\t@PostMapping(\"/updateStatus\")\n");
            sb.append("\t@ApiOperation(value = \"更改单个对象状态\")\n");
            sb.append("\tpublic ResponseData updateStatus(@RequestParam String data) {\n");
            sb.append("\t\tMap map = MapUtils.parseQueryString2Map(data);\n");
            sb.append("\t\treturn ResponseData.build(ReturnCode.SUCCESS, " + objectName + "Service.updateStatus(map));\n");
            sb.append("\t}\n");
            sb.append("\n");

            //删除单个对象-注释
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     删除单个对象\n");
            sb.append("\t*\n");
            sb.append("\t*     demo: " + this.serverPath + "/" + this.domainName + "/del\n");
            sb.append("\t*\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            //删除单个对象-代码
            sb.append("\t@PostMapping(\"/del\")\n");
            sb.append("\t@ApiOperation(value = \"删除单个对象\")\n");
            sb.append("\tpublic ResponseData delById(@RequestParam String data) {\n");
            sb.append("\t\tMap map = MapUtils.parseQueryString2Map(data);\n");
            sb.append("\t\treturn ResponseData.build(ReturnCode.SUCCESS, " + objectName + "Service.del(map));\n");
            sb.append("\t}\n");
            sb.append("\n");

            //批量添加对象-注释
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     批量添加对象\n");
            sb.append("\t*\n");
            sb.append("\t*     demo: " + this.serverPath + "/" + this.domainName + "/saveBatch\n");
            sb.append("\t*\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            //批量添加对象-代码
            sb.append("\t@PostMapping(\"/saveBatch\")\n");
            sb.append("\t@ApiOperation(value = \"批量添加对象\")\n");
            sb.append("\tpublic ResponseData saveBatch(@RequestParam String data) {\n");
            sb.append("\t\tList<Map<String, String>> list = MapUtils.parseQueryString2List(data);\n");
            sb.append("\t\treturn ResponseData.build(ReturnCode.SUCCESS, " + objectName + "Service.saveBatch(map));\n");
            sb.append("\t}\n");
            sb.append("\n");
            sb.append("}");

            genFile(sb.toString(), subPath, suffix);

        }

        /**
         * 生成Service 代码
         */
        public void genService(String subPath, String suffix) {
            StringBuilder sb = new StringBuilder();
            String myPackagePath = this.packagePath + subPath.replace("/", ".");

            sb.append("package " + myPackagePath + ";\n");
            sb.append("\n");
            sb.append("import com.github.pagehelper.PageHelper;\n");
            sb.append("import com.github.pagehelper.PageInfo;\n");
            sb.append("import " + this.packagePath + ".commons.consts.TableColumns;\n");
            sb.append("import " + this.packagePath + ".commons.models.dto.table.Column;\n");
            sb.append("import " + this.packagePath + ".commons.utils.MapUtils;\n");
            sb.append("import " + this.packagePath + ".dao.mapper.customer." + this.className + "Mapper;\n");
            sb.append("import org.springframework.stereotype.Service;\n");
            sb.append("import javax.annotation.Resource;\n");
            sb.append("import java.util.List;\n");
            sb.append("import java.util.Map;\n");
            sb.append("\n");

            sb.append("/**\n");
            sb.append("* " + this.tableComment + "\n");
            sb.append("* @Author: " + this.author + "\n");
            sb.append("* @Date: " + this.dateTime + "\n");
            sb.append("*/\n");
            sb.append("@Service(\"" + this.objectName + "Service\")\n");
            sb.append("public class " + this.className + "ServiceImpl {\n");
            sb.append("\n");
            sb.append("\t@Resource\n");
            sb.append("\tprivate " + this.className + "Mapper " + this.objectName + "Mapper;\n");
            sb.append("\n");

            sb.append("\t/**\n");
            sb.append("\t* 根据页面条件分页查询列表\n");
            sb.append("\t*/\n");
            sb.append("\tpublic com.github.pagehelper.PageInfo<Map> findByPageCustom(Map map) {\n");
            sb.append("\t\n");
            sb.append("\t\tPageHelper.startPage(MapUtils.getPageInex(map), MapUtils.getPageSize(map));\n");
            sb.append("\t\tList<Map> list = " + this.objectName + "Mapper.findCustom(map);\n");
            sb.append("\t\n");
            sb.append("\t\tList<Column> columnList = TableColumns." + this.tableName.toUpperCase() + "_TABLE;\n");
            sb.append("\t\n");
            sb.append("\t\treturn " + this.packagePath + ".commons.models.dto.PageInfo.build(new PageInfo(list), columnList);\n");
            sb.append("\t}\n");
            sb.append("\n");

            sb.append("\t/**\n");
            sb.append("\t* 查找单个对象\n");
            sb.append("\t*/\n");
            sb.append("\tpublic Map findById(String id) {\n");
            sb.append("\t\treturn " + this.objectName + "Mapper.findById(id);\n");
            sb.append("\t}\n");
            sb.append("\n");
            sb.append("\t/**\n");
            sb.append("\t* 添加单个对象\n");
            sb.append("\t*/\n");
            sb.append("\tpublic int save(Map map) {\n");
            sb.append("\t\treturn " + this.objectName + "Mapper.insert(map);\n");
            sb.append("\t}\n");
            sb.append("\n");
            sb.append("\t/**\n");
            sb.append("\t* 更改整个对象\n");
            sb.append("\t*/\n");
            sb.append("\tpublic int updateAll(Map map) {\n");
            sb.append("\t\treturn " + this.objectName + "Mapper.updateAll(map);\n");
            sb.append("\t}\n");
            sb.append("\n");
            sb.append("\t/**\n");
            sb.append("\t* 更改一个对象的某字段\n");
            sb.append("\t*/\n");
            sb.append("\tpublic int update(Map map) {\n");
            sb.append("\t\treturn " + this.objectName + "Mapper.update(map);\n");
            sb.append("\t}\n");
            sb.append("\n");
            sb.append("\t/**\n");
            sb.append("\t* 更改单个状态\n");
            sb.append("\t*/\n");
            sb.append("\tpublic int updateStatus(Map map) {\n");
            sb.append("\t\treturn " + this.objectName + "Mapper.updateStatus(map);\n");
            sb.append("\t}\n");
            sb.append("\n");

            sb.append("\t/**\n");
            sb.append("\t* 删除一个对象\n");
            sb.append("\t*/\n");
            sb.append("\tpublic int del(Map map) {\n");
            sb.append("\t\tmap.put(\"status\", \"-1\");\n");
            sb.append("\t\treturn " + this.objectName + "Mapper.updateStatus(map);\n");
            sb.append("\t}\n");
            sb.append("\t/**\n");
            sb.append("\t* 添加单个对象\n");
            sb.append("\t*/\n");
            sb.append("\tpublic int saveBatch(List<Map<String, String>> list) {\n");
            sb.append("\t\tint result = list.stream().mapToInt(map -> " + this.objectName + "Mapper.insert(map)).sum();\n");
            sb.append("\t\treturn result;\n");
            sb.append("\t}\n");
            sb.append("\n");
            sb.append("}");

            genFile(sb.toString(), subPath, suffix);

        }

        /**
         * 生成Mapper 代码
         */
        public void genMapper(String subPath, String suffix) {
            StringBuilder sb = new StringBuilder();
            String myPackagePath = this.packagePath + subPath.replace("/", ".");
            sb.append("package " + myPackagePath + ";\n");
            sb.append("\n");
            sb.append("import org.apache.ibatis.annotations.*;\n");
            sb.append("import java.util.List;\n");
            sb.append("import java.util.Map;\n");
            sb.append("\n");
            sb.append("/**\n");
            sb.append("* <pre>\n");
            sb.append("*     操作日志相关\n");
            sb.append("*     为了兼容db字段的增删改, 传参用的Map\n");
            sb.append("* </pre>\n");
            sb.append("* @author " + this.author + "\n");
            sb.append("*/\n");
            sb.append("@Mapper\n");
            sb.append("public interface " + this.className + "Mapper {\n");
            sb.append("\n");
            sb.append("\t@Select({\"<script>\",\n");
            sb.append("\t\t\"select * from " + this.tableName + "\",\n");
            sb.append("\t\t\"where status != -1\",\n");
            //迭代数据字段
            this.tableFileds.forEach( aa -> {
                sb.append("\t\t\"<when test='map." + aa.getField() + " != null'>\",\n");
                sb.append("\t\t\"and " + aa.getField() + " = #{map." + aa.getField() + "}\",\n");
                sb.append("\t\t\"</when>\",\n");
            });
            sb.append("\t\t\"<when test='map.start_time != null'>\",\n");
            sb.append("\t\t\"and update_time &gt;= #{map.start_time}\",\n");
            sb.append("\t\t\"</when>\",\n");
            sb.append("\t\t\"<when test='map.end_time != null'>\",\n");
            sb.append("\t\t\"and update_time &lt; #{map.end_time}\",\n");
            sb.append("\t\t\"</when>\",\n");
            sb.append("\t\t\"</script>\"})\n");
            sb.append("\tList<Map> findCustom(@Param(\"map\") Map map);\n");
            sb.append("\t\n");
            sb.append("\t@Select(\"select * from " + this.tableName + " \")\n");
            sb.append("\tList<Map> findAll();\n");
            sb.append("\t\n");
            sb.append("\t@Select(\"select * from " + this.tableName + " where id = #{id} \")\n");
            sb.append("\tMap findById(@Param(\"id\") String id);\n");
            sb.append("\t\n");
            sb.append("\t@Insert(\"insert into " + this.tableName + " ( ");
            StringBuilder sbInsert = new StringBuilder();
            StringBuilder finalSbInsert = sbInsert;
            this.tableFileds.forEach(aa -> {
                if ("id".equals(aa.getField())
                        || "update_time".equals(aa.getField())) {  //插入时忽略id, 一般是自增
                    return;
                }
                finalSbInsert.append(", " + aa.getField());
            });
            sb.append(finalSbInsert.toString().substring(finalSbInsert.toString().indexOf(",") + 1)); //把第一个逗号干掉
            finalSbInsert.setLength(0);

            sb.append(" ) values (");
            this.tableFileds.forEach( aa -> {
                if ("id".equals(aa.getField())
                        || "update_time".equals(aa.getField())) {  //插入时忽略id, 一般是自增
                    return;
                }
                finalSbInsert.append(", #{map." + aa.getField() + "}");
            });
            sb.append(finalSbInsert.toString().substring(finalSbInsert.toString().indexOf(",") + 1)); //把第一个逗号干掉
            finalSbInsert.setLength(0);

            sb.append(") \")\n");
            sb.append("\tint insert(@Param(\"map\") Map map);\n");
            sb.append("\t\n");
            sb.append("\t@Update({\"<script>\",\n");
            sb.append("\t\"update " + this.tableName + " set\",\n");
            this.tableFileds.forEach( aa -> {
                if ("id".equals(aa.getField())
                        || "update_time".equals(aa.getField())) {  //插入时忽略id, 一般是自增
                    return;
                }
                sb.append("\t\t\"<when test='map." + aa.getField() + " != null'>\",\n");
                sb.append("\t\t\"" + aa.getField() + " = #{map." + aa.getField() + "} ,\",\n");
                sb.append("\t\t\"</when>\",\n");
            });
            sb.append("\t\t\"status = status\",\n");
            sb.append("\t\t\"where id = #{map.id}\",\n");
            sb.append("\t\t\"</script>\"})\n");
            sb.append("\tint update(@Param(\"map\") Map map);\n");
            sb.append("\t\n");
            sb.append("\t@Update(\"update " + this.tableName + " set ");
            this.tableFileds.forEach( aa -> {
                if ("id".equals(aa.getField())
                        || "update_time".equals(aa.getField())) {  //插入时忽略id, 一般是自增
                    return;
                }
                finalSbInsert.append(", " + aa.getField() + " = #{map." + aa.getField() + "}");
            });
            sb.append(finalSbInsert.toString().substring(finalSbInsert.toString().indexOf(",") + 1)); //把第一个逗号干掉
            finalSbInsert.setLength(0);

            sb.append("\" + \n");
            sb.append("\t\t\" where id = #{map.id} \")\n");
            sb.append("\tint updateAll(@Param(\"map\") Map map);\n");
            sb.append("\t\n");
            sb.append("\t@Update(\"update " + this.tableName + " set status = #{map.status} where id = #{map.id} \")\n");
            sb.append("\tint updateStatus(@Param(\"map\") Map map);\n");
            sb.append("\t\n");
            sb.append("\t\n");
            sb.append("}\n");

            genFile(sb.toString(), subPath, suffix);
        }

        /**
         * 生成Column 代码
         */
        public void genColumn(String subPath, String suffix) {
            StringBuilder sb = new StringBuilder();
            String myPackagePath = this.packagePath + subPath.replace("/", ".");
            sb.append("package " + myPackagePath + ";\n");
            sb.append("\n");
            sb.append("import com.google.common.collect.Lists;\n");
            sb.append("import " + this.packagePath + ".commons.models.dto.table.ChildrenColumn;\n");
            sb.append("import " + this.packagePath + ".commons.models.dto.table.Column;\n");
            sb.append("import java.util.List;\n");
            sb.append("\n");
            sb.append("/**\n");
            sb.append("* 表单项常量\n");
            sb.append("*\n");
            sb.append("* @author " + this.author + "\n");
            sb.append("*/\n");
            sb.append("public class " + this.className + "TableColumns {\n");
            sb.append("\n");
            sb.append("\t/**\n");
            sb.append("\t* <pre>\n");
            sb.append("\t*     " + this.tableComment + "列表表头\n");
            sb.append("\t*\n");
            sb.append("\t*      {\n");
            //迭代表字段
            this.tableFileds.forEach( col -> {
                sb.append("\t*         \"" + col.getField() + "\": \"" + "" + "\",\n");
            });
            sb.append("\t*     }\n");
            sb.append("\t* </pre>\n");
            sb.append("\t*/\n");
            sb.append("\tpublic static final List<Column> " + this.tableName.toUpperCase() + "_TABLE = Lists.newArrayList(\n");
            //迭代表字段
            this.tableFileds.forEach( col -> {
                if ("id".equals(col.getField())) {
                    sb.append("\t\tnew Column(\"" + col.getField() + "\", \"" + col.getField() + "\"),\n");
                    return;
                } else if ("update_time".equals(col.getField())) {
                    sb.append("\t\tnew Column(\"" + col.getField() + "\", \"" + "更新时间" + "\"),\n");
                    return;
                }
                String label = col.getExtra().trim();
                label = col.getExtra().length() > 10 ? col.getExtra().substring(0, 10) : col.getExtra();
                label = label.trim().replace(",", "").replace(":", "");
                sb.append("\t\tnew Column(\"" + col.getField() + "\", \"" + label + "\"),\n");
            });
            sb.append("\t);\n");
            sb.append("\n");
            sb.append("}\n");

            genFile(sb.toString(), subPath, suffix);
        }

        /**
         * 生成文件io
         * @param conent 文件里的代码内容
         * @param subPath 各层对应的中间目录
         * @param suffix 各层对应的文件后缀
         */
        private void genFile(String conent, String subPath, String suffix) {
            System.out.println(suffix + "  =========================== >>>>> " + conent.toString());
            try {
                File fileDirect = new File(this.basePath + File.separator + this.packagePath.replace(".", File.separator) + subPath);
                fileDirect.mkdirs();
                File file = new File(this.basePath + File.separator + this.packagePath.replace(".", File.separator) + subPath + File.separator  + className + suffix);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(conent.getBytes());
                fos.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 生成POJO对象
         * @deprecated
         */
        public void genPojoObj(String tableName, List<CodeGenService.TableDesc> list) {
            String className = nameCovert_(tableName, true);
            StringBuilder sb = new StringBuilder();
            String packagePath = this.packagePath + ".pojo";
            sb.append("package " + packagePath + ";");
            sb.append("\n");
            sb.append("\n");
            sb.append("import java.io.Serializable;");
            sb.append("\n");
            sb.append("import java.sql.Timestamp;");
            sb.append("\n");
            sb.append("import " + packagePath + ".BasePojo;");
            sb.append("\n");
            sb.append("\n");
            sb.append("public class " + className.toString() + " extends BasePojo implements Serializable {");
            sb.append("\n");
            sb.append("\n");
            sb.append("\t");
            sb.append("private static final long serialVersionUID = 1L;");
            sb.append("\n");

            for(int i=0; i<list.size(); i++) {
                sb.append("\t");
                sb.append("private " + typeContvert(list.get(i).getType()) + " " + nameCovert(list.get(i).getField(), false) + ";");
                sb.append("\n");
            }

            sb.append("\n");
            sb.append("\t");
            sb.append("public " + className + "(){}");
            sb.append("\n");
            sb.append("\n");

            for(int i=0; i<list.size(); i++) {
                sb.append("\t");
                sb.append("public " + typeContvert(list.get(i).getType()) + " get" + nameCovert(list.get(i).getField(), true) + "() {");
                sb.append("\n");
                sb.append("\t");sb.append("\t");
                sb.append("return " + nameCovert(list.get(i).getField(), false) + ";");
                sb.append("\n");
                sb.append("\t");
                sb.append("}");
                sb.append("\n");

                sb.append("\t");
                sb.append("public void set" + nameCovert(list.get(i).getField(), true) + "(" + typeContvert(list.get(i).getType()) + " " + nameCovert(list.get(i).getField(), false) + ") {");
                sb.append("\n");
                sb.append("\t");sb.append("\t");
                sb.append("this. " + nameCovert(list.get(i).getField(), false) + " = " + nameCovert(list.get(i).getField(), false) + ";");
                sb.append("\n");
                sb.append("\t");
                sb.append("}");
                sb.append("\n");
            }

            sb.append("\n");
            sb.append("}");

            System.out.println(sb.toString());


            try {
                File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
                fileDirect.mkdirs();
                File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator  + className + ".java");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 生成Dao接口
         * @deprecated
         */
        public void genDaoObj(String tableName) {
            String className = nameCovert(tableName, true);
            String objectName = nameCovert(tableName, false);
            StringBuilder sb = new StringBuilder();
            String packagePath = this.packagePath + ".dao.mapper";
            sb.append("package " + packagePath + ";");
            sb.append("\n");
            sb.append("\n");
            sb.append("import java.util.List;");
            sb.append("\n");
            sb.append("\n");
            sb.append("import " + this.packagePath + ".pojo" + "." + className + ";");
            sb.append("\n");
            sb.append("import java.util.List;");
            sb.append("\n");
            sb.append("import org.apache.ibatis.annotations.Param;");
            sb.append("\n");
            sb.append("\n");
            sb.append("public interface " + className + "Mapper {");
            sb.append("\n");
            sb.append("\n");


            sb.append("\t");
            sb.append("public int insert" + className + "(" + className + " " + objectName + ");");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("public int update" + className + "(" + className + " " + objectName + ");");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("public int remove" + className + "(" + className + " " + objectName + ");");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("public int remove" + className + "(Integer id);");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("public " + className + " get" +className+ "(Integer id);");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("public List<" + className + "> list" +className+ "();");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("public List<" + className + "> paging" +className+ "(" + className + " " + objectName + ");");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("public int list" + className + "Count();");
            sb.append("\n");
            sb.append("\n");

            sb.append("\n");
            sb.append("}");

            System.out.println(sb.toString());


            try {
                File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
                fileDirect.mkdirs();
                File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator  + className + "Mapper.java");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 生成Dao mybatis 映射文件
         * @param tableName
         * @deprecated
         */
        public void genDaoObjXml(String tableName, List<CodeGenService.TableDesc> list) {
            String className = nameCovert(tableName, true);
            String objectName = nameCovert(tableName, false);
            StringBuilder sb = new StringBuilder();
            String packagePath = this.packagePath + ".dao.mapper";
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            sb.append("\n");
            sb.append("<!DOCTYPE mapper");
            sb.append("\n");
            sb.append("\tPUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
            sb.append("\n");
            sb.append("\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            sb.append("\n");
            sb.append("<mapper namespace=\"" + packagePath + "." + className + "Mapper\">");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("<resultMap id=\"" +objectName+ "ResultMap\" type=\"" +this.packagePath+ ".pojo" + "." +className+ "\">");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("<id property=\"" +nameCovert(list.get(0).getField(), false)+ "\" column=\"" +list.get(0).getField()+ "\"/>");
            for(int i=1; i<list.size(); i++) {
                sb.append("\n");
                sb.append("\t");sb.append("\t");
                sb.append("<result property=\"" +nameCovert(list.get(i).getField(), false)+ "\" column=\"" +list.get(i).getField()+ "\"/>");
            }
            sb.append("\n");
            sb.append("\t");
            sb.append("</resultMap>");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("<insert id=\"insert" + className + "\">");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("insert into " + tableName + " (");
            for(int i=1; i<list.size(); i++) {
                sb.append("\n");
                sb.append("\t");sb.append("\t");sb.append("\t");
                sb.append(list.get(i).getField() + ",");
            }
            sb.replace(0, sb.length(), sb.substring(0, sb.length()-1));
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append(")");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("values (");
            for(int i=1; i<list.size(); i++) {
                sb.append("\n");
                sb.append("\t");sb.append("\t");sb.append("\t");
                sb.append("#{" + nameCovert(list.get(i).getField(), false) + "},");
            }
            sb.replace(0, sb.length(), sb.substring(0, sb.length()-1));
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append(")");
            //sb.append("insert into " + tableName + " values (); ");
            sb.append("\n");
            sb.append("\t");
            sb.append("</insert>");
            sb.append("\n");
            sb.append("\n");


            sb.append("\t");
            sb.append("<update id=\"update" + className + "\">");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("update ");
            sb.append("\n");
            sb.append("\t");sb.append("\t");sb.append("\t");
            sb.append(tableName);
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("set");
            for(int i=1; i<list.size(); i++) {
                sb.append("\n");
                sb.append("\t");sb.append("\t");sb.append("\t");
                sb.append(list.get(i).getField() + " = #{" + nameCovert(list.get(i).getField(), false) + "},");
            }
            sb.replace(0, sb.length(), sb.substring(0, sb.length()-1));
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("where");
            sb.append("\n");
            sb.append("\t");sb.append("\t");sb.append("\t");
            sb.append("id = #{id}");
            //sb.append("update " + tableName + " set xx=yy where id = #{id}");
            sb.append("\n");
            sb.append("\t");
            sb.append("</update>");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("<delete id=\"remove" + className + "\">");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("delete from " + tableName + " where id = #{id} ");
            sb.append("\n");
            sb.append("\t");
            sb.append("</delete>");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("<select id=\"get" + className + "\" resultMap=\"" +objectName+ "ResultMap\">");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("select * from " + tableName + " where id = #{id}");
            sb.append("\n");
            sb.append("\t");
            sb.append("</select>");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("<select id=\"list" + className + "\" resultMap=\"" +objectName+ "ResultMap\">");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("select * from " + tableName + "");
            sb.append("\n");
            sb.append("\t");
            sb.append("</select>");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("<select id=\"paging" + className + "\" resultMap=\"" +objectName+ "ResultMap\">");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("select * from " + tableName + " limit #{offset}, #{number}");
            sb.append("\n");
            sb.append("\t");
            sb.append("</select>");
            sb.append("\n");
            sb.append("\n");

            sb.append("\t");
            sb.append("<select id=\"list" + className + "Count\" resultType=\"int\">");
            sb.append("\n");
            sb.append("\t");sb.append("\t");
            sb.append("select count(1) from " + tableName + "");
            sb.append("\n");
            sb.append("\t");
            sb.append("</select>");
            sb.append("\n");
            sb.append("\n");

            sb.append("\n");
            sb.append("</mapper>");

            try {
                File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
                fileDirect.mkdirs();
                File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator  +className + "Mapper.xml");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }


        /**
         * 读取数据库中的所有表名
         */
        public List<String> readTables() {
            String sql = sqlConsts.showTables;
            List<String> list = service.showTables(sql);
            return list;
        }
        /**
         * 转成领域名，类名的简称, 多用于url, api编写
         */
        private String nameDomainCovert(String name) {
            String[] classNamePath = name.split("_");
            return classNamePath[classNamePath.length - 1].toLowerCase();
        }
        /**
         * 类或字段名转换 (按照map 数据库风格)
         */
        private String nameCovert(String name, boolean firstUpper) {
            return name.trim().toLowerCase();
        }
        /**
         * 类或字段名转换 (按照java 陀峰风格)
         */
        private String nameCovert_(String name, boolean firstUpper) {
            String[] classNamePath = name.split("_");
            StringBuffer className = new StringBuffer();
            int firstLetter = 0;
            for(int i = 0; i < classNamePath.length; i++) {
                firstLetter = Integer.valueOf(classNamePath[i].charAt(0));
                if(firstLetter >= 97 && firstLetter <= 122) {
                    char c;
                    if(i==0) {
                        if(firstUpper) {
                            c = (char) (firstLetter-32);
                        } else {
                            c = (char) firstLetter;
                        }
                    } else {
                        c = (char) (firstLetter-32);
                    }
                    className.append(String.valueOf(c) + classNamePath[i].substring(1));
                } else {
                    className.append(classNamePath[i]);
                }
            }
            return className.toString();
        }
        /**
         * 类型转换
         * @param type
         * @return
         */
        private String typeContvert(String type) {
            if(type.startsWith("int") || type.startsWith("tinyint") || type.startsWith("smallint") || type.startsWith("bit") || type.startsWith("mediumint")) {
                return "Integer";
            } else if(type.startsWith("bigint")) {
                return "Long";
            } else if(type.startsWith("float")) {
                return "Float";
            } else if(type.startsWith("double")) {
                return "Double";
            } else if(type.startsWith("char") || type.startsWith("varchar") || type.startsWith("text")) {
                return "String";
            } else if(type.startsWith("datetime") || type.startsWith("timestamp")) {
                return "Timestamp";
            }
            return type;
        }

        /**
         * 取得程序根目录
         */
        private String getBasePath() {
            String basePath = CodeGen.class.getResource("") + "";//
            if (basePath.startsWith("file:")) {
                if (basePath.charAt(7) == ':') { // Windows系统路径
                    basePath = basePath.substring(6);
                } else { // Unix系统路径
                    basePath = basePath.substring(5);
                }
            }
//            basePath = basePath.substring(0, basePath.indexOf("uniutil")+7);
            basePath = basePath + File.separator +  "target" + File.separator + "autoGen";
            System.out.println("Output : " + basePath);
            File file = new File(basePath);
            file.mkdirs();
            return basePath;
        }


        /**
         * sql
         */
        public interface sqlConsts {
            public final String showTables = "show tables";
        }

    }

    /**
     * 代码生成业务类
     */
    public static class CodeGenService {

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
            this.jdbcTemplate.execute(sql);
        }

        public int doUpdate(String sql) {
            return this.jdbcTemplate.update(sql);
        }

        /**
         * 列出所有表
         */
        public List showTables(String sql) {
            List<Map<String, Object>> resultList = this.jdbcTemplate.queryForList(sql);
            List<String> list = new ArrayList<String>();
            Map<String, Object> map = new HashMap<String, Object>();
            for(int i = 0; i < resultList.size(); i++) {
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
            List<Map<String, Object>> resultList = this.jdbcTemplate.queryForList("show full columns from `" + tableName + "`");
            Map<String, Object> map = new HashMap<String, Object>();
            TableDesc table;
            List<TableDesc> list = new ArrayList<CodeGenService.TableDesc>();
            for(int i = 0; i < resultList.size(); i++) {
                map = resultList.get(i);
                table = new TableDesc(map.get("Field").toString(), map.get("Type").toString(), map.get("Null").toString(), map.get("Key").toString(), String.valueOf(map.get("Default")), String.valueOf(map.get("Comment")));//map.get("Key").toString(), map.get("Default").toString(), map.get("Extra").toString());
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
            String comment = this.jdbcTemplate.queryForObject(sql, String.class);
            return comment.replace("表", "");
//            return String.valueOf(map.get("table_comment"));
        }

        class TableDesc {
            private String field;
            private String type;
            private String allowNull;
            private String key;
            private String defaultValue;
            private String extra;

            public TableDesc() {}

            public TableDesc(String field, String type, String allowNull, String key, String defaultValue, String extra) {
                this.field = field;
                this.type = type;
                this.allowNull = allowNull;
                this.key = key;
                this.defaultValue = defaultValue;
                this.extra = extra;
            }

            public String getField() {
                return field;
            }
            public void setField(String field) {
                this.field = field;
            }
            public String getType() {
                return type;
            }
            public void setType(String type) {
                this.type = type;
            }
            public String getAllowNull() {
                return allowNull;
            }
            public void setAllowNull(String allowNull) {
                this.allowNull = allowNull;
            }
            public String getKey() {
                return key;
            }
            public void setKey(String key) {
                this.key = key;
            }
            public String getDefaultValue() {
                return defaultValue;
            }
            public void setDefaultValue(String defaultValue) {
                this.defaultValue = defaultValue;
            }
            public String getExtra() {
                return extra;
            }
            public void setExtra(String extra) {
                this.extra = extra;
            }

            @Override
            public String toString() {
                return "field=" + this.field + "," +
                        "type=" + this.type + "," +
                        "null=" + this.allowNull + "," +
                        "key=" + this.key + "," +
                        "default=" + this.defaultValue + "," +
                        "extra=" + this.extra;
            }
        }

    }


}
