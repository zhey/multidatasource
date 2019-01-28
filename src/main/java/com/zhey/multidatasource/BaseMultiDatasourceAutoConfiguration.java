package com.zhey.multidatasource;

import org.springframework.context.ApplicationContext;

/**
 * @author zhey
 */
public abstract class BaseMultiDatasourceAutoConfiguration implements MultiDatasourceAutoConfigurationInterface {
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
            datasource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract void datasource() throws Exception;
}