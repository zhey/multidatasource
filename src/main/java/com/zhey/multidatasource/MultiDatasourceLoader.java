package com.zhey.multidatasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhey
 */
@Configuration
@EnableConfigurationProperties(MultiDatasourceProperties.class)
public class MultiDatasourceLoader {

    @Bean
    @ConditionalOnClass(DruidDataSource.class)
    public DruidMultiDatasourceAutoConfiguration druidMultiDatasourceAutoConfiguration() {
        return new DruidMultiDatasourceAutoConfiguration();
    }
}
