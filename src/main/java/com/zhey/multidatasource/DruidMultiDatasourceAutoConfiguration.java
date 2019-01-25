package com.zhey.multidatasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author zhey
 */
@Component
@ConditionalOnClass(DruidDataSource.class)
public class DruidMultiDatasourceAutoConfiguration extends BaseMultiDatasourceAutoConfiguration {


    @Override
    void datasource() throws Exception {
        String primaryDatasource = properties.getPrimary().get("datasource");
        if (StringUtils.isEmpty(primaryDatasource)) {
            throw new Exception("未配置主数据源");
        } else {
            if (!properties.getDruid().keySet().contains(primaryDatasource)) {
                throw new Exception("主数据源配置错误，未找到主数据源：" + primaryDatasource);
            } else {
                DefaultListableBeanFactory factory =
                        (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                for (String key : properties.getDruid().keySet()) {
                    BeanDefinitionBuilder builder =
                            BeanDefinitionBuilder.rootBeanDefinition(MultiDatasourceBeanFactory.class);
                    builder.addPropertyValue("dataSource", properties.getDruid().get(key));
                    BeanDefinition definition = builder.getBeanDefinition();
                    if (key.equals(primaryDatasource)) {
                        definition.setPrimary(true);
                    }
                    factory.registerBeanDefinition(key, definition);
                }
            }
        }
    }
}
