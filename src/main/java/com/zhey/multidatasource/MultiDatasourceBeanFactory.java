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
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

/**
 * @author zhey
 */
public class MultiDatasourceBeanFactory implements FactoryBean {
    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object getObject() throws Exception {
        return dataSource;
    }

    @Override
    public Class<?> getObjectType() {
        if (dataSource instanceof DruidDataSource) {
            return DruidDataSource.class;
        } else if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            return org.apache.tomcat.jdbc.pool.DataSource.class;
        } else {
            return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
