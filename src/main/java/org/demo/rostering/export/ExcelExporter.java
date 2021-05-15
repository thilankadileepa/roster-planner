package org.demo.rostering.export;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.demo.rostering.domain.*;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 4/24/2021<br/>
 * Time: 12:23 AM<br/>
 * To change this template use File | Settings | File Templates.
 */
public class ExcelExporter {

    public static void main(String[] args) {
        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.export(null);
    }

    public static CellStyle getStyleBold(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    public static CellStyle getWeekendStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public static CellStyle getStyleForShift(Type type, Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        if (type == Type.M) {
            style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        } else if (type == Type.A) {
            style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        } else if (type == Type.E) {
            style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        }
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    public static String getCellText(ShiftDate shiftDate, Type type) {
        return shiftDate.isWeekEnd() ? ("WE" + type.toString()) : type.toString();
    }

    public void export(EmployeeRoster employeeRoster) {
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Plan");
            Row row = sheet.createRow(0);

            int cellNumber = 1;
            for (ShiftDate shiftDate : employeeRoster.getShiftDateList()) {
                Cell cell = row.createCell(cellNumber++);
                cell.setCellValue(shiftDate.getDate().toString());
                if (shiftDate.isWeekEnd()) {
                    cell.setCellStyle(getWeekendStyle(workbook));
                }

                sheet.autoSizeColumn(cell.getColumnIndex());
            }

            for (Employee employee : employeeRoster.getEmployeeList()) {
                Row empRow = sheet.createRow(employee.getId().intValue() + 1);
                Cell cell = empRow.createCell(0);
                cell.setCellValue(employee.getName());

                cell.setCellStyle(getStyleBold(workbook));
                sheet.autoSizeColumn(cell.getColumnIndex());
            }

            for (ShiftAssignment shiftAssignment : employeeRoster.getShiftAssignmentList()) {
                Row recordRow = sheet.getRow(shiftAssignment.getEmployee().getId().intValue() + 1);
                Cell recordRowCell = recordRow.createCell(shiftAssignment.getShiftDateDayIndex() + 1);

                if (recordRowCell != null) {
                    recordRowCell.setCellValue(getCellText(shiftAssignment.getShiftDate(), shiftAssignment.getType()));

                    //Make a style
                    recordRowCell.setCellStyle(getStyleForShift(shiftAssignment.getType(), workbook));
                }
            }

            FileOutputStream fileOut = new FileOutputStream("roster.xlsx");
            workbook.write(fileOut);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}