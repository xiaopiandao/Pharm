# 医务室药品管理系统（Spring Boot）

## 功能清单
- 药品档案管理（含条码）
- 入库管理（扫码）
- 出库管理（按近效期优先 FEFO）
- 库存流水追踪
- 低库存与近效期预警

## 运行方式

### 1）本地快速体验（推荐）
使用内存数据库 H2，不依赖 MySQL：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

启动后可访问：
- 前端页面: `http://localhost:8080/`
- Swagger: `http://localhost:8080/swagger-ui.html`
- H2 控制台: `http://localhost:8080/h2-console`

### 2）生产/联调方式
1. 创建 MySQL 数据库 `pharm`
2. 修改 `src/main/resources/application.yml` 中数据库账号密码
3. 运行：
   ```bash
   mvn spring-boot:run
   ```

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

### 3. 扫码入库
```bash
curl -X POST http://localhost:8080/api/inbounds \
  -H 'Content-Type: application/json' \
  -d '{
    "supplierName":"示例供应商",
    "items":[
      {
        "scanCode":"6901234567890",
        "batchNo":"BATCH-001",
        "productionDate":"2026-01-01",
        "expiryDate":"2028-01-01",
        "purchasePrice":12.50,
        "quantity":100
      }
    ]
  }'
```

### 4. 扫码出库
```bash
curl -X POST http://localhost:8080/api/outbounds \
  -H 'Content-Type: application/json' \
  -d '{
    "receiver":"医务室A",
    "items":[
      {
        "scanCode":"6901234567890",
        "quantity":10
      }
    ]
  }'
```

### 5. 预警查询
```bash
curl http://localhost:8080/api/warnings/low-stock
curl http://localhost:8080/api/warnings/expiring?days=90
```
