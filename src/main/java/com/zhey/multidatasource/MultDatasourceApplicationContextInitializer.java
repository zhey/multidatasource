package com.zhey.multidatasource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zhey
 */
public class MultDatasourceApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ApplicationContext parent = applicationContext.getParent();
        if (parent == null) {
            applicationContext.setParent(this.applicationContext);
        } else {
            while (true) {
                if (parent.getParent() != null) {
                    parent = parent.getParent();
                } else {
                    ((ConfigurableApplicationContext) parent).setParent(this.applicationContext);
                }
            }
        }
    }
}
