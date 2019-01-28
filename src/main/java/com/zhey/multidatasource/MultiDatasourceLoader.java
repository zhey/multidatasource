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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

/**
 * @author zhey
 */
@Configuration
@EnableConfigurationProperties(MultiDatasourceProperties.class)
public class MultiDatasourceLoader {
    @Autowired
    private MultiDatasourceProperties properties;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired(required = false)
    private List<DatasourceAutoConfiguration> datasourceAutoConfigurations;

    @PostConstruct
    public void init() throws Exception {
        if (!verify(properties)) {
            throw new Exception("没有配置主数据源");
        }
        for (DatasourceAutoConfiguration datasourceAutoConfiguration : datasourceAutoConfigurations) {
            datasourceAutoConfiguration.setApplicationContext(this.applicationContext);
            datasourceAutoConfiguration.setProperties(this.properties);
            datasourceAutoConfiguration.init();
        }
    }

    private boolean verify(MultiDatasourceProperties properties) {
        boolean hasPrimary = false;
        String primaryKey = properties.getPrimary().get("datasource");
        if (properties.getTomcat().keySet().contains(primaryKey)) {
            hasPrimary = true;
        }
        if (properties.getDruid().keySet().contains(primaryKey)) {
            hasPrimary = true;
        }
        return hasPrimary;

    }

    @ConditionalOnClass(DataSource.class)
    @Import({DatasourceAutoConfigurationImport.Tomcat.class, DatasourceAutoConfigurationImport.Druid.class})
    protected static class DatasourceImportor {

    }

}
