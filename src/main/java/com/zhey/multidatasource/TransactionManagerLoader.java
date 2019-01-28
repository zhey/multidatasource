package com.zhey.multidatasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * @author zhey
 */
public class TransactionManagerLoader {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MultiDatasourceProperties properties;

    @PostConstruct
    public void init() throws Exception {
        transactionManager();
    }

    public void transactionManager() throws Exception {
        String primaryDatasource = properties.getPrimary().get("datasource");
        DefaultListableBeanFactory factory =
                (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        for (String key : properties.getTransactionManager().keySet()) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class);
            DataSource dataSource = (DataSource) applicationContext.getBean(key);
            if (dataSource == null) {
                throw new Exception("事物配置错误，没有发现数据源：" + key);
            } else {
                builder.addConstructorArgValue(dataSource);
                BeanDefinition definition = builder.getBeanDefinition();
                if (primaryDatasource.equals(key)) {
                    definition.setPrimary(true);
                }
                factory.registerBeanDefinition(properties.getTransactionManager().get(key), definition);
            }
        }
    }
}
