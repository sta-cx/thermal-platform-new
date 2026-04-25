package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrImportHeat;
import org.sdkj.thermal.excel.ExcelStyleUtils;
import org.sdkj.thermal.service.IPrImportHeatService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/import/heat")
public class PrImportHeatController extends BaseController {

    private final IPrImportHeatService prImportHeatService;

    @SaCheckPermission("thermal:property:import:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<String> pageList() {
        Integer r = prImportHeatService.select();
        if (r == 0) {
            return R.ok("无待提交数据");
        }
        String result = prImportHeatService.getNull(r);
        return R.ok(result);
    }

    @SaCheckPermission("thermal:property:import:export")
    @SaCheckLogin
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response,
                                  @RequestParam String companyId,
                                  @RequestParam String orgId) throws IOException {
        List<PrImportHeat> lists = prImportHeatService.selectByCompanyIdOrgId(companyId, orgId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("热表数据导入", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), PrImportHeat.class)
            .registerWriteHandler(ExcelStyleUtils.fullStyleHandler(150))
            .sheet("热表数据导入")
            .doWrite(lists);
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @Log(title = "热表导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<String> importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }
        List<Object> objects;
        try {
            objects = EasyExcel.read(file.getInputStream())
                .head(PrImportHeat.class)
                .sheet(0)
                .headRowNumber(2)
                .doReadSync();
        } catch (Exception e) {
            return R.fail("文件解析失败: " + e.getMessage());
        }

        Integer select = prImportHeatService.select();
        if (select > 0) {
            return R.fail("有未提交的数据，请先提交");
        }

        try {
            Integer r = prImportHeatService.importData(objects);
            prImportHeatService.updateHouseId();
            prImportHeatService.check(r);
            String result = prImportHeatService.getNull(r);
            return R.ok(result);
        } catch (Exception e) {
            return R.fail("导入失败: " + e.getMessage());
        }
    }

    @SaCheckPermission("thermal:property:import:remove")
    @SaCheckLogin
    @DeleteMapping
    public R<Void> deleteData() {
        boolean result = prImportHeatService.deleteData();
        return result ? R.ok() : R.fail("删除失败");
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @PostMapping("/submit")
    public R<Void> submitData() {
        boolean result = prImportHeatService.submitData();
        return result ? R.ok() : R.fail("提交失败");
    }

}
