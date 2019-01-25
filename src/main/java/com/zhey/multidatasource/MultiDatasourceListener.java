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

import org.springframework.boot.Banner;
import org.springframework.boot.builder.ParentContextApplicationContextInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 类修改自 org.springframework.cloud.bootstrap.BootstrapApplicationListener
 *
 * @author zhey
 */
public class MultiDatasourceListener
        implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    private static final String PROPERTY_SOURCE_NAME = "multidatasource";
    private static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 1;
    public static final String DEFAULT_PROPERTIES = "defaultProperties";
    private static boolean loaded = false;

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        if (!environment.getProperty("com.zhey.multidatasource.enabled", Boolean.class, true)) {
            return;
        }
        // 判断是否已经启动
        if (true == loaded) {
            return;
        } else {
            loaded = true;
        }
        ConfigurableApplicationContext context = null;
        String configName = "multidatasource";

        for (ApplicationContextInitializer<?> initializer :
                event.getSpringApplication().getInitializers()) {
            if (initializer instanceof ParentContextApplicationContextInitializer) {
                context =
                        findBootstrapContext(
                                (ParentContextApplicationContextInitializer) initializer,
                                configName);
            }
        }
        if (context == null) {
            context = bootstrapServiceContext(environment, configName);
        }

        MultDatasourceApplicationContextInitializer initializer =
                new MultDatasourceApplicationContextInitializer();
        initializer.setApplicationContext(context);
        event.getSpringApplication().addInitializers(initializer);
    }

    private ConfigurableApplicationContext findBootstrapContext(
            ParentContextApplicationContextInitializer initializer, String configName) {
        Field field =
                ReflectionUtils.findField(
                        ParentContextApplicationContextInitializer.class, "parent");
        ReflectionUtils.makeAccessible(field);
        ConfigurableApplicationContext parent =
                safeCast(
                        ConfigurableApplicationContext.class,
                        ReflectionUtils.getField(field, initializer));
        if (parent != null && !configName.endsWith(parent.getId())) {
            parent = safeCast(ConfigurableApplicationContext.class, parent.getParent());
        }
        return parent;
    }

    private <T> T safeCast(Class<T> type, Object object) {
        try {
            return type.cast(object);
        } catch (ClassCastException e) {
            return null;
        }
    }

    private ConfigurableApplicationContext bootstrapServiceContext(
            ConfigurableEnvironment environment, String configName) {
        StandardEnvironment standardEnvironment = new StandardEnvironment();
        MutablePropertySources propertySources = standardEnvironment.getPropertySources();
//        for (PropertySource<?> propertySource : propertySources) {
//            propertySources.remove(propertySource.getName());
//        }
//
        String configLocation =
                environment.resolvePlaceholders("${spring.cloud.bootstrap.location:}");
        Map<String, Object> bootstrapMap = new HashMap<String, Object>();
        bootstrapMap.put("spring.config.name", configName);
        if (StringUtils.hasText(configLocation)) {
            bootstrapMap.put("spring.config.location", configLocation);
        }
        propertySources.addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, bootstrapMap));
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            propertySources.addLast(propertySource);
        }
        SpringApplicationBuilder builder =
                new SpringApplicationBuilder()
                        .profiles(environment.getActiveProfiles())
                        .bannerMode(Banner.Mode.OFF)
                        .environment(standardEnvironment)
                        .profiles("spring.application.name:" + configName)
                        .registerShutdownHook(false)
                        .logStartupInfo(false)
                        .web(false);
        List<Class<?>> soures = new ArrayList<Class<?>>();
        soures.add(Loader.class);
        builder.sources(soures.toArray(new Class[soures.size()]));
        final ConfigurableApplicationContext context = builder.run();
        mergeDefaultProperties(environment.getPropertySources(), standardEnvironment.getPropertySources());
        return context;
    }

    private void mergeDefaultProperties(MutablePropertySources environment,
                                        MutablePropertySources bootstrap) {
        String name = DEFAULT_PROPERTIES;
        if (!bootstrap.contains(name)) {
            return;
        }
        PropertySource<?> source = bootstrap.get(name);
        if (source instanceof MapPropertySource) {
            Map<String, Object> map = ((MapPropertySource) source).getSource();
            // The application name is "bootstrap" (by default) at this point and
            // we don't want that to appear in the parent context at all.
            map.remove("spring.application.name");
        }
        if (!environment.contains(name)) {
            environment.addLast(source);
        }
        mergeAdditionalPropertySources(environment, bootstrap);
    }

    private void mergeAdditionalPropertySources(MutablePropertySources environment,
                                                MutablePropertySources bootstrap) {
        PropertySource<?> defaultProperties = environment.get(DEFAULT_PROPERTIES);
        ExtendedDefaultPropertySource result = defaultProperties instanceof ExtendedDefaultPropertySource
                ? (ExtendedDefaultPropertySource) defaultProperties
                : new ExtendedDefaultPropertySource(defaultProperties.getName(),
                defaultProperties);
        for (PropertySource<?> source : bootstrap) {
            if (!environment.contains(source.getName())) {
                result.add(source);
            }
        }
        for (String name : result.getPropertySourceNames()) {
            bootstrap.remove(name);
        }
        environment.replace(DEFAULT_PROPERTIES, result);
        bootstrap.replace(DEFAULT_PROPERTIES, result);
    }

    private static class ExtendedDefaultPropertySource
            extends SystemEnvironmentPropertySource {

        private final CompositePropertySource sources;
        private final List<String> names = new ArrayList<>();

        public ExtendedDefaultPropertySource(String name,
                                             PropertySource<?> propertySource) {
            super(name, findMap(propertySource));
            this.sources = new CompositePropertySource(name);
        }

        public CompositePropertySource getPropertySources() {
            return this.sources;
        }

        public List<String> getPropertySourceNames() {
            return this.names;
        }

        public void add(PropertySource<?> source) {
            if (source instanceof EnumerablePropertySource
                    && !this.names.contains(source.getName())) {
                this.sources.addPropertySource(source);
                this.names.add(source.getName());
            }
        }

        @Override
        public Object getProperty(String name) {
            if (this.sources.containsProperty(name)) {
                return this.sources.getProperty(name);
            }
            return super.getProperty(name);
        }

        @Override
        public boolean containsProperty(String name) {
            if (this.sources.containsProperty(name)) {
                return true;
            }
            return super.containsProperty(name);
        }

        @Override
        public String[] getPropertyNames() {
            List<String> names = new ArrayList<>();
            names.addAll(Arrays.asList(this.sources.getPropertyNames()));
            names.addAll(Arrays.asList(super.getPropertyNames()));
            return names.toArray(new String[0]);
        }

        @SuppressWarnings("unchecked")
        private static Map<String, Object> findMap(PropertySource<?> propertySource) {
            if (propertySource instanceof MapPropertySource) {
                return (Map<String, Object>) propertySource.getSource();
            }
            return new LinkedHashMap<String, Object>();
        }

    }
}
