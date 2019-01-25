package com.zhey.multidatasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * @author zhey
 */
@Configuration
@EnableConfigurationProperties(MultiDatasourceProperties.class)
@Import(MultiDatasourceLoader.class)
public class Loader {
    @Autowired
    private MultiDatasourceProperties properties;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired(required = false)
    private MultiDatasourceAutoConfigurationInterface datasourceAutoConfiguration;

    @PostConstruct
    public void init() {
        datasourceAutoConfiguration.setApplicationContext(this.applicationContext);
        datasourceAutoConfiguration.setProperties(this.properties);
        datasourceAutoConfiguration.init();
    }
}
