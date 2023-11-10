package com.hzzzzzy.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.hzzzzzy.model.dto.ReadExcelDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzzzzzy
 * @date 2023/11/3
 * @description
 */
@Slf4j
public class ReadExcelUtils {

    public static List<String> simpleRead() {
        ArrayList<String> list = new ArrayList<>();
        InputStream inputStream = ReadExcelUtils.class.getClassLoader().getResourceAsStream("teacherName.xls");
        EasyExcel.read(inputStream, ReadExcelDTO.class, new PageReadListener<ReadExcelDTO>(dataList -> {
            for (ReadExcelDTO dto : dataList) {
                String str = dto.getTeacherName().replaceAll(" ", "");
                list.add(str);
            }
        })).sheet().doRead();
        return list;
    }
}