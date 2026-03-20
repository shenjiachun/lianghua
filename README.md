# 量化分析系统 (Lianghua)

基于 **COLA 框架** + **DDD 架构** 的量化分析系统，集成阿里云通义千问 & 字节跳动豆包大模型，提供智能化 A 股量化分析能力。

---

## 系统架构

本系统严格遵循 **DDD（领域驱动设计）** 思想，基于阿里巴巴开源的 **COLA（Clean Object-oriented & Layered Architecture）** 框架构建，采用经典的六层模块化结构：

```
lianghua/
├── lianghua-client/         # 客户端层：API 接口定义、DTO、Command、Query
├── lianghua-domain/         # 领域层：实体、值对象、领域服务、网关接口
├── lianghua-app/            # 应用层：CQRS 命令/查询服务实现
├── lianghua-infrastructure/ # 基础设施层：网关实现、持久化、AI API 调用
├── lianghua-adapter/        # 适配器层：REST 控制器
└── lianghua-start/          # 启动层：Spring Boot 入口、配置、数据库初始化
```

### 架构依赖关系

```
adapter → client
app → client + domain
infrastructure → domain
start → adapter + app + infrastructure
```

---

## 核心功能

### 1. 市场数据管理
- **实时行情同步**：支持从多数据源同步 A 股实时行情（Tushare、东方财富等）
- **K 线数据存储**：日线数据持久化，支持历史数据查询
- **行情查询**：按股票代码查询实时行情、列表分页查询

### 2. AI 量化分析（大模型集成）
支持两大 AI 平台：

| 平台 | 模型 | 申请地址 |
|------|------|---------|
| 阿里云通义千问 | qwen-turbo | https://dashscope.aliyun.com/ |
| 字节跳动豆包 | doubao-pro-4k | https://www.volcengine.com/product/doubao |

功能：
- **技术指标计算**：MA（5/10/20/60）、MACD、RSI(14)、布林带（Bollinger Bands）
- **AI 智能分析**：调用大模型对股票进行技术面 + 基本面综合分析
- **量化评分**：综合评分（0-100），自动生成投资建议（BUY/HOLD/SELL）
- **目标价预测**：AI 预测目标价位
- **风险提示**：自动生成个股风险提示

---

## 领域模型

### 市场数据领域（Market）
- **实体**：`MarketData`（市场数据）、`KLineData`（K 线数据）
- **值对象**：`StockSymbol`（股票代码，格式：`CODE.MARKET`，如 `000001.SZ`）
- **领域服务**：`MarketDataDomainService`（指标计算：MA、RSI、涨跌幅等）
- **网关接口**：`MarketDataGateway`（数据查询/持久化/外部数据源）

### 分析报告领域（Analysis）
- **实体**：`AnalysisReport`（分析报告）、`TechnicalIndicators`（技术指标快照）
- **值对象**：`AIProvider`（AI 提供商枚举）、`Recommendation`（投资建议枚举）
- **领域服务**：`AnalysisDomainService`（提示词构建、指标计算、报告创建）
- **网关接口**：`AIAnalysisGateway`（AI 大模型调用）、`AnalysisReportGateway`（报告持久化）

---

## REST API

### 市场数据接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/market/data/{symbol}` | 查询股票实时行情 |
| GET | `/api/v1/market/data/list` | 分页查询行情列表 |
| POST | `/api/v1/market/data/sync` | 同步市场数据 |

### 量化分析接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/analysis/report/{symbol}` | 获取最新分析报告 |
| GET | `/api/v1/analysis/report/list` | 分页查询分析报告 |
| POST | `/api/v1/analysis/report/generate` | 生成量化分析报告 |

#### 生成分析报告示例

```json
POST /lianghua/api/v1/analysis/report/generate
{
    "symbol": "000001.SZ",
    "aiProvider": "ALIYUN",
    "analysisDays": 30
}
```

响应示例：
```json
{
    "success": true,
    "data": {
        "reportId": "abc123...",
        "symbol": "000001.SZ",
        "stockName": "平安银行",
        "aiProvider": "ALIYUN",
        "overallScore": 72,
        "recommendation": "BUY",
        "targetPrice": 13.50,
        "technicalSummary": "均线多头排列，MACD 金叉，短期看好...",
        "fundamentalSummary": "市盈率合理，业绩稳健...",
        "riskWarnings": ["市场整体风险需关注", "注意止损位设置"],
        "createdAt": "2026-01-01T10:00:00"
    }
}
```

---

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+

### 配置 AI API Key

编辑 `lianghua-start/src/main/resources/application.yml` 或通过环境变量配置：

```bash
# 阿里云通义千问 API Key（申请：https://dashscope.aliyun.com/）
export ALIYUN_API_KEY=your-aliyun-api-key

# 字节跳动豆包 API Key（申请：https://www.volcengine.com/product/doubao）
export BYTEDANCE_API_KEY=your-bytedance-api-key
```

> **注意**：如果未配置 API Key，系统会自动返回模拟分析数据，方便本地开发调试。

### 构建运行

```bash
# 构建
mvn clean package -DskipTests

# 运行
java -jar lianghua-start/target/lianghua-start-1.0.0-SNAPSHOT.jar
```

服务启动后访问：
- API 服务：http://localhost:8080/lianghua
- H2 控制台（开发环境）：http://localhost:8080/lianghua/h2-console

---

## 技术栈

| 组件 | 技术选型 |
|------|---------|
| 框架 | Spring Boot 3.2 + COLA 4.3 |
| 架构 | DDD + CQRS |
| 持久化 | MyBatis-Plus + H2（开发）/ MySQL（生产） |
| HTTP 客户端 | OkHttp 4 |
| JSON | FastJSON2 |
| AI 大模型 | 阿里云通义千问 / 字节跳动豆包 |

---

## 项目结构详情

```
lianghua-client/
└── src/main/java/com/lianghua/client/
    ├── api/                    # 服务接口
    │   ├── IMarketDataQueryService.java
    │   ├── IMarketDataCmdService.java
    │   ├── IAnalysisReportQueryService.java
    │   └── IAnalysisReportCmdService.java
    ├── command/market/         # 命令对象（写操作）
    ├── query/                  # 查询对象（读操作）
    └── dto/                    # 数据传输对象

lianghua-domain/
└── src/main/java/com/lianghua/domain/
    ├── market/                 # 市场数据领域
    │   ├── model/entity/       # 领域实体
    │   ├── model/vo/           # 值对象
    │   ├── gateway/            # 网关接口
    │   └── service/            # 领域服务
    └── analysis/               # 分析报告领域
        ├── model/entity/
        ├── model/vo/
        ├── gateway/
        └── service/

lianghua-infrastructure/
└── src/main/java/com/lianghua/infrastructure/
    ├── market/                 # 市场数据基础设施
    │   ├── dataobject/         # 持久化对象（DO）
    │   ├── mapper/             # MyBatis Mapper
    │   └── gateway/            # 网关实现
    ├── ai/                     # AI 大模型集成
    │   ├── aliyun/             # 阿里云通义千问实现
    │   ├── bytedance/          # 字节跳动豆包实现
    │   └── AIAnalysisGatewayRouter.java  # 路由器（Strategy 模式）
    └── analysis/               # 分析报告基础设施
```
