package com.zhey.multidatasource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhey
 */
@Configuration
@EnableConfigurationProperties(MultiDatasourceProperties.class)
@Import({MultiDatasourceLoader.class, TransactionManagerLoader.class, MultiMybatisLoader.class})
public class Loader {
}
