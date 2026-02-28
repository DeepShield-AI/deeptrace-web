# 1. 架构分层
<a href="#dummy"><img src="https://i.hd-r.cn/fd6bbc44-c7e2-4b7e-9fda-0f87d7265364.png" alt="struture" /></a>

# 2. 各组件介绍
## Adapter：处理外部服务 
**程序架构组织**

| 组件      | 包名        | 功能                     |
|---------|-----------|--------------------------|
| Adapter | web       | 处理页面请求的Controller |
| Adapter | scheduler | 处理定时器的请求         |

**类命名规范**

| 种类             | 对象 | 示例                |
|----------------|----|-------------------|
| web       | /  | XXXXController.java     |
| scheduler | /  | XXXXScheduler.java |



## App：实现业务逻辑的编排和协调，接受外部请求，将请求拆解为领域层(Domain)层可处理的指令
- trace: 处理trace业务
- metric: 处理metric业务
- log: 处理log业务
- user: 处理 user 业
- agent: 处理agent业务
**程序架构组织**

| 组件  | 包名        | 功能                        |
|-----|-----------|---------------------------|
| App | executor  | 处理request，包括command和query |
| App | convertor | 处理DTO实体之间的转换              |

**类命名规范**

| 种类             | 对象        | 示例                 |
|----------------|-----------|--------------------|
| executor       | 命令执行器     | XXXXCmdExe.java    |
| executor.query | 查询执行器     | XXXXQueryExe.java        |
| /              | service实现 | XXXServiceImpl.java |
| convertor      |转换器|XXXConvertor.java|



## Client：定义业务逻辑对外提供的服务
**程序架构组织**

| 组件     | 包名  | 功能       |
|--------|-----|----------|
| Client | api | 业务服务API  |
| Client | dto | 业务服务的dto |

**类命名规范**

| 种类        | 对象                                        | 示例                           |
|-----------|-------------------------------------------|------------------------------|
| api       | API  service                              | XXXServiceI.java             |
| dto       | 增删改服务入参                                   | XXXCmd.java                  |
| dto       | 查询服务入参                                    | XXXQry.java                  |
| dto.data  | 查询服务返回结果（对外统一接口返回结果，统一用Response或它的子类来封装。） | XXXDto.java 、 ErrorCode.java |
| dto.event | 领域驱动事件入参                                  | XXXDto.java                  |


## Domain：核心业务逻辑承载层‌，完全聚焦于业务领域的本质规则和逻辑，不包含任何外部依赖（如数据库）
- trace: 处理trace业务
- metric: 处理metric业务
- log: 处理log业务
- user: 处理 user 业
- agent: 处理agent业务
**程序架构组织**

| 组件     | 包名            | 功能           |
|--------|---------------|--------------|
| Domain | gateway       | 领域网关，与外部依赖解耦 |
| Domain | ability | 领域核心能力       |

**类命名规范**

| 种类        | 对象     | 示例             |
|-----------|--------|----------------|
| gateway       | 防腐服务定义 | XXXGateway.java |
| ability       | 领域能力实现 | XXXDomainService.java    |


## Infrastructure：处理外部依赖，比如数据库
- trace: 处理trace业务
- metric: 处理metric业务
- log: 处理log业务
- user: 处理 user 业
- agent: 处理agent业务
**程序架构组织**

| 组件       | 包名/类名               | 功能                       |
|----------|---------------------|--------------------------|
| Infrastructure | XXXGatewayImpl.java | 防腐服务实现 -- 外部依赖实现逻辑，比如数据库 |
| Infrastructure | XXXMapper.java      | 数据库映射                    |
| Infrastructure | config              | 配置信息 --比如外部diamond配置     |
| Infrastructure | XXXDO.java          | 存储的对象                    |

**类命名规范**

| 种类        | 对象                                        | 示例             |
|-----------|-------------------------------------------|----------------|
| api       | 防腐服务实现  | XXXGatewayImpl.java |
| dto       | 增删改服务入参                                   | XXXCmd.java    |
| dto       | 查询服务入参                                    | XXXQry.java    |
| dto.data  | | XXXDto.java    |
| dto.event | 领域驱动事件入参                                  | XXXDto.java    |



## Common：常量或工具类
**程序架构组织**

| 组件      | 包名        | 功能          |
|---------|-----------|-------------|
| Common  | utils     | 工具类（多个组件共用） |
| Common  | constants | 常量类 （多个组件共用）|
| Common  | enums     | 枚举类（多个组件共用） |

**类命名规范**

| 种类       | 示例              |
|-----------|-----------------|
| utils       | XXXUtil.java    |
| constants    | XXXContant.java |
| enums       | XXXEnum.java    |


## Start：应用启用、全局配置等

# 3. 模块之间的依赖关系--待补充
<a href="#dummy"><img src="" alt="struture" /></a>
