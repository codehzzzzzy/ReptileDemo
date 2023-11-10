package com.hzzzzzy.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.hzzzzzy.model.entity.Course;

import java.io.IOException;
import java.util.List;

/**
 * @author hzzzzzy
 * @date 2023/11/2
 * @description
 */
public interface ReptileService {

    /**
     * 爬取课表
     *
     * @param webClient webClient对象
     * @param page 登陆后页面
     * @param teacherName 教师名称
     */
    void doCourse(WebClient webClient, HtmlPage page, String teacherName) throws IOException;


    /**
     * 通过缓存获取爬取的结果
     * @param teacherName 老师名称
     * @return
     */
    List<Course> getCourse(String teacherName);
}
