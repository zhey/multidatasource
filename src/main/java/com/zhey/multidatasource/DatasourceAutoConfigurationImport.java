package com.zhey.multidatasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @author zhey
 */
public class DatasourceAutoConfigurationImport {
    @ConditionalOnClass(DataSource.class)
    public static class Tomcat {
        @Bean
        public DatasourceAutoConfiguration tomcatDatasourceAutoConfiguration() {
            return new TomcatDatasourceAutoConfiguration();
        }
    }

    @ConditionalOnClass(DruidDataSource.class)
    public static class Druid {
        @Bean
        public DatasourceAutoConfiguration druidMultiDatasourceAutoConfiguration() {
            return new DruidDatasourceAutoConfiguration();
        }
    }
}
