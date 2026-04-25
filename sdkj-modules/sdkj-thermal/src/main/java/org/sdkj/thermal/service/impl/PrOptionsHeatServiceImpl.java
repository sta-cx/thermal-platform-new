package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.thermal.domain.PrOptionsHeat;
import org.sdkj.thermal.mapper.PrOptionsHeatMapper;
import org.sdkj.thermal.service.IPrOptionsHeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * 供热系统选项 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrOptionsHeatServiceImpl extends ServiceImpl<PrOptionsHeatMapper, PrOptionsHeat>
        implements IPrOptionsHeatService {

    private final PrOptionsHeatMapper baseMapper;

    @Override
    public PrOptionsHeat getByOrgAndCompany(String orgId, String companyId, String level) {
        // 确定级别：如果 orgId 为空或为 "0"，则为公司级别（0），否则为小区级别（1）
        String actualLevel = (StringUtils.isBlank(orgId) || "0".equals(orgId)) ? "0" : "1";

        if ("0".equals(actualLevel)) {
            // 公司级别配置
            return baseMapper.selectByCompanyId(companyId);
        } else {
            // 小区级别配置
            PrOptionsHeat options = baseMapper.selectByOrgAndCompany(orgId, companyId);
            if (options == null) {
                // 如果小区没有配置，尝试从公司级别获取默认配置
                PrOptionsHeat companyOptions = baseMapper.selectByCompanyId(companyId);
                if (companyOptions != null) {
                    // 复制公司配置作为小区配置的默认值
                    options = new PrOptionsHeat();
                    options.setId(UUID.randomUUID().toString().replace("-", ""));
                    options.setCompanyId(companyId);
                    options.setOrgId(orgId);
                    options.setLevel("1");

                    // 设置默认的开关阀时间
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime openDateTime = LocalDateTime.parse("2020-11-01 00:00:00", formatter);
                    LocalDateTime closeDateTime = LocalDateTime.parse("2021-04-01 23:59:59", formatter);
                    options.setOpenTime(Date.from(openDateTime.atZone(ZoneId.systemDefault()).toInstant()));
                    options.setCloseTime(Date.from(closeDateTime.atZone(ZoneId.systemDefault()).toInstant()));
                    options.setOpenEarlyDays(0);
                    options.setCloseLaterDays(0);

                    // 复制公司配置的其他字段
                    copyProperties(companyOptions, options);

                    // 保存小区配置
                    save(options);
                    options = baseMapper.selectByOrgAndCompany(orgId, companyId);
                }
            }
            return options;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initData(String orgId, String companyId) {
        // 检查是否已存在配置
        PrOptionsHeat existing = baseMapper.selectByOrgAndCompany(orgId, companyId);
        if (existing != null) {
            // 如果存在，删除后重新初始化
            baseMapper.deleteByOrgAndCompany(orgId, companyId);
        }

        // 创建新配置
        PrOptionsHeat options = new PrOptionsHeat();
        options.setId(UUID.randomUUID().toString().replace("-", ""));
        options.setCompanyId(companyId);
        options.setOrgId(orgId);
        options.setLevel(StringUtils.isBlank(orgId) ? "0" : "1");

        // 设置默认的开关阀时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime openDateTime = LocalDateTime.parse("2020-11-01 00:00:00", formatter);
        LocalDateTime closeDateTime = LocalDateTime.parse("2021-04-01 23:59:59", formatter);
        options.setOpenTime(Date.from(openDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        options.setCloseTime(Date.from(closeDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        options.setOpenEarlyDays(0);
        options.setCloseLaterDays(0);

        return save(options);
    }

    /**
     * 复制属性（排除 ID、orgId、level 等字段）
     */
    private void copyProperties(PrOptionsHeat source, PrOptionsHeat target) {
        target.setMoneyCharge(source.getMoneyCharge());
        target.setAutoClose(source.getAutoClose());
        target.setAutoSms(source.getAutoSms());
        target.setOpenEarlyDays(source.getOpenEarlyDays());
        target.setCloseLaterDays(source.getCloseLaterDays());
        target.setScale(source.getScale());
        target.setIsEnable(source.getIsEnable());
        target.setQuittanceTitle(source.getQuittanceTitle());
        target.setStartSerial(source.getStartSerial());
        target.setSerialLength(source.getSerialLength());
        target.setLetterPrefix(source.getLetterPrefix());
        target.setSerialPrefix(source.getSerialPrefix());
        target.setRoundMode(source.getRoundMode());
        target.setDefine1(source.getDefine1());
        target.setDefine2(source.getDefine2());
        target.setControlMin(source.getControlMin());
        target.setControlMax(source.getControlMax());
        target.setRegulation(source.getRegulation());
        target.setRegulationNum(source.getRegulationNum());
        target.setCommandNum(source.getCommandNum());
        target.setIntervalTime(source.getIntervalTime());
        target.setTeleApiKey(source.getTeleApiKey());
        target.setTeleAppKey(source.getTeleAppKey());
        target.setTeleAppSecret(source.getTeleAppSecret());
        target.setTeleProductId(source.getTeleProductId());
        target.setService(source.getService());
        target.setStride(source.getStride());
        target.setWdbjx(source.getWdbjx());
        target.setWdbjd(source.getWdbjd());
        target.setSwbjx(source.getSwbjx());
        target.setSwbjd(source.getSwbjd());
        target.setKzwclcs(source.getKzwclcs());
        target.setKzsbcs(source.getKzsbcs());
        target.setHouseMin(source.getHouseMin());
        target.setHouseMax(source.getHouseMax());
        target.setHouseSmallColor(source.getHouseSmallColor());
        target.setHouseMediumColor(source.getHouseMediumColor());
        target.setHouseBigColor(source.getHouseBigColor());
        target.setBackWaterMin(source.getBackWaterMin());
        target.setBackWaterMax(source.getBackWaterMax());
        target.setBackWaterSmallColor(source.getBackWaterSmallColor());
        target.setBackWaterMediumColor(source.getBackWaterMediumColor());
        target.setBackWaterBigColor(source.getBackWaterBigColor());
        target.setFloorViewCompleteColor(source.getFloorViewCompleteColor());
        target.setFloorViewNoCompleteColor(source.getFloorViewNoCompleteColor());
        target.setFloorViewAbnormalColor(source.getFloorViewAbnormalColor());
        target.setBwbh(source.getBwbh());
        target.setBwbsh(source.getBwbsh());
        target.setBwsh(source.getBwsh());
        target.setBwxh(source.getBwxh());
        target.setBwzjh(source.getBwzjh());
        target.setBwbxh(source.getBwbxh());
        target.setBwblyhxh(source.getBwblyhxh());
        target.setHswdpcz(source.getHswdpcz());
        target.setDlsdUnitCode(source.getDlsdUnitCode());
        target.setIsAbnormalEnable(source.getIsAbnormalEnable());
        target.setMrphwd(source.getMrphwd());
        target.setWjfhswd(source.getWjfhswd());
    }
}
