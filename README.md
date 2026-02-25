# 量化分析系统 (LiangHua Quantitative Analysis System)

基于开源大模型（阿里通义千问、字节豆包）和市场数据的量化分析系统，采用 **COLA 框架** 和 **DDD（领域驱动设计）** 架构。

---

## 项目简介

本系统整合 A 股市场数据采集与 AI 大模型分析能力，为量化投资提供智能化支持。

**核心能力：**
- 📈 市场数据采集与存储（支持 A 股 K 线数据）
- 🤖 对接阿里通义千问（Qwen）和字节豆包（Doubao）大模型 API
- 📊 AI 驱动的趋势分析、技术分析、情绪分析、风险评估
- 💹 技术指标计算（移动均线 MA、RSI、MACD）
- 🏗️ 严格遵循 DDD 领域驱动设计和 COLA 架构

---

## 技术架构

### COLA 分层架构

```
├── lianghua-client          # 客户端 API 契约（DTO、Command、Query、接口定义）
├── lianghua-domain          # 领域层（聚合根、值对象、领域服务、防腐层接口）
├── lianghua-app             # 应用层（用例处理器：Command/Query Executor）
├── lianghua-adapter         # 适配器层（REST 控制器、全局异常处理）
├── lianghua-infrastructure  # 基础设施层（AI 客户端、数据库 Mapper、外部数据源）
└── lianghua-start           # 应用启动（Spring Boot 入口、配置）
```

### DDD 领域划分

**Market 领域（市场数据）**
- 聚合根：`MarketData`（行情数据）
- 值对象：`StockCode`（股票代码）、`Price`（价格）
- 领域服务：`MarketDataDomainService`（MA/RSI/MACD 计算）
- 防腐层：`MarketDataGateway`（持久化）、`MarketDataFetcherGateway`（外部数据获取）

**Analysis 领域（AI 分析）**
- 聚合根：`AnalysisResult`（分析结果）
- 值对象：`AnalysisType`（分析类型）、`AIModel`（AI 模型）、`TradingSuggestion`（交易建议）
- 领域服务：`AnalysisDomainService`（AI Prompt 构建与结果解析）
- 防腐层：`AnalysisResultGateway`（持久化）、`AIModelGateway`（AI 模型调用）

---

## 快速开始

### 环境要求

- Java 17+
- Maven 3.8+
- MySQL 8.0+（生产环境）

### 1. 初始化数据库

```sql
CREATE DATABASE lianghua DEFAULT CHARACTER SET utf8mb4;
```

执行 `lianghua-infrastructure/src/main/resources/db/schema.sql` 初始化表结构。

### 2. 配置应用

修改 `lianghua-start/src/main/resources/application.yml`，或通过环境变量配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lianghua?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

ai:
  aliyun:
    api-key: ${ALIYUN_API_KEY:}          # 阿里云灵积 API Key（通义千问）
  bytedance:
    api-key: ${BYTEDANCE_API_KEY:}       # 字节跳动火山引擎 API Key（豆包）
```

> 💡 **无需配置 API Key 即可运行**：未配置时系统自动使用模拟数据，方便本地开发调试。

### 3. 构建运行

```bash
# 构建
mvn clean package -DskipTests

# 运行
java -jar lianghua-start/target/lianghua-start-1.0.0-SNAPSHOT.jar
```

---

## API 接口

### 市场数据接口

| 方法   | 路径                          | 说明                   |
|--------|-------------------------------|------------------------|
| POST   | `/api/v1/market/fetch`        | 采集并保存市场行情数据 |
| GET    | `/api/v1/market/{stockCode}`  | 查询历史行情数据       |
| GET    | `/api/v1/market/{stockCode}/latest` | 查询最新行情     |

#### 示例：采集数据

```bash
curl -X POST http://localhost:8080/api/v1/market/fetch \
  -H "Content-Type: application/json" \
  -d '{
    "stockCode": "600519.SH",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "dataSource": "MANUAL"
  }'
```

### AI 分析接口

| 方法   | 路径                     | 说明                   |
|--------|--------------------------|------------------------|
| POST   | `/api/v1/analysis/analyze`    | 发起 AI 分析      |
| GET    | `/api/v1/analysis/history`    | 查询历史分析结果  |

#### 示例：发起 AI 分析

```bash
curl -X POST http://localhost:8080/api/v1/analysis/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "stockCode": "600519.SH",
    "analysisType": "TREND",
    "aiModel": "QWEN",
    "lookbackDays": 30
  }'
