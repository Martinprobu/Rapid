package org.rapid.common.utils;

import org.rapid.common.enums.DomainType;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigUtil {

    static Logger log = Logger.getGlobal();

    public static HashMap wrapConfigMap(DomainType type) {
        HashMap result = new HashMap<String, String>();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        Properties properties = yaml.getObject();
        properties.forEach((key, val) -> {
            result.put(key, val);
        });

        String domainName = type.getName();
        YamlPropertiesFactoryBean yamlDomain = new YamlPropertiesFactoryBean();
        if (type.equals(DomainType.TEMPLATE)) {
            yamlDomain.setResources(new ClassPathResource(domainName + "/" + domainName + ".yml"));
        } else {
            yamlDomain.setResources(new ClassPathResource("domain/" + domainName + "/" + domainName + ".yml"));
        }

        Properties propertiesDomain = yamlDomain.getObject();
        propertiesDomain.forEach((key, val) -> {
            result.put(key, val);
        });
        log.info(result.toString());

        return result;
    }
}
