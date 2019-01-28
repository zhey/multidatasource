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

import org.springframework.context.ApplicationContext;

/**
 * @author zhey
 */
public abstract class BaseDatasourceAutoConfiguration implements DatasourceAutoConfiguration {
    protected ApplicationContext applicationContext;
    protected MultiDatasourceProperties properties;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public MultiDatasourceProperties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(MultiDatasourceProperties properties) {
        this.properties = properties;
    }

    @Override
    public void init() {
        try {
            createDatasource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract void createDatasource() throws Exception;
}
