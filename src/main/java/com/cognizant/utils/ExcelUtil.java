package com.cognizant.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Abel
 * @date 2018/12/11 10:40
 */
public class ExcelUtil {


    private static final String EXCEL_XLS = ".xls";
    private static final String EXCEL_XLSX = ".xlsx";


    private static String sheetName;

    public static String getSheetName() {
        return sheetName;
    }


    /**
     * 读取Excel文件内容
     *
     * @param file            File
     * @param whichSheetBegin 从第几个sheet页读取。默认为第1个sheet页，索引为0
     * @param whichSheetStop  到第几个sheet页结束。默认到最后一个
     * @param whichRowBegin   从第几行读取。默认为第1行，索引为0
     * @param whichRowStop    到第几行结束。默认到最后一行
     * @param whichCellBegin  第几个单元格读取。默认为第1个单元格，索引为0
     * @param whichCellStop   到第几个单元格结束。默认到最后一个单元格
     * @return Map
     */
    public static Map<String, List<List<String>>> readExcelContent(File file,
                                                                   Integer whichSheetBegin, Integer whichSheetStop,
                                                                   Integer whichRowBegin, Integer whichRowStop,
                                                                   Integer whichCellBegin, Integer whichCellStop) {
        Workbook workbook = null;
        InputStream inputStream = null;
        List<List<String>> dataList = null;
        Map<String, List<List<String>>> map = null;
        try {
            inputStream = new FileInputStream(file);

            if (checkExcelVaild(file)) {
                workbook = getWorkbook(inputStream, file);

                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();


//                int sheetNum = 0;//从第几个sheet页开始,默认为第1个sheet页，索引为0-------------------
                int sheetNum = CommonUtil.isEmpty(whichSheetBegin) ? 0 : (whichSheetBegin - 1);

                map = new HashMap<>();

                //获取sheet页数量
                int totalSheets = workbook.getNumberOfSheets();
                System.out.println("sheet数量：" + totalSheets);

                int numberOfSheets = CommonUtil.isEmpty(whichSheetStop) ? totalSheets : whichSheetStop;

                for (int sheetIndex = sheetNum; sheetIndex < numberOfSheets; sheetIndex++) {

                    dataList = new ArrayList<>();//大容器，存储外层行数据

                    //根据索引获取sheet页
                    Sheet sheet = workbook.getSheetAt(sheetIndex);
                    sheetName = sheet.getSheetName();

//                    System.out.println(sheet.getSheetName());

                    if (sheet == null) {
                        continue;
                    }

//                    int rowNum = 1;//从第几行开始，默认为第1行，索引为0-------------------------
                    int rowNum = CommonUtil.isEmpty(whichRowBegin) ? 0 : (whichRowBegin - 1);

                    //获取总行数
                    int totoalRows = sheet.getLastRowNum() + 1;//getLastRowNum()返回最后一行的索引,需加1
//                    System.out.println("总行数：" + totoalRows);
                    int numOfSheets = CommonUtil.isEmpty(whichRowStop) ? totoalRows : whichRowStop;

                    //从第一行开始读取
                    for (int rowIndex = rowNum; rowIndex < numOfSheets; rowIndex++) {
//                        System.out.println("第" + (rowIndex + 1) + "行");
                        Row row = sheet.getRow(rowIndex);
                        if (row == null) {
                            continue;
                        }
                        List<String> cellList = new ArrayList<>();//存内层单元格数据

//                        int cellNum = 0;//从第几个单元格开始，默认为第1个单元格，索引为0-------------------------
                        int cellNum = CommonUtil.isEmpty(whichCellBegin) ? 0 : (whichCellBegin - 1);

                        //获取总列数
//                        short totalCellNums = row.getLastCellNum();

                        short totalCells = sheet.getRow(rowNum).getLastCellNum();//=============================================
//                        System.out.println("总列数：" + totalCellNums);
                        int numOfCells = CommonUtil.isEmpty(whichCellStop) ? totalCells : whichCellStop;

                        for (int cellIndex = cellNum; cellIndex < numOfCells; cellIndex++) {
//                            System.out.println("第" + (cellIndex + 1) + "列");

                            boolean mergedRegion = isMergedRegion(sheet, rowIndex, cellIndex);//判断是否是合并单元格
                            if (mergedRegion) {
                                String mergedRegionValue = getMergedRegionValue(sheet, rowIndex, cellIndex, formulaEvaluator);//获取合并单元格的值

                                //过滤合并单元格重复读取的内容（判断是否和前一合并单元格或上一合并单元格内容一样）
//                                String value1 = getMergedRegionValue(sheet, rowIndex, cellIndex - 1, formulaEvaluator);
//                                String value2 = getMergedRegionValue(sheet, rowIndex - 1, cellIndex, formulaEvaluator);
//                                if (mergedRegionValue.equals(value1) || mergedRegionValue.equals(value2)) {
//                                    continue;
//                                }
                                cellList.add(mergedRegionValue);
                            } else {
                                Cell cell = row.getCell(cellIndex);
                                if (cell == null) {
                                    cellList.add("");
                                    continue;
                                }
                                String cellValue = getCellValue(cell, formulaEvaluator);
                                cellList.add(cellValue);
                            }

                        }
                        if (cellList.size() > 0) {
                            dataList.add(cellList);
                        }
                    }
                    map.put(sheetName, dataList);

                }

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }
        return map;
    }


    /**
     * 检查文件是否是Excel
     *
     * @param file
     * @return boolean
     */
    private static boolean checkExcelVaild(File file) {
        if (CommonUtil.isEmpty(file)) {
            System.out.println("文件不存在");
            throw new RuntimeException("文件不存在");
        }
        if (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)) {
            return true;
        }
        System.out.println("文件不是Excel.........");
        return false;
    }

