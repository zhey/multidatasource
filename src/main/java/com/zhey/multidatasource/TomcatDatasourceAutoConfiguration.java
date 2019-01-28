package com.zhey.multidatasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zhey
 */
public class TomcatDatasourceAutoConfiguration extends BaseDatasourceAutoConfiguration {
    @Override
    void createDatasource() throws Exception {
        String primaryDatasource = properties.getPrimary().get("createDatasource");
        if (StringUtils.isEmpty(primaryDatasource)) {
            throw new Exception("未配置主数据源");
        } else {
            DefaultListableBeanFactory factory =
                    (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
            for (String key : properties.getTomcat().keySet()) {
                BeanDefinitionBuilder builder =
                        BeanDefinitionBuilder.rootBeanDefinition(MultiDatasourceBeanFactory.class);
                builder.addPropertyValue("dataSource", properties.getTomcat().get(key));
                BeanDefinition definition = builder.getBeanDefinition();
                if (key.equals(primaryDatasource)) {
                    definition.setPrimary(true);
                }
                factory.registerBeanDefinition(key, definition);
            }
        }
    }
}
