package com.zhey.multidatasource;

import org.springframework.context.ApplicationContext;

/**
 * @author zhey
 */
public interface DatasourceAutoConfiguration {
    void setApplicationContext(ApplicationContext applicationContext);

    void setProperties(MultiDatasourceProperties properties);

    void init();
}
