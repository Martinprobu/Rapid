package org.rapid.domain.template.service;

import org.rapid.common.enums.DomainType;
import org.rapid.common.model.TableDesc;
import org.rapid.common.utils.ConfigUtil;
import org.rapid.core.config.TplConfig;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * 代码生成主类
 */
public class CodeGen {

    Logger log = Logger.getGlobal();
    public static HashMap<String, String> configMap = ConfigUtil.wrapConfigMap(DomainType.TEMPLATE);
    //以下配置基本可以不变
    public static final String CONTROLLER_PACKAGE = configMap.get("java.controller_package");      //controller层package
    public static final String CONTROLLER_SUFFIX = configMap.get("java.controller_suffix");       //controller层类后缀, //以下均是相同作用
    public static final String SERVICE_PACKAGE = configMap.get("java.service_package");
    public static final String SERVICE_SUFFIX = configMap.get("java.service_suffix");
    public static final String MAPPER_PACKAGE = configMap.get("java.mapper_package");
    public static final String MAPPER_SUFFIX = configMap.get("java.mapper_suffix");
    public static final String COLUMNS_PACKAGE = configMap.get("java.columns_package");
    public static final String COLUMNS_SUFFIX = configMap.get("java.columns_suffix");
    public static final String HTTP_MOCK_PACKAGE = configMap.get("java.http_mock_package");
    public static final String HTTP_MOCK_SUFFIX = configMap.get("java.http_mock_suffix");
    public static final String KUDU_PACKAGE = "/kudu";
    public static final String KUDU_SUFFIX = ".kudu";
    public static final String VUE_PACKAGE = "/vue";
    public static final String VUE_LIST_SUFFIX = "List.vue";
    public static final String VUE_DETAIL_SUFFIX = "Page.vue";

    private String packagePath = configMap.get("domain.package_path");
    private String serverPath = configMap.get("domain.server_path");
    private String jdbcPath = configMap.get("domain.jdbc_path");
    private String author = configMap.get("domain.author");
    private String version = configMap.get("domain.version");


    private String basePath = getBasePath();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private String dateTime = sdf.format(new Date());
    private CodeGenService service = new CodeGenService();
    private String tableComment;                        //表描述
    private String tableName;
    private List<TableDesc> tableFileds; //表字段描述
    private String className;
    private String objectName;
    private String domainName;

    private ClassLoaderTemplateResolver templateResolver;

    /**
     * 构造传参
     */
    public CodeGen() {
    }

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
    public void readTable(String tableName, File child) throws IOException {
        this.tableComment = service.descTableComment(this.jdbcPath.substring(this.jdbcPath.lastIndexOf("/") + 1), tableName);
        this.tableFileds = service.descTable(tableName, child.getPath());
        this.className = nameCovert_(tableName, true);      //类名字, 首字母大写
        this.objectName = nameCovert_(tableName, false);    //对象名字, 首字母小写
        this.domainName = nameDomainCovert(tableName);                //领域名字, 就是类名的简写，用于放url,api生成等
        this.tableName = tableName.toLowerCase();

        String classPath = this.getClass().getClassLoader().getResource("").getPath();
        String twoPath = child.getPath().split("/target/classes/template/")[1];
        twoPath = twoPath.substring(twoPath.indexOf("/") + 1);
        twoPath = twoPath.replace("[tableName]", className);
        String realPath = classPath + "template/output/" + twoPath;
        String endPrefix = realPath.substring(realPath.lastIndexOf(".") + 1);

        System.out.println("org.rapid.readTable Generate  ========================================== ");

        TemplateEngine templateEngine = TplConfig.getEngine();
        if (null == templateResolver) {
            templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setOrder(1);
            templateResolver.setResolvablePatterns(Collections.singleton("*." + endPrefix));
//        templateResolver.setPrefix("domain/template/");
            templateResolver.setPrefix("template/");
//        templateResolver.setSuffix(".txt");
            templateResolver.setTemplateMode(TemplateMode.TEXT);
            templateResolver.setCharacterEncoding("UTF-8");
            templateResolver.setCacheable(false);
            templateEngine.addTemplateResolver(templateResolver);
        }

        final Context ctx = new Context(Locale.CHINA);
        HashMap<String, Object> map = new HashMap<>();
        map.put("tableComment", this.tableComment);
        map.put("tableFileds", this.tableFileds);
        map.put("className", this.className);
        map.put("objectName", this.objectName);
        map.put("domainName", this.domainName);
        map.put("tableName", this.tableName);
        map.put("packagePath", this.packagePath);
        map.put("author", this.author);
        map.put("version", this.version);

        ctx.setVariables(map);
        String templateRelaPath = child.getPath().replace(classPath + "template/", "");
//        genFile(readFileContent(child), realPath);

        String content = templateEngine.process(templateRelaPath, ctx);
        log.info(content);
//        templateEngine.clearTemplateCache();

        genFile(content, realPath);

    }

