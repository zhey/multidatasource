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
