package com.bs;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

class ExcelHelper {

    private static Workbook wb = new HSSFWorkbook();
    private static CellStyle cellStyle = getCellStyle(wb, false, HorizontalAlignment.CENTER, FillPatternType.NO_FILL);
    private static CellStyle boldStyle = getCellStyle(wb, true, HorizontalAlignment.CENTER, FillPatternType.NO_FILL);
    private static CellStyle filledStyle = getCellStyle(wb, false, HorizontalAlignment.CENTER, FillPatternType
            .SOLID_FOREGROUND);
    private static CellStyle leftAlignStyle = getCellStyle(wb, false, HorizontalAlignment.LEFT, FillPatternType
            .NO_FILL);

    static void export(TableModelMatrix model, String fileName, ResultBean res) throws IOException {
        Vector titles = model.getTitleName();
        Vector contents = model.getContent();
        // prevent from multi-sheet
        if (wb.getNumberOfSheets() > 0) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                wb.removeSheetAt(i);
            }
        }
        // create a new sheet
        Sheet sheet = wb.createSheet();
        // declare a row object reference
        Row r;
        // declare a cell object reference
        Cell c;
        int lastCol = ((Vector) contents.get(0)).size();

        initHeader(sheet, lastCol, res);

        // Titles
        r = sheet.createRow(2);
        for (int i = 0; i < titles.size(); i++) {
            c = r.createCell(i + 1);
            if (i > 0) c.setCellValue(titles.get(i).toString().replaceAll(" ", ""));
            c.setCellStyle(boldStyle);
        }

        for (int rowNum = 0; rowNum < contents.size(); rowNum++) {
            r = sheet.createRow(rowNum + 3);
            Vector cellVec = (Vector) contents.get(rowNum);
            c = r.createCell(0);
            if (rowNum == 0)
                c.setCellValue("预计行程时间-实际行程时间（分钟）");
            c.setCellStyle(cellStyle);
            for (int cellNum = 0; cellNum < cellVec.size(); cellNum++) {
                c = r.createCell(cellNum + 1);
                Object val = cellVec.get(cellNum);
                if (val instanceof Number) {
                    int intVal = ((Number) val).intValue();
                    c.setCellValue(intVal);
                    if (intVal > 0) {
                        c.setCellStyle(filledStyle);
                    } else c.setCellStyle(cellStyle);
                } else {
                    c.setCellValue(val.toString());
                    c.setCellStyle(boldStyle);
                }
            }
        }
        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 7));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, lastCol));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, lastCol));
        sheet.addMergedRegion(new CellRangeAddress(3, contents.size() + 2, 0, 0));
        for (int i = 0; i < lastCol; i++) {
            //设置自适应宽度
            sheet.autoSizeColumn(i);
        }
        // Save
        FileOutputStream out = new FileOutputStream(fileName.replaceAll(".txt", "") + ".xls");
        wb.write(out);
        out.close();

    }

    /**
     * 初始化表头
     *
     * @param sheet   sheet
     * @param lastCol 最后一列索引数
     * @param res     res
     */
    private static void initHeader(Sheet sheet, int lastCol, ResultBean res) {
        Row r = sheet.createRow(0);
        Cell c;
        for (int i = 0; i <= lastCol; i++) {
            c = r.createCell(i);
            if (i == 0) {
                c.setCellValue("线路：");
                c.setCellStyle(cellStyle);
            } else if (i == 2) {
                c.setCellValue("数据日期：");
                c.setCellStyle(leftAlignStyle);
            } else if (i == 8) {
                c.setCellValue("数据量：" + res.getTotal() + "  百分比：" + res.getRatio());
                c.setCellStyle(leftAlignStyle);
            } else {
                c.setCellStyle(cellStyle);
            }
        }

        r = sheet.createRow(1);
        for (int i = 0; i <= lastCol; i++) {
            c = r.createCell(i);
            if (i == 0) {
                c.setCellValue("次数");
                c.setCellStyle(boldStyle);
            } else if (i == 2) {
                c.setCellValue("实际行程时间（分钟）");
                c.setCellStyle(boldStyle);
            } else c.setCellStyle(cellStyle);
        }
    }

    private static CellStyle getCellStyle(Workbook wb, boolean isBold, HorizontalAlignment hAlign, FillPatternType
            fillType) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(hAlign);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(fillType);
        cellStyle.setWrapText(true);
        Font f = wb.createFont();
        f.setFontName("宋体");
        f.setFontHeightInPoints((short) 12);
        f.setBold(isBold);
        cellStyle.setFont(f);
        return cellStyle;
    }

}