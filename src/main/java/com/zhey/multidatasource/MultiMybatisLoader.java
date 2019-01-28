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

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * @author zhey
 */
@ConditionalOnClass(SqlSessionFactoryBean.class)
public class MultiMybatisLoader {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MultiDatasourceProperties properties;

    @PostConstruct
    public void init() throws Exception {
        sqlSessionFactory();
        sqlSessionTemplate();
        mapper();
    }

    /**
     * 根据配置向容器中添加SqlSessionFactory
     *
     * @throws Exception
     */
    public void sqlSessionFactory() throws Exception {
        String primaryMybatis = properties.getPrimary().get("mybatis");
        if (StringUtils.isEmpty(primaryMybatis)) {
            throw new Exception("没有配置主mybatis");
        } else {
            if (!properties.getMybatis().keySet().contains(primaryMybatis)) {
                throw new Exception("mybtis配置错误：没有找到主要mybatis：" + primaryMybatis);
            } else {
                DefaultListableBeanFactory factory =
                        (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                for (String key : properties.getMybatis().keySet()) {
                    BeanDefinitionBuilder builder =
                            BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class);
                    DataSource dataSource =
                            (DataSource) applicationContext.getBean(properties.getMybatis().get(key).getDatasource());
                    if (dataSource == null) {
                        throw new Exception();
                    }
                    builder.addPropertyValue("datasource", dataSource);
                    builder.addPropertyValue("mapperLocations",
                            properties.getMybatis().get(key).resolveMapperLocations());
                    builder.addPropertyValue("typeAliasesPackage", properties.getMybatis().get(key).getTypeAliasesPackage());
                    BeanDefinition definition = builder.getBeanDefinition();
                    if (StringUtils.equals(primaryMybatis, key)) {
                        definition.setPrimary(true);
                    }
                    factory.registerBeanDefinition(key + "sqlSessionFactory", definition);
                }
            }
        }
    }

    /**
     * 根据配置向容器中添加SqlSessionTemplate
     *
     * @throws Exception
     */
    public void sqlSessionTemplate() throws Exception {
        String primaryMybatis = properties.getPrimary().get("mybatis");
        if (StringUtils.isEmpty(primaryMybatis)) {
            throw new Exception("没有配置主mybatis");
        } else {
            if (!properties.getMybatis().keySet().contains(primaryMybatis)) {
                throw new Exception("mybtis配置错误：没有找到主要mybatis：" + primaryMybatis);
            } else {
                DefaultListableBeanFactory factory =
                        (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                for (String key : properties.getMybatis().keySet()) {
                    BeanDefinitionBuilder builder =
                            BeanDefinitionBuilder.rootBeanDefinition(SqlSessionTemplate.class);
                    SqlSessionFactory sqlSessionFactory =
                            (SqlSessionFactory) applicationContext.getBean(key + "sqlSessionFactory");
                    if (sqlSessionFactory == null) {
                        throw new Exception();
                    }
                    builder.addConstructorArgValue(sqlSessionFactory);
                    BeanDefinition definition = builder.getBeanDefinition();
                    if (StringUtils.equals(primaryMybatis, key)) {
                        definition.setPrimary(true);
                    }
                    factory.registerBeanDefinition(key + "sqlSessionTemplate", definition);
                }
            }
        }
    }

    /**
     * 根据配置扫描mapper接口
     *
     * @throws Exception
     */
    public void mapper() throws Exception {
        String primaryMybatis = properties.getPrimary().get("mybatis");
        if (StringUtils.isEmpty(primaryMybatis)) {
            throw new Exception();
        } else {
            if (!properties.getMybatis().keySet().contains(primaryMybatis)) {
                throw new Exception();
            } else {
                DefaultListableBeanFactory factory =
                        (DefaultListableBeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                for (String key : properties.getMybatis().keySet()) {
                    ClassPathMapperScanner scanner = new ClassPathMapperScanner(factory);
                    scanner.setSqlSessionFactoryBeanName(key + "sqlSessionTemplate");
                    scanner.setAnnotationClass(Mapper.class);
                    scanner.registerFilters();
                    scanner.doScan(org.springframework.util.StringUtils.toStringArray(properties.getMybatis().get(key).getBasePackage()));
                }
            }
        }
    }
}
