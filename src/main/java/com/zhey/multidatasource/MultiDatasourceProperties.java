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

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhey
 */
@ConfigurationProperties(prefix = "multi")
public class MultiDatasourceProperties {
    /**
     * druid数据源配置信息
     */
    private Map<String, DruidDataSource> druid = new HashMap<String, DruidDataSource>();
    /**
     * tomcat数据源配置信息
     */
    private Map<String, DataSource> tomcat = new HashMap<String, DataSource>();

    /**
     * 数据源和mybatis的u主bean
     */
    private Map<String, String> primary = new HashMap<String, String>();

    /**
     * 事物管理器配置信息
     */
    private Map<String, String> transactionManager = new HashMap<String, String>();
    /**
     * mybatis配置信息
     */
    private Map<String, MultiMybatisProperties> mybatis = new HashMap<>();

    public Map<String, DruidDataSource> getDruid() {
        return druid;
    }

    public void setDruid(Map<String, DruidDataSource> druid) {
        this.druid = druid;
    }

    public Map<String, String> getPrimary() {
        return primary;
    }

    public void setPrimary(Map<String, String> primary) {
        this.primary = primary;
    }

    public Map<String, String> getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(Map<String, String> transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Map<String, DataSource> getTomcat() {
        return tomcat;
    }

    public void setTomcat(Map<String, DataSource> tomcat) {
        this.tomcat = tomcat;
    }

    public Map<String, MultiMybatisProperties> getMybatis() {
        return mybatis;
    }

    public void setMybatis(Map<String, MultiMybatisProperties> mybatis) {
        this.mybatis = mybatis;
    }
}
