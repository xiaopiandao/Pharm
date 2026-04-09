# 医务室药品管理系统（Spring Boot + MySQL）

## 功能清单
- 药品档案管理（含条码）
- 入库管理（扫码）
- 出库管理（按近效期优先 FEFO）
- 库存流水追踪
- 低库存与近效期预警
- 前端演示页面（`/`）

## 一键启动 MySQL（推荐）

```bash
docker compose up -d
```

默认数据库信息：
- host: `localhost`
- port: `3306`
- db: `pharm`
- user: `root`
- password: `root`

## 启动后端（MySQL）

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

> `local` 配置也已改为 MySQL，不再使用 H2。

启动后访问：
- 前端页面: `http://localhost:8080/`
- Swagger: `http://localhost:8080/swagger-ui.html`

## 环境变量（可选覆盖）
- `MYSQL_HOST`（默认 `localhost`）
- `MYSQL_PORT`（默认 `3306`）
- `MYSQL_DB`（默认 `pharm`）
- `MYSQL_USER`（默认 `root`）
- `MYSQL_PASSWORD`（默认 `root`）

## 接口体验示例

### 1. 新增药品
```bash
curl -X POST http://localhost:8080/api/drugs \
  -H 'Content-Type: application/json' \
  -d '{
    "code":"D001",
    "name":"布洛芬片",
    "genericName":"布洛芬",
    "spec":"0.2g*24片",
    "unit":"盒",
    "manufacturer":"XX制药",
    "barcode":"6901234567890",
    "barcodeType":"EAN-13",
    "lowStockThreshold":20
  }'
```

### 2. 扫码查询药品
```bash
curl http://localhost:8080/api/drugs/by-barcode/6901234567890
```
