package org.sdkj.thermal.ai.tools.readonly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetValveStatusTool {

    private final PrHeatValveArchiveMapper valveMapper;

    public record ValveStatus(
        Long valveId,
        Long houseId,
        String meterNum,
        String valveStatus,
        Integer actualStatus,
        BigDecimal inTemperature,
        BigDecimal outTemperature,
        BigDecimal roomTemp,
        Date valveTime
    ) {}

    @Tool(description = """
        查询指定热户阀门的当前状态(开度、温度、上报时间)。
        典型用途:用户问"张三家阀门什么状态"。
        若热户未配阀门,返回 null。
        """)
    public ValveStatus getByHouseId(
        @ToolParam(description = "热户 ID(必填)") Long houseId
    ) {
        log.info("[Tool] getValveStatusTool.getByHouseId houseId={}", houseId);

        LambdaQueryWrapper<PrHeatValveArchive> qw = new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getHouseId, houseId)
            .orderByDesc(PrHeatValveArchive::getValveTime)
            .last("LIMIT 1");
        PrHeatValveArchiveVo v = valveMapper.selectVoOne(qw);
        if (v == null) return null;
        return new ValveStatus(
            v.getId(),
            v.getHouseId(),
            v.getMeterNum(),
            v.getValveStatus(),
            v.getActualStatus(),
            v.getInTemperature(),
            v.getOutTemperature(),
            v.getRoomTemp(),
            v.getValveTime()
        );
    }
}
