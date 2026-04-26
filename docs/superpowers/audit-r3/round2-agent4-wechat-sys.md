# Round 2 — Agent 4: 微信支付 + 系统管理 Service/DB 深度审核

## 审核日期
2026-04-26

## 审核范围
- 微信/支付模块 Service 层和 DB 层深度对比
- 系统管理模块 Service 层和 DB 层深度对比

---

## 一、微信/支付模块 Service 层对比

### 1.1 WechatPayService (微信支付核心服务)

#### 旧系统实现状态
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/WechatPayService.java`

**完整实现的方法**:
- `createOrder(PayParamVO)` - 创建微信支付订单，调用微信统一下单 API
- `handlePayNotify(HttpServletRequest)` - 处理支付回调，包含签名验证、幂等性处理
- `queryOrder(String)` - 查询订单状态
- `applyRefund(RefundParamVO)` - 申请退款
- `handleRefundNotify(HttpServletRequest)` - 处理退款回调
- `queryRefund(String)` - 查询退款状态
- `cancelExpiredOrders()` - 定时任务取消过期订单

**关键特性**:
- 使用 `com.github.wxpay.sdk.WXPay` SDK
- MD5 签名验证
- 回调幂等性处理（检查订单状态，避免重复处理）
- 金额验证（订单金额 vs 回调金额）
- 事务管理 `@Transactional`
- 支付成功后触发关联业务更新（交易记录、费用明细、房屋状态）

#### 新系统实现状态
**文件**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/WechatPayController.java`

**实现状态**: **SKELETON (骨架)**

**现状分析**:
- Controller 直接实现业务逻辑，无独立 Service 层
- 使用内存 Map (`LOCAL_ORDERS`) 存储订单数据，无持久化
- 无微信 SDK 集成
- 无签名验证逻辑
- 无幂等性处理
- 所有方法标记为 "Phase 6 待实现"

**代码示例**:
```java
// 骨架实现 - 使用内存存储
private static final Map<String, Map<String, Object>> LOCAL_ORDERS = new ConcurrentHashMap<>();

@PostMapping("/createOrder")
public R<Map<String, Object>> createOrder(@RequestBody Map<String, Object> payParam) {
    // 仅为演示，无实际支付功能
    String outTradeNo = "WX" + LocalDateTime.now().format(...);
    // Phase 6: 调用微信统一下单 API
    return R.fail("微信支付 Phase 6 实现");
}
```

**风险点**:
1. **RED - 支付安全缺失**: 无签名验证，存在伪造回调风险
2. **RED - 幂等性缺失**: 无重复通知处理机制，可能导致重复扣费
3. **RED - 数据持久化缺失**: 服务重启丢失订单数据
4. **ORANGE - 金额验证缺失**: 无金额校验，可能被篡改

---

### 1.2 WechatAuthService (微信授权服务)

#### 旧系统实现状态
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/WechatAuthService.java`

**完整实现的方法**:
- `buildAuthUrl(String, String)` - 构建微信授权 URL
- `getOpenIdByCode(String)` - 通过 code 获取 OpenID
- `bindWechatUser(String, String, String, String)` - 绑定微信用户与缴费码
- `getUserBindInfo(String)` - 查询用户绑定信息

**关键特性**:
- 调用微信 `/sns/oauth2/access_token` API
- 绑定关系持久化到 `PrWechatUser` 表
- 验证缴费码存在性
- 检查重复绑定

#### 新系统实现状态
**文件**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/WechatAuthController.java`

**实现状态**: **SKELETON (骨架)**

**现状分析**:
- 仅有 `getAuthUrl` 方法实现
- `callback` 方法仅记录日志，无实际处理
- `bindWechatUser` 和 `getUserInfo` 标记为 Phase 6
- 无 Service 层
- 无数据库表

**风险点**:
1. **ORANGE - 授权流程不完整**: 无法完成 OAuth2 授权流程
2. **ORANGE - 用户数据无法持久化**: 无绑定记录表

---

### 1.3 WechatUserService (微信用户服务)

#### 旧系统实现状态
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/WechatUserService.java`

**完整实现的方法**:
- `getUserByOpenId(String)` - 通过 OpenID 查询用户
- `bindWechatUser(String, String, String, String)` - 绑定用户
- `getUserBindStatus(String)` - 获取绑定状态
- `unbindWechatUser(String, String)` - 解除绑定

#### 新系统实现状态
**实现状态**: **MISSING**

**现状**: 新系统中无对应的 Service 或 Domain 类。

---

### 1.4 WxPortal (微信消息入口)

#### 旧系统实现状态
**功能**: 微信公众号消息接收与处理

**关键特性**:
- SHA-1 签名验证
- XML 消息解析
- 消息路由（文本/图片/事件）

#### 新系统实现状态
**文件**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/WxPortalController.java`

**实现状态**: **PARTIAL (部分实现)**

**已实现**:
- GET 端点签名验证逻辑完整
- XML 解析工具方法

**未实现**:
- 消息路由逻辑仅包含占位符
- 无具体消息处理

---