    /**
     * 创建Workbook对象
     *
     * @param inputStream InputStream 输入流
     * @param file        File
     * @return Workbook
     * @throws IOException
     */
    private static Workbook getWorkbook(InputStream inputStream, File file) throws IOException {
        Workbook workbook = null;
        if (file.getName().endsWith(EXCEL_XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (file.getName().endsWith(EXCEL_XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            System.out.println("创建Workbook失败");
            throw new RuntimeException("创建Workbook失败");
        }
        return workbook;
    }


    /**
     * 根据类型进行判断，获取单元格数据
     *
     * @param cell             Cell
     * @param formulaEvaluator FormulaEvaluator 公式计算器
     * @return String
     */
    private static String getCellValue(Cell cell, FormulaEvaluator formulaEvaluator) {
        String cellValue = "";
        CellType cellType = cell.getCellType();
        if (cellType == CellType.STRING) { //字符串
            cellValue = cell.getStringCellValue().trim();
            return CommonUtil.isEmpty(cellValue) ? "" : cellValue;

        } else if (cellType == CellType.NUMERIC) { //日期

            if (DateUtil.isCellDateFormatted(cell)) {//isCellDateFormatted不能判断中文时间单位（例如含有“年”或“月”等时间单位）
//                todo isCellDateFormatted不能判断中文时间单位问题没有解决
                double numericCellValue = cell.getNumericCellValue();
                Date date = DateUtil.getJavaDate(numericCellValue);
                cellValue = CommonUtil.formatDate(date);
                return cellValue;
//                下面这张方式也可以获取到日期
//                long time = cell.getDateCellValue().getTime();
//                cellValue = CommonUtil.dateToStr(time);

            } else {
                //处理自定义单元格%格式
                double numericCellValue = cell.getNumericCellValue();
                DecimalFormat decimalFormat = new DecimalFormat();
                String dataFormatString = cell.getCellStyle().getDataFormatString();//获取自定义单元格的格式
                if (dataFormatString.endsWith("%")) {
                    if (dataFormatString.contains(".")) {//0%
                        int point = dataFormatString.lastIndexOf(".");
                        String substring = dataFormatString.substring(point);
                        String result = "0" + substring;//0.00%
                        decimalFormat.applyPattern(result);//应用被给的模式
                        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);//四舍五入
                        cellValue = decimalFormat.format(numericCellValue);
                        return cellValue;
                    } else {
                        decimalFormat.applyPattern("0%");//应用被给的模式
                        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);//四舍五入
                        cellValue = decimalFormat.format(numericCellValue);
                        return cellValue;
                    }

                }
                cellValue = new DecimalFormat("#.######").format(numericCellValue);
                return cellValue;

            }

        } else if (cellType == CellType.BLANK) {//空
            return cellValue;
        } else if (cellType == CellType.BOOLEAN) { //boolean类型
            cellValue = String.valueOf(cell.getBooleanCellValue());
            return cellValue;
        } else if (cellType == CellType.FORMULA) {
//            处理公式类型的格式
//           setIgnoreMissingWorkbooks(true)处理“#N/A”（#N/A表示值计算不出来）外部链接文档，
            formulaEvaluator.setIgnoreMissingWorkbooks(true);//是否忽略对外部工作簿的缺失引用并使用缓存的公式结果而不是主工作簿。
            CellValue evaluate = formulaEvaluator.evaluate(cell);
            cellValue = getCellFormulaValue(evaluate);
            return cellValue;
        }
        return "";
    }

    /**
     * 获取公式计算后的值
     *
     * @param cellValue CellValue
     * @return String
     */
    private static String getCellFormulaValue(CellValue cellValue) {
        CellType cellType = cellValue.getCellType();
        if (cellType == CellType.STRING) {
            return cellValue.getStringValue().trim();

        } else if (cellType == CellType.NUMERIC) {
            return String.valueOf(cellValue.getNumberValue());

        } else if (cellType == CellType.BOOLEAN) {
//            System.out.println(cellValue.getBooleanValue());
            return String.valueOf(cellValue.getBooleanValue());
        } else if (cellType == CellType.BLANK) {
            return "";
        } else if (cellType == CellType.ERROR) {
            System.out.println(cellValue.getErrorValue());
            return "";
        } else if (cellType == CellType.FORMULA) {// CellType.FORMULA will never happen
            return "";
        }
        return "";
    }


    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet  Sheet
     * @param row    行下标
     * @param column 列下标
     * @return boolean
     */
    private static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int numMergedRegions = sheet.getNumMergedRegions();//获取合并单元格数量

        for (int i = 0; i < numMergedRegions; i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            int firstRow = cellRangeAddress.getFirstRow();
            int lastRow = cellRangeAddress.getLastRow();
            int firstColumn = cellRangeAddress.getFirstColumn();
            int lastColumn = cellRangeAddress.getLastColumn();
            if (firstRow <= row && row <= lastRow) {
                if (firstColumn <= column && column <= lastColumn) {
                    return true;
                }
            }

        }
        return false;
    }


