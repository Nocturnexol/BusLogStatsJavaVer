package com.bs;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

final class ExcelHelper {
    private static CellStyle cellStyle;
    private static CellStyle boldStyle;
    private static CellStyle filledStyle;
    private static CellStyle leftAlignStyle;

    static void export(TableModelMatrix model) throws IOException {
        Vector titles = model.getTitleName();
        Vector contents = model.getContent();

        Workbook wb = new HSSFWorkbook();
        cellStyle = getCellStyle(wb);
        boldStyle = getBoldStyle(wb);
        filledStyle = getFilledStyle(wb);
        leftAlignStyle = getLeftAlignStyle(wb);

        // create a new sheet
        Sheet sheet = wb.createSheet();
        // declare a row object reference
        Row r;
        // declare a cell object reference
        Cell c;
        int lastCol = ((Vector) contents.get(0)).size();

        initHeader(sheet, lastCol, model);

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
        FileOutputStream out = new FileOutputStream(model.getFileName().replaceAll(".txt", "") + ".xls");
        wb.write(out);
        out.close();

    }

    static void bulkExport(File path) throws IOException {
        if (!path.isDirectory()) return;
        File[] dirs = path.listFiles();
        if (dirs == null || dirs.length == 0) return;
        HashMap<String, Workbook> hMap = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        for (File dir : dirs) {
            if (!dir.isDirectory()) continue;
            String dirName = dir.getName();
            try {
                df.parse(dirName);
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }
            File[] txtFiles = dir.listFiles();
            if (txtFiles == null || txtFiles.length == 0) continue;
            for (File txt : txtFiles) {
                if (!txt.isFile()) continue;
                String fileName = txt.getName();
                //get current workbook by the file name
                //a new instance will be created if not existing
                Workbook wb = hMap.computeIfAbsent(fileName, k -> new HSSFWorkbook());
                Sheet sheet = wb.createSheet(dirName);

            }
        }
        //export all workbooks
        for (Object o : hMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object key = entry.getKey();
            Workbook val = (Workbook) entry.getValue();
            FileOutputStream fs = new FileOutputStream("excels/" + key);
            val.write(fs);
            fs.close();
        }

    }


    /**
     * 初始化表头
     *
     * @param sheet   sheet
     * @param lastCol 最后一列索引数
     */
    private static void initHeader(Sheet sheet, int lastCol, BusTableModel tbl) {
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
                c.setCellValue("数据量：" + tbl.getTotal() + "  百分比：" + tbl.getRatio());
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

    private static CellStyle getCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setWrapText(true);
        Font f = wb.createFont();
        f.setFontName("宋体");
        f.setFontHeightInPoints((short) 12);
        cellStyle.setFont(f);
        return cellStyle;
    }

    private static CellStyle getBoldStyle(Workbook wb) {
        CellStyle cellStyle = getCellStyle(wb);
        Font f = wb.getFontAt((short) 0);
        if (f == null) {
            f = wb.createFont();
            f.setFontName("宋体");
            f.setFontHeightInPoints((short) 12);
        }
        f.setBold(true);
        cellStyle.setFont(f);
        return cellStyle;
    }

    private static CellStyle getFilledStyle(Workbook wb) {
        CellStyle cellStyle = getCellStyle(wb);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    private static CellStyle getLeftAlignStyle(Workbook wb) {
        CellStyle cellStyle = getCellStyle(wb);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        return cellStyle;
    }
}