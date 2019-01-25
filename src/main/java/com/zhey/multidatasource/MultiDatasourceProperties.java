package com.zhey.multidatasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhey
 */
@ConfigurationProperties(prefix = "multi")
public class MultiDatasourceProperties {
    /**
     * 数据源配置信息
     */
    private Map<String, DruidDataSource> druid = new HashMap<String, DruidDataSource>();

    /**
     * 数据源和mybatis的u主bean
     */
    private Map<String, String> primary = new HashMap<String, String>();

    /**
     * 事物管理器配置信息
     */
    private Map<String, String> transactionManager = new HashMap<String, String>();

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
}