## 二、系统管理模块 Service 层对比

### 2.1 用户管理

#### 旧系统
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/SysUserController.java`

**功能范围**:
- 物业员工管理（非系统管理员）
- 数据权限管理（小区级别）
- 角色分配
- 密码修改
- 仪表数量统计
- 微信 OpenID 查询

#### 新系统
**文件**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/controller/system/SysUserController.java`

**功能范围**:
- 系统用户管理（管理员）
- 部门管理
- 角色授权
- 数据权限
- 导入/导出

**差异分析**:
1. **用户类型不同**: 旧系统管理物业员工，新系统管理平台管理员
2. **数据权限粒度**: 旧系统按小区划分，新系统按部门划分
3. **缺失功能**:
   - 仪表数量统计 (`getMeterNum`)
   - 授权文件下载 (`queryDecryption`)
   - 微信 OpenID 查询 (`getDataByWXOpenId`)

**迁移说明**: 旧系统的物业员工管理功能已迁移到 `AgUserController` (代理商用户管理)，但需验证完整性。

---

### 2.2 公司管理

#### 旧系统
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/SysCompanyController.java`

**功能范围**:
- 商家档案管理
- 自动创建关联用户和角色
- 商家详情（富文本 + OSS 图片）
- 删除前验证（检查名下商品）

#### 新系统
**文件**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AgCompanyController.java`

**实现状态**: **SKELETON**

**缺失功能**:
- 详情编辑/查看
- 删除验证
- 自动创建用户逻辑

---

### 2.3 首页数据统计

#### 旧系统
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/SysHomeService.java`

**功能范围**:
- 7 个异步数据查询方法
- 涵盖小区、楼宇、房屋、用户、设备、费用等统计

**统计维度**:
- 小区数、楼宇数、房屋数、用户数、供暖面积
- 阀门/热表/温采器数量及状态
- 平均温度、平均开度、累计热量
- 缴费/欠费/空置/停供统计

#### 新系统
**实现状态**: **MISSING**

**影响**: 首页仪表盘功能完全缺失。

---

### 2.4 字典管理

#### 旧系统
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/SysDictService.java`

#### 新系统
**文件**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/service/ISysDictTypeService.java`

**迁移状态**: **COMPLETE (由基座提供)**

`sdkj-system` 基座已提供完整的字典管理功能。

---

### 2.5 菜单管理

#### 旧系统
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/MenuService.java`

#### 新系统
**文件**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/service/ISysMenuService.java`

**迁移状态**: **COMPLETE (由基座提供)**

---

### 2.6 角色管理

#### 旧系统
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/RoleService.java`

