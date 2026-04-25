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
import org.sdkj.thermal.domain.PrImportBasicData;
import org.sdkj.thermal.service.IPrImportBasicDataService;
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
            .registerWriteHandler(new BasicDataHandler())
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
        List<Object> objects;
        try {
            objects = EasyExcel.read(file.getInputStream())
                .head(PrImportBasicData.class)
                .sheet(0)
                .headRowNumber(2)
                .doReadSync();
        } catch (Exception e) {
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

    static class BasicDataHandler implements CellWriteHandler {

        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            WriteCellData<?> cellData = context.getFirstCellData();
            if (cellData == null || context.getRow() == null) {
                return;
            }
            Row row = context.getRow();
            WriteCellStyle cellStyle = cellData.getOrCreateStyle();

            if (row.getRowNum() == 0) {
                row.setHeight((short) (500 * 20));
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
