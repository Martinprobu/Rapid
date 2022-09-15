package org.rapid.core.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collections;

public class TplConfig {
    final static TemplateEngine templateEngine = new TemplateEngine();
    // templateEngine.addTemplateResolver(textTemplateResolver());

    public static TemplateEngine getEngine(){
        return templateEngine;
    }

    /**
     * 配置模板的位置,前缀,后缀等
     * @return
     */
//    public static ClassLoaderTemplateResolver textTemplateResolver() {
//        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//        templateResolver.setOrder(1);
//        templateResolver.setResolvablePatterns(Collections.singleton("text/*"));
////        templateResolver.setPrefix("/tpl/");
////        templateResolver.setSuffix(".txt");
//        templateResolver.setTemplateMode(TemplateMode.TEXT);
//        templateResolver.setCharacterEncoding("UTF-8");
//        templateResolver.setCacheable(false);
//        return templateResolver;
//    }
}
