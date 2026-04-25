package org.sdkj.thermal.excel;

import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import org.apache.poi.ss.usermodel.*;

public class ExcelStyleUtils {

    public static CellWriteHandler fullStyleHandler(int titleRowHeight) {
        return new CellWriteHandler() {
            @Override
            public void afterCellDispose(CellWriteHandlerContext context) {
                WriteCellData<?> cellData = context.getFirstCellData();
                if (cellData == null || context.getRow() == null) {
                    return;
                }
                Row row = context.getRow();
                WriteCellStyle cellStyle = cellData.getOrCreateStyle();

                if (row.getRowNum() == 0) {
                    row.setHeight((short) (titleRowHeight * 20));
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
        };
    }

    public static CellWriteHandler headerOnlyStyleHandler(int titleRowHeight) {
        return new CellWriteHandler() {
            @Override
            public void afterCellDispose(CellWriteHandlerContext context) {
                WriteCellData<?> cellData = context.getFirstCellData();
                if (cellData == null || context.getRow() == null) return;
                Row row = context.getRow();
                WriteCellStyle style = cellData.getOrCreateStyle();
                if (row.getRowNum() == 0) row.setHeight((short) (titleRowHeight * 20));
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
        };
    }
}
