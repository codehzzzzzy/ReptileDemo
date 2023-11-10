package com.hzzzzzy.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.hzzzzzy.constants.BusinessFailCode;
import com.hzzzzzy.exception.GlobalException;
import com.hzzzzzy.model.entity.Course;
import com.hzzzzzy.model.entity.Result;
import com.hzzzzzy.service.ReptileService;
import com.hzzzzzy.utils.ReptileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.hzzzzzy.constants.Common.SALT;
import static com.hzzzzzy.constants.RedisKeyPrefix.TEACHER_ALL_COURSE_CACHE_PREFIX;

/**
 * @author hzzzzzy
 * @date 2023/11/2
 * @description
 */
@Service
@Slf4j
public class ReptileServiceImpl implements ReptileService {

    private final ThreadPoolExecutor executor;
    private final StringRedisTemplate redisTemplate;

    public ReptileServiceImpl(ThreadPoolExecutor executor, StringRedisTemplate redisTemplate) {
        this.executor = executor;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doCourse(WebClient webClient, HtmlPage page, String teacherName) throws IOException {
        // 跳转到教师端
        HtmlElement changeButton = page.getFirstByXPath("/html/body/div[2]/div[2]/div[1]/div[2]/div[4]/div/ul/li[2]");
        page = changeButton.click();
        // 跳转教师课表查询页面
        HtmlElement curriculum = page.getHtmlElementById("NEW_JSD_JXFW_WDKB_JSKBCS");
        page = curriculum.click();
        HtmlInput teacherNameInput = page.getHtmlElementById("skjs");
        teacherNameInput.setValueAttribute(teacherName);
        HtmlButton searchButton = page.getFirstByXPath("/html/body/div[1]/div/div[2]/form/div/div[9]/div[2]/button");
        page = searchButton.click();
        // 提取课程信息
        Document doc = Jsoup.parse(page.asXml());
        List<Course> courseList = new ArrayList<>();
        executor.execute(()->{
            Elements courseElements = doc.select("td[valign=top] div[class=kbcontent1]");
            for (Element courseElement : courseElements) {
                String courseInfo = courseElement.text();
                // 解析课程信息
                int count = 1; // 计数器(统计节数)
                String[] strings = courseInfo.split(" ");
                Course course = new Course();
                course.setCourseName(strings[0]);
                course.setClazz(strings[1]);
                course.setTeacherName(strings[2]);
                course.setWeekNumber(ReptileUtils.splitWeekPlus(strings[3]));
                course.setClazzRoom(strings[4]);
                course.setClazzPeriods(ReptileUtils.getClassPeriods(count++));
                courseList.add(course);
            }
        });

        // 加密姓名
        String nameByDigest = DigestUtils.md5DigestAsHex((teacherName + SALT).getBytes(StandardCharsets.UTF_8));
        String key = TEACHER_ALL_COURSE_CACHE_PREFIX + nameByDigest;
        // 存在key 删除缓存 然后再次进行重建
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(courseList));
        log.info("爬虫结束 已存入缓存");
    }

    @SneakyThrows
    @Override
    public List<Course> getCourse(String teacherName) {
        // 加密名称
        String nameByDigest = DigestUtils.md5DigestAsHex((teacherName + SALT).getBytes(StandardCharsets.UTF_8));
        String jsonStr = redisTemplate.opsForValue().get(TEACHER_ALL_COURSE_CACHE_PREFIX + nameByDigest);
        if (StringUtils.isEmpty(jsonStr)){
            throw new GlobalException(new Result<>().error(BusinessFailCode.DATA_FETCH_ERROR).message("数据正在获取，请稍后再试"));
        }
        return JSONUtil.toBean(jsonStr, new TypeReference<List<Course>>() {}, false);
    }
}