    @Deprecated
    public void readTable2(String tableName, File child) throws IOException {
        this.tableComment = service.descTableComment(this.jdbcPath.substring(this.jdbcPath.lastIndexOf("/") + 1), tableName);
        this.tableFileds = service.descTable(tableName, child.getPath());
        this.className = nameCovert_(tableName, true);      //类名字, 首字母大写
        this.objectName = nameCovert_(tableName, false);    //对象名字, 首字母小写
        this.domainName = nameDomainCovert(tableName);                //领域名字, 就是类名的简写，用于放url,api生成等
        this.tableName = tableName.toLowerCase();

        String classPath = this.getClass().getClassLoader().getResource("").getPath();
        String twoPath = child.getPath().split("/target/classes/template/")[1];
        twoPath = twoPath.substring(twoPath.indexOf("/") + 1);
        twoPath = twoPath.replace("[tableName]", className);
        String realPath = classPath + "template/" + twoPath;
        String endPrefix = realPath.substring(realPath.lastIndexOf(".") + 1);

        System.out.println("org.rapid.readTable Generate2  ========================================== ");

        final TemplateEngine templateEngine = TplConfig.getEngine();
        if (null == templateResolver) {
            templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setOrder(1);
            templateResolver.setResolvablePatterns(Collections.singleton("*." + endPrefix));
//        templateResolver.setPrefix("domain/template/");
            templateResolver.setPrefix("template/");
//        templateResolver.setSuffix(".txt");
            templateResolver.setTemplateMode(TemplateMode.TEXT);
            templateResolver.setCharacterEncoding("UTF-8");
            templateResolver.setCacheable(false);
            templateEngine.addTemplateResolver(templateResolver);
        }

        final Context ctx = new Context(Locale.CHINA);
        HashMap<String, Object> map = new HashMap<>();
        map.put("tableComment", this.tableComment);
        map.put("tableFileds", this.tableFileds);
        map.put("className", this.className);
        map.put("objectName", this.objectName);
        map.put("domainName", this.domainName);
        map.put("tableName", this.tableName);
        map.put("myPackagePath", this.tableName);
        map.put("author", this.author);
        map.put("version", this.version);


        ctx.setVariables(map);
        String templateRelaPath = child.getPath().replace(classPath + "template/", "");
//        genFile(readFileContent(child), realPath);

        String content = templateEngine.process(templateRelaPath, ctx);
        log.info(content);

        genFile(content, realPath);

    }

    public String readFileContent(File file) throws IOException {
        StringBuilder buffer = new StringBuilder();
        InputStream is = new FileInputStream(file);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
        return buffer.toString();
    }

    /**
     * 生成文件io
     *
     * @param conent  文件里的代码内容
     * @param subPath 各层对应的中间目录
     * @param suffix  各层对应的文件后缀
     */
    private void genFile(String conent, String subPath, String suffix) {
        System.out.println(suffix + "  =========================== >>>>> " + conent.toString());
        try {
            File fileDirect = new File(this.basePath + File.separator + this.packagePath.replace(".", File.separator) + subPath);
            fileDirect.mkdirs();
            File file = new File(this.basePath + File.separator + this.packagePath.replace(".", File.separator) + subPath + File.separator + className + suffix);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(conent.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void genFile(String conent, String path) {
//        System.out.println(suffix + "  =========================== >>>>> " + conent.toString());
        try {
            File fileDirect = new File(path.substring(0, path.lastIndexOf("/")));
            fileDirect.mkdirs();
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(conent.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取数据库中的所有表名
     */
    public List<String> readTables() {
        String sql = CodeGen.sqlConsts.showTables;
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
        for (int i = 0; i < classNamePath.length; i++) {
            firstLetter = Integer.valueOf(classNamePath[i].charAt(0));
            if (firstLetter >= 97 && firstLetter <= 122) {
                char c;
                if (i == 0) {
                    if (firstUpper) {
                        c = (char) (firstLetter - 32);
                    } else {
                        c = (char) firstLetter;
                    }
                } else {
                    c = (char) (firstLetter - 32);
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
     *
     * @param type
     * @return
     */
    private String typeContvert(String type) {
        if (type.startsWith("int") || type.startsWith("tinyint") || type.startsWith("smallint") || type.startsWith("bit") || type.startsWith("mediumint")) {
            return "Integer";
        } else if (type.startsWith("bigint")) {
            return "Long";
        } else if (type.startsWith("float")) {
            return "Float";
        } else if (type.startsWith("double")) {
            return "Double";
        } else if (type.startsWith("char") || type.startsWith("varchar") || type.startsWith("text")) {
            return "String";
        } else if (type.startsWith("datetime") || type.startsWith("timestamp")) {
            return "Timestamp";
        }
        return type;
    }

    /**
     * 取得程序根目录
     */
    public String getBasePath() {
        String basePath = CodeGen.class.getResource("") + "";//
        if (basePath.startsWith("file:")) {
            if (basePath.charAt(7) == ':') { // Windows系统路径
                basePath = basePath.substring(6);
            } else { // Unix系统路径
                basePath = basePath.substring(5);
            }
        }
//            basePath = basePath.substring(0, basePath.indexOf("uniutil")+7);
//            basePath = basePath + File.separator +  "target" + File.separator + "autoGen";
        //修改生成文件路径 ex_panggn3
//        basePath = basePath.replace("target/classes/", "src/main/java/");
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

