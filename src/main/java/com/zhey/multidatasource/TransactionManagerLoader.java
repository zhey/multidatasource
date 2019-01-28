/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    /**
     * 创建事务管理器
     *
     * @throws Exception
     */
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
