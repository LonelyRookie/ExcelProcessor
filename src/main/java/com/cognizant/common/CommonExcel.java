package com.cognizant.common;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Abel
 * @date 2018/12/19 13:41
 */
public interface CommonExcel {

    Map<String, List<List<String>>> readExcelContent(File file,
                                                     Integer whichSheetBegin, Integer whichSheetStop,
                                                     Integer whichRowBegin, Integer whichRowStop,
                                                     Integer whichCellBegin, Integer whichCellStop);


    boolean checkExcelVaild(File file);

    Workbook getWorkbook(InputStream inputStream, File file);

    String getCellValue(Cell cell, FormulaEvaluator formulaEvaluator);

    String getCellFormulaValue(CellValue cellValue);

    boolean isMergedRegion(Sheet sheet, int row, int column);

    String getMergedRegionValue(Sheet sheet, int row, int column, FormulaEvaluator formulaEvaluator);

    FormulaEvaluator createFormulaEvaluator(Workbook workbook);

}