```

**分析类型（`analysisType`）：**
- `TREND` - 趋势分析
- `SENTIMENT` - 情绪分析
- `RISK` - 风险评估
- `TECHNICAL` - 技术分析
- `FUNDAMENTAL` - 基本面分析

**AI 模型（`aiModel`）：**
- `QWEN` - 通义千问 Plus
- `QWEN_TURBO` - 通义千问 Turbo
- `DOUBAO` - 豆包 Pro 32K
- `DOUBAO_LITE` - 豆包 Lite 32K

---

## AI 大模型接入

### 阿里通义千问（Qwen）

- API 端点：`https://dashscope.aliyuncs.com/compatible-mode/v1`
- 兼容 OpenAI API 格式
- 获取 API Key：[阿里云灵积模型服务](https://dashscope.aliyun.com/)

### 字节豆包（Doubao）

- API 端点：`https://ark.cn-beijing.volces.com/api/v3`
- 兼容 OpenAI API 格式
- 获取 API Key：[火山引擎方舟平台](https://www.volcengine.com/product/ark)

---

## 项目结构详解

```
lianghua/
├── lianghua-client/                          # API 契约层
│   └── src/main/java/com/lianghua/client/
│       ├── api/                              # 服务接口定义
│       │   ├── MarketDataServiceI.java
│       │   └── AnalysisServiceI.java
│       ├── command/                          # 写操作命令
│       │   ├── FetchMarketDataCmd.java
│       │   └── AnalyzeMarketCmd.java
│       ├── query/                            # 查询请求
│       │   ├── MarketDataQry.java
│       │   └── AnalysisResultQry.java
│       └── dto/                              # 数据传输对象
│           ├── MarketDataDTO.java
│           └── AnalysisResultDTO.java
│
├── lianghua-domain/                          # 领域层（核心业务）
│   └── src/main/java/com/lianghua/domain/
│       ├── market/                           # 市场数据领域
│       │   ├── model/
│       │   │   ├── entity/MarketData.java    # 聚合根
│       │   │   └── valobj/                   # 值对象
│       │   ├── gateway/                      # 防腐层接口
│       │   └── service/MarketDataDomainService.java
│       └── analysis/                         # AI 分析领域
│           ├── model/
│           │   ├── entity/AnalysisResult.java
│           │   └── valobj/                   # AIModel, AnalysisType, TradingSuggestion
│           ├── gateway/
│           └── service/AnalysisDomainService.java
│
├── lianghua-app/                             # 应用层（用例编排）
│   └── src/main/java/com/lianghua/app/
│       ├── command/                          # 命令处理器
│       ├── query/                            # 查询处理器
│       └── service/                          # 应用服务实现
│
├── lianghua-adapter/                         # 适配器层
│   └── src/main/java/com/lianghua/adapter/
│       └── web/                              # REST 控制器
│
├── lianghua-infrastructure/                  # 基础设施层
│   └── src/main/java/com/lianghua/infrastructure/
│       ├── ai/
│       │   ├── client/AliyunQwenClient.java  # 通义千问客户端
│       │   ├── client/DoubaoClient.java       # 豆包客户端
│       │   └── gateway/AIModelGatewayImpl.java
│       ├── market/
│       │   └── gateway/MarketDataFetcherGatewayImpl.java
│       └── persistence/                      # MyBatis 持久化
│           ├── do_/                          # 数据库对象
│           ├── mapper/                       # Mapper 接口
│           └── gateway/                      # 防腐层实现
│
└── lianghua-start/                           # 启动层
    └── src/main/
        ├── java/com/lianghua/LianghuaApplication.java
        └── resources/application.yml
```

---

## 开发指南

### 运行测试

```bash
mvn test
```

测试使用 H2 内存数据库，无需外部依赖。

### 添加新的 AI 模型

1. 在 `lianghua-domain` 的 `AIModel` 枚举中添加新模型
2. 在 `lianghua-infrastructure` 中创建对应的 HTTP 客户端
3. 在 `AIModelGatewayImpl` 中添加路由逻辑

### 添加新的市场数据源

1. 实现 `MarketDataFetcherGateway` 接口
2. 在 `FetchMarketDataCmd` 中添加新的 `dataSource` 类型
3. 注入新实现并按 `dataSource` 路由

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.3 | 应用框架 |
| COLA | 5.0.0 | 架构框架 |
| MyBatis | 3.0.3 | ORM 框架 |
| OkHttp | 4.12.0 | HTTP 客户端 |
| FastJSON2 | 2.0.47 | JSON 处理 |
| MySQL | 8.0+ | 生产数据库 |
| H2 | - | 测试数据库 |
| Lombok | - | 代码简化 |

---

## 免责声明

本系统仅供学习和研究使用，AI 分析结果不构成投资建议。投资有风险，入市需谨慎。