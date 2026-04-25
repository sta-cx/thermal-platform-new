package org.sdkj.thermal.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.*;
import org.sdkj.thermal.domain.dto.PrHeatVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.mapper.*;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatArchiveService;
import org.sdkj.thermal.service.IPrOptionsHeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 房屋热表配表 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrHeatArchiveServiceImpl extends ServiceImpl<PrHeatArchiveMapper, PrHeatArchive> implements IPrHeatArchiveService {

    private final PrHeatArchiveMapper baseMapper;
    private final IHtTasksPerformService htTasksPerformService;
    private final IPrOptionsHeatService prOptionsHeatService;
    private final PrTransactionRecordMapper prTransactionRecordMapper;
    private final PrTransactionRecordSubMapper prTransactionRecordSubMapper;

    @Override
    public PrHeatArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                          String unitCode, String search, String archiveId,
                                                          PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        // buildingId and unitCode are not direct fields on PrHeatArchive;
        // they are used for frontend filtering and may be resolved through house/building relations
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(StringUtils.isNotBlank(archiveId), PrHeatArchive::getArchiveId, archiveId);
        // isDeleted is handled by @TableLogic automatically, no need to filter
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<PrHeatArchiveVo> queryCompanyHeat(String companyId) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatArchive::getCompanyId, companyId);
        // isDeleted handled by @TableLogic
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatArchive entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(java.util.Collection<?> ids) {
        return super.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replaceHeatMeter(PrHeatArchive newHeatArchive) {
        // 获取旧表信息
        PrHeatArchive oldHeatArchive = getById(newHeatArchive.getId());
        if (oldHeatArchive == null) {
            throw new RuntimeException("旧表信息不存在");
        }

        Long creater = LoginHelper.getUserId();
        Date date = new Date();
        BigDecimal zero = BigDecimal.ZERO;

        // 更新旧表信息
        oldHeatArchive.setUpdateBy(creater);
        oldHeatArchive.setCurrentReading(newHeatArchive.getCurrentReading());
        oldHeatArchive.setTotalUsed(newHeatArchive.getTotalUsed());
        oldHeatArchive.setTotalMoney(newHeatArchive.getTotalMoney());
        oldHeatArchive.setIsChanged(1);

        boolean type = false;
        // 当前余额
        if (newHeatArchive.getCurrentBalance().compareTo(zero) >= 0) {
            oldHeatArchive.setCurrentBalance(zero);
            newHeatArchive.setTotalRecharge(newHeatArchive.getCurrentBalance());
            type = true;
        } else {
            oldHeatArchive.setCurrentBalance(newHeatArchive.getCurrentBalance());
            newHeatArchive.setTotalRecharge(zero);
            newHeatArchive.setCurrentBalance(zero);
        }

        // 更新旧表
        updateById(oldHeatArchive);

        // 新表信息
        newHeatArchive.setId(IdUtil.simpleUUID());
        newHeatArchive.setCurrentReading(zero);
        newHeatArchive.setTotalMoney(zero);
        newHeatArchive.setTotalUsed(zero);
        newHeatArchive.setCreateBy(creater);
        newHeatArchive.setCreateTime(date);

        // 保存新表
        save(newHeatArchive);

        // 余额转移生成交易记录
        if (type && newHeatArchive.getCurrentBalance() != null
                && newHeatArchive.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
            String serialNum = DateUtil.format(date, "yyyyMMddHHmmss")
                + String.format("%06d", new Random().nextInt(1000000));
            PrTransactionRecord record = new PrTransactionRecord();
            record.setId(IdUtil.simpleUUID());
            record.setSerialNum(serialNum);
            record.setTransactionType(3);
            record.setPaymentType(4);
            record.setAmount(newHeatArchive.getCurrentBalance());
            record.setPaidAmount(newHeatArchive.getCurrentBalance());
            record.setStatus(0);
            record.setHouseId(oldHeatArchive.getHouseId());
            record.setOrgId(oldHeatArchive.getOrgId());
            record.setCompanyId(newHeatArchive.getCompanyId());
            record.setOperatorId(String.valueOf(creater));
            record.setTransactionTime(date);
            record.setNotes("换表余额转移");
            record.setCreateBy(creater);
            record.setCreateTime(date);
            prTransactionRecordMapper.insert(record);

            PrTransactionRecordSub sub = new PrTransactionRecordSub();
            sub.setId(IdUtil.simpleUUID());
            sub.setMainId(record.getId());
            sub.setAmount(newHeatArchive.getCurrentBalance());
            sub.setBalanceBefore(BigDecimal.ZERO);
            sub.setBalanceAfter(newHeatArchive.getCurrentBalance());
            sub.setHouseId(oldHeatArchive.getHouseId());
            sub.setCreateBy(creater);
            sub.setCreateTime(date);
            prTransactionRecordSubMapper.insert(sub);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object recharge(PrHeatArchive heatArchive, String paymentMethod) {
        Long userId = LoginHelper.getUserId();
        String companyId = LoginHelper.getTenantId();
        Date now = new Date();

        PrHeatArchive archive = getById(heatArchive.getId());
        if (archive == null) {
            throw new RuntimeException("配表记录不存在");
        }

        String serialNum = DateUtil.format(now, "yyyyMMddHHmmss")
            + String.format("%06d", new Random().nextInt(1000000));

        BigDecimal rechargeAmount = heatArchive.getCurrentBalance() != null
            ? heatArchive.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal currentBalance = (archive.getCurrentBalance() != null ? archive.getCurrentBalance() : BigDecimal.ZERO)
            .add(rechargeAmount);

        archive.setTotalRecharge((archive.getTotalRecharge() != null ? archive.getTotalRecharge() : BigDecimal.ZERO)
            .add(rechargeAmount));
        archive.setCurrentBalance(currentBalance);
        archive.setUpdateBy(userId);
        updateById(archive);

        PrTransactionRecord record = new PrTransactionRecord();
        record.setId(IdUtil.simpleUUID());
        record.setSerialNum(serialNum);
        record.setTransactionType(1);
        record.setPaymentType(Integer.parseInt(paymentMethod != null ? paymentMethod : "1"));
        record.setAmount(rechargeAmount);
        record.setPaidAmount(rechargeAmount);
        record.setStatus(0);
        record.setHouseId(archive.getHouseId());
        record.setOrgId(archive.getOrgId());
        record.setCompanyId(companyId);
        record.setOperatorId(String.valueOf(userId));
        record.setTransactionTime(now);
        record.setCreateBy(userId);
        record.setCreateTime(now);
        prTransactionRecordMapper.insert(record);

        PrTransactionRecordSub sub = new PrTransactionRecordSub();
        sub.setId(IdUtil.simpleUUID());
        sub.setMainId(record.getId());
        sub.setAmount(rechargeAmount);
        sub.setBalanceBefore(archive.getCurrentBalance().subtract(rechargeAmount));
        sub.setBalanceAfter(currentBalance);
        sub.setHouseId(archive.getHouseId());
        sub.setCreateBy(userId);
        sub.setCreateTime(now);
        prTransactionRecordSubMapper.insert(sub);

        log.info("仪表充值完成，表号：{}，金额：{}，流水号：{}", archive.getMeterNum(), rechargeAmount, serialNum);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean manualControl(List<PrHeatVo> prHeatVoList, boolean switch1, Integer scale, String adjust,
                                  String orgId, String companyId, String intervall, String unit, String duration) {
        // 获取调控配置
        PrOptionsHeat prOptionsHeat = prOptionsHeatService.getDataById(orgId, companyId, "2");
        if (prOptionsHeat == null) {
            throw new RuntimeException("未找到调控配置");
        }

        Integer min = prOptionsHeat.getControlMin();
        Integer max = prOptionsHeat.getControlMax();
        Long create = LoginHelper.getUserId();

        // TODO: 提取各种类型的仪表信息
        // PrHeatValveArchive, PrHeatCommandValveArchive, PrHeatHotArchive, PrHeatDtuArchive

        List<HtTasksPerform> htTasksPerformList = new ArrayList<>();

        // 根据不同的调控类型创建任务
        // adjust = "1": 开度调节
        // adjust = "2": 开关控制
        // adjust = "3": 开度设定
        // adjust = "4": 状态查询
        // adjust = "5": 制动
        // adjust = "6": 上报周期调整
        // adjust = "7": 热表状态查询
        // adjust = "51": 特殊指令
        // adjust = "28-1": 修改设备信道(阀门)
        // adjust = "28-2": 修改设备信道(DTU)
        // adjust = "27": 读取信道
        // adjust = "29": 打开网关
        // adjust = "30": 关闭网关

        // TODO: 创建调控任务
        // 根据 adjust 类型创建不同的 HtTasksPerform

        if (!htTasksPerformList.isEmpty()) {
            htTasksPerformService.saveBatchTasks(htTasksPerformList);

            // 根据不同类型执行不同的任务
            if ("7".equals(adjust)) {
                try {
                    htTasksPerformService.executeHeatMeterTasks(htTasksPerformList);
                } catch (Exception e) {
                    log.error("执行热表调控指令失败", e);
                    return false;
                }
            } else if ("28-2".equals(adjust) || "27".equals(adjust) || "29".equals(adjust) || "30".equals(adjust)) {
                try {
                    htTasksPerformService.executeDtuControlTasks(htTasksPerformList);
                } catch (Exception e) {
                    log.error("执行 DTU 调控指令失败", e);
                    return false;
                }
            } else {
                try {
                    htTasksPerformService.executeValveControlTasks(htTasksPerformList);
                    htTasksPerformService.insertValveOCLog(htTasksPerformList);
                } catch (Exception e) {
                    log.error("执行阀门调控指令失败", e);
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public TableDataInfo<PrHeatArchiveVo> realTimeData(String companyId, String orgId, String buildingId,
                                                        String unitCode, String search, PageQuery pageQuery) {
        // TODO: 实现实时数据查询
        // 需要关联查询多种仪表类型的实时数据
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatArchiveVo> zonghe(String companyId, String orgId, String buildingId,
                                                  String unitCode, String search, String moneyType,
                                                  String valveStatus, PageQuery pageQuery) {
        // TODO: 实现综合查询
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xunce(List<PrHeatVo> prHeatVoList, String orgId, String companyId) {
        // TODO: 实现热表巡测
        log.info("执行热表巡测，数量：{}", prHeatVoList.size());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setValveGroupParam(List<PrHeatVo> prHeatVoList, String commandParam, String orgId, String companyId) {
        // TODO: 实现设置阀门组号
        log.info("设置阀门组号，组号：{}，数量：{}", commandParam, prHeatVoList.size());
        return true;
    }

    @Override
    public List<PrHeatArchiveVo> findMeter(String search, String companyId) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatArchive::getCompanyId, companyId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.last("limit 20");
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public BigDecimal calculate(String id) {
        PrHeatArchive archive = getById(id);
        if (archive == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal current = archive.getCurrentReading() != null ? archive.getCurrentReading() : BigDecimal.ZERO;
        BigDecimal start = archive.getStartReading() != null ? BigDecimal.valueOf(archive.getStartReading()) : BigDecimal.ZERO;
        if (current.compareTo(start) < 0) {
            return BigDecimal.ZERO;
        }
        return current.subtract(start);
    }

    @Override
    public List<PrHeatArchiveVo> exportAll(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importData(String uuid) {
        String companyId = LoginHelper.getTenantId();
        String create = LoginHelper.getUserIdStr();
        log.info("导入配表数据，批次号：{}，公司：{}，用户：{}", uuid, companyId, create);
        // 导入逻辑由 Mapper XML 的 importData SQL 实现
        return true;
    }

    @Override
    public List<PrHeatArchiveVo> selectReport(String companyId, String orgId, String buildingId,
                                              String unitCode, String startTime, String endTime, String search) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatArchiveVo> selectMeterReport(String companyId, String orgId, String buildingId,
                                                   String unitCode, String startTime, String endTime, String search) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }
}
