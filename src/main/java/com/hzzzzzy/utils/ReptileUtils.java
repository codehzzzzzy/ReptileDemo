package com.hzzzzzy.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author hzzzzzy
 * @date 2023/11/2
 * @description
 */
public class ReptileUtils {

    /**
     * 将周次进行分离
     *
     * @param originWeek 未分离的周次
     * @return List<第x周>
     */
    public static List<Integer> splitWeek(String originWeek){
        // （比如2-4,6-7周）
        String week = originWeek.substring(0, originWeek.length() - 1);
        ArrayList<Integer> list = new ArrayList<>();
        if (week.contains(",") && week.contains("-")){
            String[] strings = week.split(",");
            for (int i = 0; i < strings.length; i++) {
                String[] number = strings[i].split("-");
                for (int j = 0; j < number.length; j++) {
                    list.add(Integer.parseInt(number[j]));
                }
            }
            // （比如2-4周）
        }else if (week.contains("-")){
            String[] number = week.split("-");
            list.add(Integer.parseInt(number[0]));
            list.add(Integer.parseInt(number[1]));
            // （比如2周）
        }else {
            list.add(Integer.parseInt(week));
        }
        return list;
    }


    /**
     * 将周次进行分离
     *
     * @param originWeek 未分离的周次
     * @return List<第x周>
     */
    public static List<String> splitWeekPlus(String originWeek) {
        // 比如(2-4,6-7周) --- 2,3,4,6,7
        // 去除第一个字符和尾部两个字符
        String week = originWeek.substring(1).substring(0, originWeek.length() - 3);
        ArrayList<String> list = new ArrayList<>();
        if (week.contains(",") && week.contains("-")) {
            String[] strings = week.split(",");
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].contains("-")){
                    String[] number = strings[i].split("-");
                    for (int j = Integer.parseInt(number[0]); j <= Integer.parseInt(number[1]); j++) {
                        list.add(String.valueOf(j));
                    }
                }else {
                    list.add(week);
                }
            }
            // 比如(2-4周)
        } else if (week.contains("-")) {
            String[] number = week.split("-");
            for (int j = Integer.parseInt(number[0]); j <= Integer.parseInt(number[1]); j++) {
                list.add(String.valueOf(j));
            }
            // 比如(2周)或(8双周)
        } else {
            list.add(week);
        }
        return list;
    }

    /**
     * 将1，2，3，4，5，6
     * 分别映射为
     * 第一二节、第三四节、第五节、第六七节、第八九节、第十十一十二节
     *
     * @param number 数字
     * @return 上课节数
     */
    public static String getClassPeriods(int number) {
        HashMap<Integer, String> numberToStringMap = new HashMap<>();
        numberToStringMap.put(1, "第一二节");
        numberToStringMap.put(2, "第三四节");
        numberToStringMap.put(3, "第五节");
        numberToStringMap.put(4, "第六七节");
        numberToStringMap.put(5, "第八九节");
        numberToStringMap.put(6, "第十十一十二节");
        return numberToStringMap.get(number);
    }

    /**
     * 将1，2，3，4，5，6
     * 分别映射为
     * 星期一、星期二、星期三、星期四、星期五、星期六、星期日
     *
     * @param number 数字
     * @return 星期x
     */
    public static String getDayOfWeek(int number) {
        HashMap<Integer, String> numberToStringMap = new HashMap<>();
        numberToStringMap.put(1, "星期一");
        numberToStringMap.put(2, "星期二");
        numberToStringMap.put(3, "星期三");
        numberToStringMap.put(4, "星期四");
        numberToStringMap.put(5, "星期五");
        numberToStringMap.put(6, "星期六");
        numberToStringMap.put(7, "星期日");
        return numberToStringMap.get(number);
    }
}
