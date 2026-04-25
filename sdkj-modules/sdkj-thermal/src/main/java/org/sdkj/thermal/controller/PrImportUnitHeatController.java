package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrImportUnitHeat;
import org.sdkj.thermal.service.IPrImportUnitHeatService;
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
@RequestMapping("/thermal/property/import/unit-heat")
public class PrImportUnitHeatController extends BaseController {

    private final IPrImportUnitHeatService prImportUnitHeatService;

    @SaCheckPermission("thermal:property:import:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<String> pageList() {
        Integer r = prImportUnitHeatService.select();
        if (r == 0) {
            return R.ok("无待提交数据");
        }
        String result = prImportUnitHeatService.getNull(r);
        return R.ok(result);
    }

    @SaCheckPermission("thermal:property:import:export")
    @SaCheckLogin
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response,
                                  @RequestParam String companyId,
                                  @RequestParam String orgId) throws IOException {
        List<PrImportUnitHeat> lists = prImportUnitHeatService.selectByCompanyIdOrgId(companyId, orgId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("单元热表数据导入", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), PrImportUnitHeat.class)
            .registerWriteHandler(new ImportExcelHandler())
            .sheet("单元热表数据导入")
            .doWrite(lists);
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @Log(title = "单元热表导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<String> importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }
        List<Object> objects;
        try {
            objects = EasyExcel.read(file.getInputStream())
                .head(PrImportUnitHeat.class)
                .sheet(0)
                .headRowNumber(2)
                .doReadSync();
        } catch (Exception e) {
            return R.fail("文件解析失败: " + e.getMessage());
        }

        Integer select = prImportUnitHeatService.select();
        if (select > 0) {
            return R.fail("有未提交的数据，请先提交");
        }

        try {
            Integer r = prImportUnitHeatService.importData(objects);
            prImportUnitHeatService.updateIds();
            prImportUnitHeatService.check(r);
            String result = prImportUnitHeatService.getNull(r);
            return R.ok(result);
        } catch (Exception e) {
            return R.fail("导入失败: " + e.getMessage());
        }
    }

    @SaCheckPermission("thermal:property:import:remove")
    @SaCheckLogin
    @DeleteMapping
    public R<Void> deleteData() {
        boolean result = prImportUnitHeatService.deleteData();
        return result ? R.ok() : R.fail("删除失败");
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @PostMapping("/submit")
    public R<Void> submitData() {
        boolean result = prImportUnitHeatService.submitData();
        return result ? R.ok() : R.fail("提交失败");
    }

    static class ImportExcelHandler implements CellWriteHandler {

        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            WriteCellData<?> cellData = context.getFirstCellData();
            if (cellData == null || context.getRow() == null) {
                return;
            }
            Row row = context.getRow();
            WriteCellStyle cellStyle = cellData.getOrCreateStyle();

            if (row.getRowNum() == 0) {
                row.setHeight((short) (150 * 20));
            }

            if (row.getRowNum() == 1) {
                cellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                row.setHeight((short) (20 * 20));
            }
            if (row.getRowNum() > 1) {
                cellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                row.setHeight((short) (15 * 20));
            }
        }
    }
}
