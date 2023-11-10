package com.hzzzzzy.utils;

import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hzzzzzy
 * @date 2023/11/6
 * @description 转换 日期 --- 周数 工具类
 */
public class TimeParseUtils {

    @SneakyThrows
    public static String getDate()  {
        String schoolStartDateStr = "2023-09-04";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date schoolStartDate = dateFormat.parse(schoolStartDateStr);
        // 当前日期
        Date currentDate = new Date();
        // 计算日期差值
        long diffInMilliseconds = currentDate.getTime() - schoolStartDate.getTime();
        long diffInDays = diffInMilliseconds / (24 * 60 * 60 * 1000);
        // 计算当前是第几周
        int currentWeek = (int) (diffInDays / 7) + 1;
        return String.valueOf(currentWeek);
    }
}