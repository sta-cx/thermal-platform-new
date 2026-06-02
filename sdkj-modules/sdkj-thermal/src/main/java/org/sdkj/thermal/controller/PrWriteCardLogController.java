package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.bo.PrWriteCardLogBo;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;
import org.sdkj.thermal.service.IPrWriteCardLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 写卡日志（写卡/开卡/补卡 操作记录，表 pr_use_card_log） */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/write-card-log")
public class PrWriteCardLogController extends BaseController {

    private final IPrWriteCardLogService writeCardLogService;

    /** 写卡记录分页查询 */
    @SaCheckPermission("thermal:ht:valveArchive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrValveOperationLogVo> list(PrWriteCardLogBo bo, PageQuery pageQuery) {
        return writeCardLogService.selectPageList(bo, pageQuery);
    }

    /** 写卡成功后插入日志 */
    @SaCheckPermission("thermal:ht:valveArchive:edit")
    @SaCheckLogin
    @Log(title = "写卡日志", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Long> add(@Validated @RequestBody PrWriteCardLogBo bo) {
        return R.ok("写卡日志已记录", writeCardLogService.insertWriteCardLog(bo));
    }
}
