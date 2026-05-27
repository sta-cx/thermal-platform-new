package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrImportBasicData;
import org.sdkj.thermal.domain.PrImportBasicDataByCode;
import org.sdkj.thermal.excel.ExcelStyleUtils;
import org.sdkj.thermal.service.IPrImportBasicDataService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/import/basic-data")
public class PrImportBasicDataController extends BaseController {

    private final IPrImportBasicDataService service;

    @SaCheckPermission("thermal:property:import:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<String> pageList() {
        return R.ok();
    }

    @SaCheckPermission("thermal:property:import:export")
    @SaCheckLogin
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("基础数据导入", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), PrImportBasicData.class)
            .registerWriteHandler(ExcelStyleUtils.fullStyleHandler(500))
            .sheet("基础数据导入")
            .doWrite(new java.util.ArrayList<>());
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @Log(title = "基础数据导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<String> importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }
        List<PrImportBasicData> objects;
        try {
            objects = (List<PrImportBasicData>) (List<?>) EasyExcel.read(file.getInputStream())
                .head(PrImportBasicData.class)
                .sheet(0)
                .headRowNumber(2)
                .doReadSync();
        } catch (Exception e) {
            log.error("PrImportBasicDataController failed", e);
            return R.fail("文件解析失败: " + e.getMessage());
        }

        long count = service.count();
        if (count > 0) {
            return R.fail("有未提交的数据，请先提交");
        }
        try {
            Integer r = service.importData(objects);
            R result = service.check(r);
            return result;
        } catch (Exception e) {
            log.error("PrImportBasicDataController failed", e);
            return R.fail("导入失败: " + e.getMessage());
        }
    }

    @SaCheckPermission("thermal:property:import:remove")
    @SaCheckLogin
    @DeleteMapping
    public R<Void> deleteData() {
        boolean result = service.deleteData();
        return result ? R.ok() : R.fail("删除失败");
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @PostMapping("/submit")
    public R<Void> submitData() {
        boolean result = service.submitData();
        return result ? R.ok() : R.fail("提交失败");
    }

    // ========== 按房屋编码导入 ==========

    /**
     * 下载按房屋编码导入的 Excel 模板
     */
    @SaCheckPermission("thermal:property:import:export")
    @SaCheckLogin
    @PostMapping("/template-by-code")
    public void downloadTemplateByHeatCode(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("按房屋编码导入模板", StandardCharsets.UTF_8)
            .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), PrImportBasicDataByCode.class)
            .registerWriteHandler(ExcelStyleUtils.fullStyleHandler(500))
            .sheet("按房屋编码导入")
            .doWrite(new java.util.ArrayList<>());
    }

    /**
     * 按房屋编码导入基础数据
     * 读取 Excel，通过房屋编码匹配已有房屋，更新房屋信息及用户档案
     */
    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @Log(title = "按房屋编码导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import-by-code")
    public R<String> importDataByHeatCode(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }
        List<PrImportBasicDataByCode> objects;
        try {
            objects = (List<PrImportBasicDataByCode>) (List<?>) EasyExcel.read(file.getInputStream())
                .head(PrImportBasicDataByCode.class)
                .sheet(0)
                .headRowNumber(2)
                .doReadSync();
        } catch (Exception e) {
            log.error("PrImportBasicDataController failed", e);
            return R.fail("文件解析失败: " + e.getMessage());
        }

        if (objects == null || objects.isEmpty()) {
            return R.fail("文件中没有数据");
        }

        try {
            R<String> result = service.importDataByHeatCode(objects);
            return result;
        } catch (Exception e) {
            log.error("PrImportBasicDataController failed", e);
            return R.fail("导入失败: " + e.getMessage());
        }
    }

    /**
     * 检查指定房屋编码的房屋是否存在
     */
    @SaCheckLogin
    @GetMapping("/check-house")
    public R<Boolean> isCheckHouse(@RequestParam("code") String code) {
        boolean exists = service.isCheckHouse(code);
        return R.ok(exists);
    }

}