#### 新系统
**文件**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AgRoleController.java`

**迁移状态**: **PARTIAL**

部分功能迁移到 `AgRoleController`，部分由 `sdkj-system` 基座提供。

---

## 三、DB 层对比

### 3.1 微信相关 Entity

| 旧系统 Entity | 新系统对应 | 状态 |
|--------------|-----------|------|
| `PrWechatUser` | 无 | MISSING |
| `PrWechatOrder` | 无 | MISSING |
| `PrWechatRefund` | 无 | MISSING |
| `PrWechatBindRecord` | 无 | MISSING (Controller 存在但无表) |
| `PrWechatBill` | 无 | MISSING |
| `PrWechatPaymentRecord` | 无 | MISSING |
| `PrWechatConfig` | 无 | MISSING |

**旧系统表结构** (`PrWechatUser`):
```java
@TableName("pr_wechat_user")
public class PrWechatUser {
    private String id;
    private String openId;
    private String otherCode;  // 供热缴费码
    private String houseId;
    private String userName;
    private String phone;
    private Integer bindStatus;  // 0-未绑定，1-已绑定
    private String sessionKey;
    private String unionId;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

**新系统**: 无对应的 Domain 类，数据库表未创建。

---

### 3.2 系统管理相关 Entity

| 旧系统 Entity | 新系统对应 | 状态 |
|--------------|-----------|------|
| `SysUser` | `SysUser` (sdkj-system) | COMPLETE |
| `SysCompany` | `AgCompany` | PARTIAL |
| `SysOrganization` | `SysDept` (sdkj-system) | COMPLETE |
| `SysRole` | `SysRole` (sdkj-system) + `AgRole` | PARTIAL |
| `SysDict` | `SysDictType` + `SysDictData` | COMPLETE |
| `Menu` | `SysMenu` | COMPLETE |

---

## 四、Mapper 层对比

### 4.1 微信支付 Mapper

#### 旧系统
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/mapper/PrWechatOrderMapper.java`

**关键方法**:
```java
PrWechatOrder selectByOutTradeNo(String outTradeNo);
PrWechatOrder selectByTransactionId(String transactionId);
List<PrWechatOrder> selectExpiredOrders(Integer pendingStatus, LocalDateTime now);
Integer insertPrTransactionRecordByWechat(...);  // 支付成功后插入交易记录
Integer updatePrExponseByWechat(...);  // 更新费用明细
Integer updatePrHouse(String houseId);  // 更新房屋状态
```

**业务关联**: 支付成功后自动触发三个业务更新。

#### 新系统
**实现状态**: **MISSING**

---

### 4.2 微信用户 Mapper

#### 旧系统
**文件**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/mapper/PrWechatUserMapper.java`

**关键方法**:
```java
PrWechatUser selectByOpenId(String openId);
PrWechatUser selectByOpenIdAndOtherCode(String openId, String otherCode);
```

#### 新系统
**实现状态**: **MISSING**

---

## 五、风险标注

### RED 级别 (严重影响功能或安全)

1. **微信支付无签名验证**
   - 位置: `WechatPayController.handlePayNotify()`
   - 风险: 攻击者可伪造支付成功回调，造成资金损失
   - 旧系统实现: `WXPayUtil.isSignatureValid(resultMap, wechatPayConfig.getKey())`
   - 新系统状态: 完全缺失

2. **支付回调无幂等性处理**
   - 位置: `WechatPayController.handlePayNotify()`
   - 风险: 微信重复回调可能导致重复扣费或业务数据异常
   - 旧系统实现: 检查订单状态，已支付订单直接返回成功
   - 新系统状态: 无检查逻辑

3. **订单数据无持久化**
   - 位置: `WechatPayController` 全局
   - 风险: 服务重启丢失所有订单数据，用户已支付但系统无记录
   - 旧系统实现: 持久化到 `pr_wechat_order` 表
   - 新系统状态: 仅内存存储

4. **支付金额无验证**
   - 位置: `WechatPayController.handlePayNotify()`
   - 风险: 回调金额可被篡改
   - 旧系统实现: 对比订单金额与回调金额
   - 新系统状态: 无验证

### ORANGE 级别 (影响功能完整性)

1. **微信授权流程不完整**
   - 位置: `WechatAuthController`
   - 影响: 用户无法完成微信授权登录

2. **首页数据统计缺失**
   - 位置: `SysHomeController`
   - 影响: 首页仪表盘无法显示

3. **微信用户绑定无持久化**
   - 位置: `WechatAuthController.bindWechatUser()`
   - 影响: 用户绑定关系丢失

---

## 六、建议

### 6.1 微信/支付模块

1. **立即创建数据库表**:
   - `pr_wechat_user` - 微信用户绑定
   - `pr_wechat_order` - 支付订单
   - `pr_wechat_refund` - 退款记录
   - `pr_wechat_config` - 微信配置

2. **创建 Service 层**:
   - `IWechatPayService` - 支付服务
   - `IWechatAuthService` - 授权服务
   - `IWechatUserService` - 用户服务

3. **集成微信 SDK**:
   - 添加 `github.weixinpay.sdk` 依赖
   - 实现 `WechatPayConfig` 配置类
   - 实现 MD5/HMAC-SHA256 签名验证

4. **实现安全机制**:
   - 回调签名验证
   - 回调幂等性处理
   - 金额验证
   - 订单过期定时任务

### 6.2 系统管理模块

1. **首页数据统计**:
   - 评估是否需要迁移旧系统首页功能
   - 如需迁移，创建 `SysHomeService` 和对应 Controller

2. **代理商管理完善**:
   - 补充 `AgCompanyController` 的详情编辑/查看功能
   - 补充删除验证逻辑

3. **物业员工管理**:
   - 确认 `AgUserController` 是否完全覆盖旧系统 `SysUserController` 功能
   - 补充缺失的仪表统计、授权文件等功能

---

## 七、数据表缺失清单

### 需要创建的表

1. **微信相关**:
   - `pr_wechat_user`
   - `pr_wechat_order`
   - `pr_wechat_refund`
   - `pr_wechat_bind_record`
   - `pr_wechat_config`

2. **系统管理相关**:
   - 确认是否需要 `sys_home_*` 统计表

### 表结构参考

参考旧系统 Entity 创建表结构，注意:
- 使用 `id` BIGINT 自增主键（新系统规范）
- 添加 `tenant_id` 字段（多租户支持）
- 添加 `create_by`, `create_time`, `update_by`, `update_time` 审计字段
- 逻辑删除使用 `del_flag` 而非 `is_deleted`

---

## 八、总结

### 微信/支付模块
- **Service 层**: 0% 迁移（全部为骨架或缺失）
- **DB 层**: 0% 迁移（无对应表和 Domain）
- **风险**: RED 级别安全问题严重，不建议上线

### 系统管理模块
- **Service 层**: 70% 迁移（由 sdkj-system 基座提供）
- **DB 层**: 80% 迁移（核心表已存在）
- **缺失**: 首页数据统计、部分代理商功能

### 优先级
1. **P0**: 微信支付安全机制（签名验证、幂等性）
2. **P0**: 微信相关数据库表创建
3. **P1**: 微信授权流程完整实现
4. **P2**: 首页数据统计评估与迁移

---

## 审核人
Agent 4 (自动审核)

## 附件
- 旧系统微信支付 Service: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/WechatPayService.java`
- 旧系统微信用户 Entity: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/entity/PrWechatUser.java`
- 新系统基座模块: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-system/`
