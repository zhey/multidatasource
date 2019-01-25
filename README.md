# multidatasource
配置文件格式为：
```
multi:
  druid:
    userdruid:
      url:
      username: root
      passowrd: root
      driver-class-name: com.mysql.jdbc.Driver
      initialSize: 10
      maxActive: 100
      maxWait: 100
      minIdel: 10
      validationQuery: select 2
    agedruid:
      url:
      username: root
      passowrd: root
      driver-class-name: com.mysql.jdbc.Driver
      initialSize: 10
      maxActive: 100
      maxWait: 100
      minIdel: 10
      validationQuery: select 2
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

