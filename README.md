# multidatasource
提供自定义的数据库配置，可以添加多个数据库连接池，数据库连接池可以不同。在生成数据源的过程中同时创建相应的事务管理器。数据源的名称为multi.druid或multi.tomcat下配置的名称，在multi
.transactionManager节点下为数据源配置对应的事务管理器的名称

1、配置文件格式为：
```
multi:
  druid:
    userdruid:
      url:
      username: root
      password: root
      driver-class-name: com.mysql.jdbc.Driver
      initialSize: 10
      maxActive: 100
      maxWait: 100
      minIdel: 10
      validationQuery: select 2
    agedruid:
      url:
      username: root
      password: root
      driver-class-name: com.mysql.jdbc.Driver
      initialSize: 10
      maxActive: 100
      maxWait: 100
      minIdel: 10
      validationQuery: select 2
  tomcat:
    other:
      url:
      username: 
      password: 
      driver-class-name: 
      max-active: 
      max-idel: 
      minIdel: 
      validationQuery: 
  transactionManager:
    userdruid: transactionManager
    agedruid: transactionManagerAge
  mybatis:
    mybatisuser:
      datasource: userdruid
      basePackage:
      - com.zhey.name.dao
      type-aliases-package: com.zhey.name.entity
      mapper-locations: classpath:mapper/name/*.xml
    mybatisage:
      datasource: agedruid
      basePackage:
      - com.zhey.age.dao
      type-aliases-package: com.zhey.age.entity
      mapper-locations: classpath:mapper/age/*.xml
  primary:
    datasource: userdruid
    mybatis: mybatisuser
```
2、设计思路：

使用`org.springframework.context.ApplicationListener`机制引入一个父容器，在父容器中创建数据库连接和mybatis，从而屏蔽掉子容器中数据库的自动配置。
