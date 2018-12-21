package com.cognizant;

import com.cognizant.utils.DbUtil;
import com.cognizant.utils.ExcelUtil;
import com.cognizant.utils.PropertyUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Abel
 * @date 2018/12/11 10:39
 */
public class App {
    public static void main(String[] args) {

        while (true) {
            help();
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入操作命令：");
            String command = scanner.nextLine();

            if ("import".equalsIgnoreCase(command)) {
                try {
                    System.out.println();
                    String filePath = filePath(scanner);
                    String file_name = inputString(scanner, "文件名(包含后缀)", "文件名：www.xlsx");
                    System.out.println("===============================================================");
                    System.out.println();

                    String pathName = filePath + file_name;

                    Integer whichSheetBegin = inputInt(scanner, "从第几个sheet开始读取", "请从第1个sheet开始读取");
                    System.out.println();
                    Integer whichSheetStop = inputInt(scanner, "到第几个sheet页结束", "默认到最后一个。默认请输入【$】");
                    System.out.println();
                    Integer whichRowBegin = inputInt(scanner, "从第几行开始读取", "必须从列标题行开始读取。列标题行必须是英文且不重复");
                    System.out.println();
                    Integer whichRowStop = inputInt(scanner, "到第几行结束", "默认到最后一行。默认请输入【$】");
                    System.out.println();
                    Integer whichCellBegin = inputInt(scanner, "从第几列开始读取", "请从第1列开始读取");
                    System.out.println();
                    Integer whichCellStop = inputInt(scanner, "到第几列格结束", "默认到最后一列。默认请输入【$】");
                    System.out.println();

                    File file = new File(pathName);
                    String fileName = file.getName();
                    Map<String, List<List<String>>> listMap = ExcelUtil.readExcelContent(file, whichSheetBegin, whichSheetStop
                            , whichRowBegin, whichRowStop, whichCellBegin, whichCellStop);
                    importDataToDb(listMap, fileName);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    return;
                }


            } else if ("allImport".equalsIgnoreCase(command)) {
                String filePath = filePath(scanner);

                File[] files = new File(filePath).listFiles();
                for (File file : files) {
                    String fileName = file.getName();
                    Map<String, List<List<String>>> listMap = ExcelUtil.readExcelContent(file, 1, null, 1, null, 1, null);
                    importDataToDb(listMap, fileName);

                }
            } else if ("quit".equalsIgnoreCase(command)) {
                break;
            } else {
                System.out.println("命令不正确，请从新输入！");
                System.out.println();
            }
        }


    }


    public static void importDataToDb(Map<String, List<List<String>>> listMap, String fileName) {
        Set<String> keys = listMap.keySet();
        keys.stream().forEach(key -> {
            List<List<String>> listList = listMap.get(key);

            String sqlForCreateTable = DbUtil.sqlForCreateTable(listMap, key, fileName);
            DbUtil.createTable(sqlForCreateTable);

            String sqlForInsert = DbUtil.sqlForInsert(listMap, key, fileName);
            DbUtil.insertBatch(sqlForInsert, listList);

            System.out.println();
        });

//        for (String key : listMap.keySet()) {
//            List<List<String>> listList = listMap.get(key);
//
//            String sqlForCreateTable = DbUtil.sqlForCreateTable(listMap, key, fileName);
//            DbUtil.createTable(sqlForCreateTable);
//
//            String sqlForInsert = DbUtil.sqlForInsert(listMap, key, fileName);
//            DbUtil.insertBatch(sqlForInsert, listList);
//
////                    System.out.println("excel文件：" + fileName + "||" + "sheet页：" + key + "成功写入数据到数据库");
//            System.out.println();
//        }
    }


    public static String inputString(Scanner scanner, String title, String explain) {
        System.out.println(title + "：");
        System.out.println("提示：" + explain);
        String result = scanner.nextLine();
        return result;
    }

    public static Integer inputInt(Scanner scanner, String title, String explain) {
        System.out.println(title + "：");
        System.out.println("提示：" + explain);
        String result = scanner.nextLine();
        if ("$".equals(result)) {
            return null;
        }
        if (Integer.valueOf(result) < 0) {
            throw new RuntimeException("输入的值不为负数");
        }
        return Integer.valueOf(result);
    }

    public static void help() {
        System.out.println("******************************************************************************************************************");
        System.out.println();
        System.out.println("------------请输入命令，执行相关操作---------");
        System.out.println("*********** 1.quit 退出***********");
        System.out.println();
        System.out.println("*********** 2.import 从本地读取Excel文件写入数据库***********");
        System.out.println("使用【import】命令应注意：");
        System.out.println("                            1.必须从列标题行开始读取。列标题行必须是英文且不重复");
        System.out.println();
        System.out.println("*********** 3.allImport 一键从本地读取Excel文件写入数据库***********");
        System.out.println("使用【allImport】命令应注意：");
        System.out.println("                            1.必须从列标题行开始读取。列标题行必须是英文且不重复");
        System.out.println("                            2.列标题必须全部统一在sheet页的第一行");
        System.out.println("                            3.默认从第一个sheet页、第一个行、第一列依次读取");
        System.out.println();


        System.out.println("******************************************************************************************************************");

        System.out.println();
    }

    public static String filePath(Scanner scanner) {
        String filePath = "";
        String file_path = PropertyUtil.getProperty("file_path");
        if (file_path != null) {
            System.out.println("上一次保存路径是：" + file_path);
            System.out.println();
            String replace = inputString(scanner, "是否更换保存路径？", "y/n");
            System.out.println();
            if ("y".equalsIgnoreCase(replace)) {
                filePath = inputString(scanner, "输入文件路径:", "文件路径：C:/temps/");
                System.out.println();
                if (!filePath.endsWith("/")) {
                    filePath = filePath + "/";
                }
                PropertyUtil.setProperty("file_path", filePath);
                System.out.println("文件路径保存成功");
                System.out.println();
                System.out.println("更换后的路径：" + filePath);
                System.out.println();
            } else {
                return PropertyUtil.getProperty("file_path");
            }

        } else {
            filePath = inputString(scanner, "输入文件路径:", "文件路径：C:/temps/");
            System.out.println();
            String hint = inputString(scanner, "是否保存文件路径？", "y/n");
            System.out.println();
            if ("y".equalsIgnoreCase(hint)) {
                if (!filePath.endsWith("/")) {
                    filePath = filePath + "/";
                }
                PropertyUtil.setProperty("file_path", filePath);
                System.out.println("文件路径保存成功");
                System.out.println();
            }

        }
        return filePath;
    }

}
