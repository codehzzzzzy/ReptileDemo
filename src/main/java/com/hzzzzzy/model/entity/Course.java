package com.hzzzzzy.model.entity;
import lombok.Data;

import java.util.List;

/**
 * @author hzzzzzy
 * @date 2023/11/2
 * @description
 */
@Data
public class Course {

    /**
     * 课程名称
     */
    String courseName;

    /**
     * 教学的班级
     */
    String clazz;

    /**
     * 星期
     */
    String dayOfWeek;

    /**
     * 教师名称
     */
    String teacherName;

    /**
     * 周数
     */
    List<String> weekNumber;

    /**
     * 教室
     */
    String clazzRoom;

    /**
     * 上课节数
     */
    String clazzPeriods;
}