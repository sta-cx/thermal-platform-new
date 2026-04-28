package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrRepairRecord;
import org.sdkj.thermal.service.IPrRepairRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/repair")
public class WechatRepairController extends BaseController {

    private final IPrRepairRecordService repairRecordService;

    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrRepairRecord> list(
            @RequestParam(required = false) String userId,
            PageQuery pageQuery) {
        Page<PrRepairRecord> page = pageQuery.build();
        LambdaQueryWrapper<PrRepairRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrRepairRecord::getUserId, userId);
        lqw.orderByDesc(PrRepairRecord::getCreateTime);
        repairRecordService.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrRepairRecord> details(@PathVariable String id) {
        return R.ok(repairRecordService.getById(id));
    }

    @SaCheckLogin
    @Log(title = "微信报修", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insertData(@RequestBody PrRepairRecord record) {
        record.setRepairNo(repairRecordService.generateRepairNo());
        record.setCreateByName(record.getPhone());
        record.setRepairName(record.getUserName());
        record.setRepairPhone(record.getPhone());
        record.setRepairRoomNum(record.getRepairAddress());
        record.setInUserName(record.getUserName());
        record.setInPhone(record.getPhone());
        record.setAppointTime(record.getRepairTime());
        record.setRepairType("01");
        record.setRepairStatus(1);
        record.setCreateTime(new Date());
        return toAjax(repairRecordService.save(record));
    }

    @SaCheckLogin
    @Log(title = "微信报修修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateData(@RequestBody PrRepairRecord record) {
        record.setCreateByName(record.getPhone());
        record.setRepairName(record.getUserName());
        record.setRepairPhone(record.getPhone());
        record.setRepairRoomNum(record.getRepairAddress());
        record.setInUserName(record.getUserName());
        record.setInPhone(record.getPhone());
        record.setAppointTime(record.getRepairTime());
        record.setRepairStatus(2);
        return toAjax(repairRecordService.updateById(record));
    }

    @SaCheckLogin
    @Log(title = "微信报修撤销", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> deleteData(@PathVariable String id) {
        return toAjax(repairRecordService.removeById(id));
    }

    @SaCheckLogin
    @Log(title = "微信报修状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public R<Void> updateStatus(@RequestBody PrRepairRecord record) {
        return toAjax(repairRecordService.updateById(record));
    }
}
