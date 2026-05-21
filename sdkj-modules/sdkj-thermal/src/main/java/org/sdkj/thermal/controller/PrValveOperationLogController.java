package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;
import org.sdkj.thermal.service.IPrValveOperationLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/valve-operation-log")
public class PrValveOperationLogController extends BaseController {

    private final IPrValveOperationLogService valveOperationLogService;

    @SaCheckPermission("thermal:ht:valve-operation-log:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrValveOperationLogVo> list(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String meterNum,
            PageQuery pageQuery) {
        return valveOperationLogService.selectPageList(pageQuery, orgId, meterNum);
    }
}
