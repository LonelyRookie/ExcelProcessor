package com.cognizant.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Abel
 * @date 2018/12/11 11:22
 */
public class CommonUtil {

    /**
     * 判断是否为空
     *
     * @param o
     * @return
     */
    public static boolean isEmpty(Object o) {
        boolean flag = true;
        if (o == null || "".equals(o)) {
            return flag;
        }
        flag = false;
        return flag;
    }

    /**
     * long类型时间转换成字符串
     *
     * @param time
     * @return
     */
    public static String dateToStr(long time) {
        Date d = new Date(time);
        String pattern = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(d);
    }

    /**
     * Date类型转成字符串
     *
     * @param date
     * @return String
     */
    public static String formatDate(Date date) {
        String pattern = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 格式化字符串
     * 把‘空格’、‘&’、‘(’、‘)’、‘-’过滤为‘_’
     *
     * @param splits
     * @return
     */
    public static String formatString(String splits) {
        StringBuilder formatSb = new StringBuilder();
        String[] split = splits.split(" ");
        for (String str : split) {
            //过滤中文
            String notChinese = filterChinese(str);

            StringBuilder sb = new StringBuilder();

            char[] chars = notChinese.toCharArray();
            for (char ch : chars) {
//                ~!#%^&*=+\\|{};:'\",<>/?○●★☆☉♀♂※¤╬の〆
                if ('.' == ch || '&' == ch || '(' == ch || ')' == ch || '-' == ch || '/' == ch || '\\' == ch || '。' == ch ||
                        '%' == ch || '*' == ch || '=' == ch || '+' == ch || '!' == ch || '#' == ch || ',' == ch ||
                        '|' == ch || '?' == ch || ':' == ch || ';' == ch || '<' == ch || '>' == ch || '{' == ch || '}' == ch) {
                    sb.append('_');
                    continue;
                }
                if ('\n' == ch) {
                    continue;
                }
                sb.append(ch);
            }
            String filterStr = sb.toString();
//            System.out.println(filterStr);

            formatSb.append(filterStr);
            formatSb.append("_");
        }
        String value = formatSb.toString().toLowerCase().trim();
//        System.out.println(value);
        return value;
    }


    /**
     * 判断一个字符串是否都为数字
     *
     * @param str
     * @return
     */
    public static boolean isDigit1(String str) {
        return str.matches("[0-9]{1,}");
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigit2(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }


    /**
     * 截取数字
     *
     * @param content
     * @return
     */
    public String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }


    /**
     * 截取非数字
     *
     * @param content
     * @return
     */
    public static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 验证字符串内容是否包含下列非法字符<br>
     * `~!#%^&*=+\\|{};:'\",<>/?○●★☆☉♀♂※¤╬の〆
     * 如果包含，返回非法字符
     *
     * @param content 字符串内容
     * @return 't'代表不包含非法字符，otherwise代表包含非法字符。
     */
    public static char validateLegalString(String content) {
        String illegal = "`~!#%^&*=+\\|{};:'\",<>/?○●★☆☉♀♂※¤╬の〆";
        char isLegalChar = 't';
        L1:
        for (int i = 0; i < content.length(); i++) {
            for (int j = 0; j < illegal.length(); j++) {
                if (content.charAt(i) == illegal.charAt(j)) {
                    isLegalChar = content.charAt(i);
                    break L1;
                }
            }
        }
        return isLegalChar;
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 校验一个字符是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判定输入的是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 过滤掉中文
     *
     * @param str 待过滤中文的字符串
     * @return 过滤掉中文后字符串
     */
    public static String filterChinese(String str) {
        // 用于返回结果
        String result = str;
        boolean flag = isContainChinese(str);//判断是否包含中文
        if (flag) {// 包含中文
            StringBuffer sb = new StringBuffer();
            // 用于校验是否为中文
            boolean flag2 = false;
            // 用于临时存储单字符
            char chinese = 0;
            // 5.去除掉文件名中的中文
            char[] charArray = str.toCharArray();
            // 过滤到中文及中文字符
            for (int i = 0; i < charArray.length; i++) {
                chinese = charArray[i];
                flag2 = isChinese(chinese);
                if (!flag2) {// 不是中日韩文字及标点符号
                    sb.append(chinese);
                }
            }
            result = sb.toString();
        }
        return result;
    }


}
