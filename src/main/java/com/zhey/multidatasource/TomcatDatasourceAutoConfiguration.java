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
