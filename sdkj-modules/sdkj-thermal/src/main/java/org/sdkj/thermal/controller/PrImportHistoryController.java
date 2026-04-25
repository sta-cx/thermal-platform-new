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
import org.sdkj.thermal.domain.PrImportHistory;
import org.sdkj.thermal.service.IPrImportHistoryService;
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
@RequestMapping("/thermal/property/import/history")
public class PrImportHistoryController extends BaseController {

    private final IPrImportHistoryService service;

    @SaCheckPermission("thermal:property:import:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<String> pageList() {
        Integer r = service.select();
        if (r == 0) return R.ok("无待提交数据");
        return R.ok(service.check(r));
    }

    @SaCheckPermission("thermal:property:import:export")
    @SaCheckLogin
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("历史数据导入", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrImportHistory.class)
            .registerWriteHandler(new HistoryHandler()).sheet("历史数据导入")
            .doWrite(new java.util.ArrayList<>());
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @Log(title = "历史数据导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<String> importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return R.fail("文件为空");
        List<Object> objects;
        try {
            objects = EasyExcel.read(file.getInputStream()).head(PrImportHistory.class)
                .sheet(0).headRowNumber(2).doReadSync();
        } catch (Exception e) {
            return R.fail("文件解析失败: " + e.getMessage());
        }
        try {
            Integer r = service.importData(objects);
            service.updateIds();
            return R.ok(service.check(r));
        } catch (Exception e) {
            return R.fail("导入失败: " + e.getMessage());
        }
    }

    @SaCheckPermission("thermal:property:import:remove")
    @SaCheckLogin
    @DeleteMapping
    public R<Void> deleteData() {
        return service.deleteData() ? R.ok() : R.fail("删除失败");
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @PostMapping("/submit")
    public R<Void> submitData() {
        return service.submitData() ? R.ok() : R.fail("提交失败");
    }

    static class HistoryHandler implements CellWriteHandler {
        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            WriteCellData<?> cellData = context.getFirstCellData();
            if (cellData == null || context.getRow() == null) return;
            Row row = context.getRow();
            WriteCellStyle style = cellData.getOrCreateStyle();
            if (row.getRowNum() == 0) row.setHeight((short) (120 * 20));
            if (row.getRowNum() == 1) {
                style.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                row.setHeight((short) (20 * 20));
            }
        }
    }
}
