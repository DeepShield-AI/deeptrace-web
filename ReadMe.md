# 1. 架构分层
<a href="#dummy"><img src="https://i.hd-r.cn/5ed6bd68-db48-4415-8adf-3da2aa7e573e.png" alt="struture" /></a>

# 2. 各组件介绍
## Adapter：处理外部服务，主要是面向前端接口，实现一些校验逻辑
**程序架构组织**

| 组件      | 包名        | 功能                   |
|---------|-----------|----------------------|
| Adapter | web       | 处理web页面请求的Controller |
| Adapter | scheduler | 处理定时器的请求             |

**类命名规范**

| 包名        | 对象     | 示例                |
|-----------|--------|-------------------|
| web       | web请求  | XXXXController.java     |
| scheduler | 定时任务请求 | XXXXScheduler.java |



## App：实现业务逻辑的编排和协调，接受外部请求，将请求拆解为领域层(Domain)层可处理的指令
调用领域层的核心业务能力，处理业务逻辑编排，协调多个领域服务完成复杂的业务场景。实现业务逻辑解耦和复用。
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

| 包名            | 对象      | 示例                 |
|----------------|---------|--------------------|
| executor       | 命令执行器   | XXXXCmdExe.java    |
| executor.query | 查询执行器   | XXXXQueryExe.java        |
| /              | service实现 | XXXServiceImpl.java |
| convertor      | 转换器     |XXXConvertor.java|



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


## Domain：核心业务逻辑，封装业务领域的核心规则和逻辑，不包含任何外部依赖（如数据库）
定义业务领域的同于语言和模型，确保代码结构与业务语义的高度对齐
- trace: 处理trace业务
- metric: 处理metric业务
- log: 处理log业务
- user: 处理 user 业
- agent: 处理agent业务
**程序架构组织**

| 组件     | 包名      | 功能           |
|--------|---------|--------------|
| Domain | ability | 领域核心能力       |
| Domain | entity  | 领域实体         |
| Domain | gateway | 领域网关，与外部依赖解耦 |

**类命名规范**

| 种类       | 对象     | 示例              |
|----------|--------|-----------------|
| gateway  | 防腐服务定义 | XXXGateway.java |
| ability  | 领域能力实现 | XXXDomainService.java |


## Infrastructure：处理外部依赖，封装与外部系统的交互逻辑，比如数据库、缓存、消息队列、第三方服务调用等
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
|-----------|--------------|----------------|
| api       | 防腐服务实现  | XXXGatewayImpl.java |




## Common：用于存放通用的枚举、常量或工具类
**程序架构组织**

| 组件    | 包名       | 功能          |
|--------|-----------|-------------|
| Common | utils     | 工具类（多个组件共用） |
| Common | constants | 常量类 （多个组件共用）|
| Common | enums     | 枚举类（多个组件共用） |

**类命名规范**

| 种类        | 示例              |
|-----------|-----------------|
| utils     | XXXUtil.java    |
| constants | XXXContant.java |
| enums     | XXXEnum.java    |

## Framework：应用内部的通用框架，比如拦截器、模板代码、扩展框架等
**程序架构组织**

| 组件       | 包名        | 功能   |
|----------|-----------|------|
| Framwork | aop       | 拦截器  |
| Framwork | exception | 异常处理 |



## Start：应用启用、全局配置等

# 3. 模块之间的依赖关系--待补充
<a href="#dummy"><img src="https://i.hd-r.cn/3dfcf454-6b9d-45ac-96ff-8c31a8601759.png" alt="struture" /></a>


# 4. 其他开发规范
## 4.1 Exception抛出
如果是开发者自行抛出的错误，更应该处理成业务错误 BizException，便于格式化出更友好的错误信息出来。

## 4.2 出参
- 如果是无需返回返回使用Cola框架的Response,
- 如果是单个概念返回使用Cola框架的SingleResponse,
- 如果是多个概念返回使用Cola框架的MultiResponse.
  - 如果概念是某个明确的对象，例如MultiResponse<XXXDTO>

## 4.3 错误码规范
一个错误码编码只能用一次。不允许重复编码！不允许重复编码！不允许重复编码！
错误码编码规则：
- 第一块：SYS表示系统级错误，BIZ表示业务级错误
- 第二块：TRACE METRIC LOG USER AGENT
- 第三块：错误类型描述
  - 数据库错误 SQL
  - 参数错误 PARAM
  - 业务错误 XXXX
  - …… 待完善
  - 其他错误 unknown