    /**
     * 获取合并单元格的值
     *
     * @param sheet            Sheet
     * @param row              行下标
     * @param column           列下标
     * @param formulaEvaluator
     * @return
     */
    public static String getMergedRegionValue(Sheet sheet, int row, int column, FormulaEvaluator formulaEvaluator) {
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        for (CellRangeAddress cellAddresses : mergedRegions) {
            int firstRow = cellAddresses.getFirstRow();
            int lastRow = cellAddresses.getLastRow();
            int firstColumn = cellAddresses.getFirstColumn();
            int lastColumn = cellAddresses.getLastColumn();
            if (firstRow <= row && row <= lastRow) {
                if (firstColumn <= column && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    String cellValue = getCellValue(fCell, formulaEvaluator);
                    return cellValue;
                }
            }

        }
//        int numMergedRegions = sheet.getNumMergedRegions();
//        for (int i = 0; i < numMergedRegions; i++) {
//            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
//            int firstRow = cellRangeAddress.getFirstRow();
//            int lastRow = cellRangeAddress.getLastRow();
//            int firstColumn = cellRangeAddress.getFirstColumn();
//            int lastColumn = cellRangeAddress.getLastColumn();
//            if (firstRow <= row && row <= lastRow) {
//                if (firstColumn <= column && column <= lastColumn) {
//                    Row fRow = sheet.getRow(firstRow);
////                    if (fRow == null) {
////                        continue;
////                    }
//                    Cell fCell = fRow.getCell(firstColumn);
////                    if (fCell == null) {
////                        continue;
////                    }
//                    String cellValue = getCellValue(fCell, formulaEvaluator);
//                    return cellValue;
//                }
//            }
//
//        }
        return null;

    }


    /**
     * 判断是否合并了列
     *
     * @param sheet  Sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    private static boolean isMergedCell(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            if (row == firstRow && row == lastRow) {
                if (firstColumn <= column && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }


}
