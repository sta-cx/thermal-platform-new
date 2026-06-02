-- 监控模块测试种子数据（tenant_000000）
-- 目的: 让分区/换热站/分组(houseType=pay_sit_type) 筛选链端到端走通
-- 性质: 测试数据，非生产迁移脚本。生产回填见 docs/superpowers/specs/2026-05-31-monitor-data-backfill-followup.md
-- 现有: 16房屋(org 2053696230064996354 占13 / 2053696407974789122 占3)、4楼宇、14阀门档案(已关联house_id)

-- 1. 换热站（2个，分属两个 org，下拉 heatStationListByOrg 依赖 org_id）
INSERT INTO pr_heat_station (id, code, name, type, org_id, del_flag, create_time) VALUES
(2059000000000000001, 'HS-YG', '阳光花园换热站', '1', '2053696230064996354', '0', NOW()),
(2059000000000000002, 'HS-CH', '翠湖小区换热站', '1', '2053696407974789122', '0', NOW());

-- 2. 分区（3个，挂换热站，下拉 partitionListByStation 依赖 station_id）
INSERT INTO pr_heat_station_partition (id, station_id, name, del_flag, create_time) VALUES
(2059100000000000001, 2059000000000000001, '阳光A区分站', '0', NOW()),
(2059100000000000002, 2059000000000000001, '阳光B区分站', '0', NOW()),
(2059100000000000003, 2059000000000000002, '翠湖分站',   '0', NOW());

-- 3. 楼宇挂换热站（pr_building.station_id）
UPDATE pr_building SET station_id = 2059000000000000001
  WHERE id IN (2052928621044416514, 2054000000000000001, 2054000000000000002);
UPDATE pr_building SET station_id = 2059000000000000002
  WHERE id = 2054000000000000003;

-- 4. 房屋 station_type 指向分区（= 分区id，分区/换热站筛选的核心字段）
UPDATE pr_house SET station_type = '2059100000000000001'
  WHERE building_id IN (2052928621044416514, 2054000000000000001);  -- 测试楼宇1 + 阳光A栋 → 阳光A区分站
UPDATE pr_house SET station_type = '2059100000000000002'
  WHERE building_id = 2054000000000000002;                          -- 阳光B栋 → 阳光B区分站
UPDATE pr_house SET station_type = '2059100000000000003'
  WHERE building_id = 2054000000000000003;                          -- 翠湖C栋 → 翠湖分站

-- 5. 房屋 pay_sit_type（缴费位置属性 1孤岛/2上不供/3下不供/4正常）
--    错位映射 site_type 制造 1-4 多样分布，便于测试分组(houseType)筛选
UPDATE pr_house SET pay_sit_type = (CAST(site_type AS UNSIGNED) % 4) + 1
  WHERE site_type REGEXP '^[0-9]+$';

-- 6. 单元 station_id（= 分区id，与房屋 station_type 同源；F1 单元级筛选依赖）
--    映射与房屋一致：测试楼1+阳光A栋→阳光A区，阳光B栋→阳光B区，翠湖C栋→翠湖
UPDATE pr_unit SET station_id=2059100000000000001 WHERE building_id IN (2052928621044416514,2054000000000000001);
UPDATE pr_unit SET station_id=2059100000000000002 WHERE building_id=2054000000000000002;
UPDATE pr_unit SET station_id=2059100000000000003 WHERE building_id=2054000000000000003;

-- 7. 单元阀门档案（5 条，F1 单元级列表端到端验证用；幂等：先删固定 id 段）
DELETE FROM pr_heat_unit_valve_archive WHERE id BETWEEN 2059200000000000001 AND 2059200000000000005;
INSERT INTO pr_heat_unit_valve_archive (id, meter_num, meter_arc_name, unit_id, org_id, valve_status, is_changed, create_time) VALUES
(2059200000000000001,'UV-001','测试楼1-1单元阀',2052929096590409729,'2053696230064996354','1',0,NOW()),
(2059200000000000002,'UV-002','阳光A栋-1单元阀',2054100000000000001,'2053696230064996354','1',0,NOW()),
(2059200000000000003,'UV-003','阳光A栋-2单元阀',2054100000000000002,'2053696230064996354','2',0,NOW()),
(2059200000000000004,'UV-004','阳光B栋-1单元阀',2054100000000000003,'2053696230064996354','1',0,NOW()),
(2059200000000000005,'UV-005','翠湖C栋-1单元阀',2054100000000000004,'2053696407974789122','1',0,NOW());

-- 8. 户级阀门 voltage 阶梯 + valve_time（电压区间筛选验证用；valve_time=NOW 让 online 转正常）
UPDATE pr_heat_valve_archive SET voltage = CONCAT('3.', (id MOD 10)), valve_time = NOW()
  WHERE org_id = '2053696230064996354' AND is_changed = 0;
