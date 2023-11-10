package com.hzzzzzy.controller;

import com.gargoylesoftware.htmlunit.WebClient;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.hzzzzzy.constants.BusinessFailCode;
import com.hzzzzzy.exception.GlobalException;
import com.hzzzzzy.model.entity.Course;
import com.hzzzzzy.model.entity.Result;
import com.hzzzzzy.service.LoginService;
import com.hzzzzzy.service.ReptileService;
import com.hzzzzzy.utils.WebClientUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author hzzzzzy
 * @date 2023/11/2
 * @description 爬虫接口
 */
@RestController
@CrossOrigin
@RequestMapping("/reptile")
public class ReptileController {

    private final ExecutorService executorService = new ThreadPoolExecutor(
            1,
            32,
            1,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>()
    );
    private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();
    private final LoginService loginService;
    private final ReptileService reptileService;

    @ApiOperation("刷新课表")
    @PostMapping("doCourse/{teacherName}")
    public Result doCourse(
            @PathVariable("teacherName")
            String teacherName
    ) {
        // todo 参考job进行修改
        executorService.execute(() -> {
            // 确保一个老师当前只能有一条线程正在爬课
            synchronized (lockMap.computeIfAbsent(teacherName, item -> new Object())) {
                Thread.currentThread().setName(teacherName);
                try (WebClient webClient = WebClientUtils.getWebClient()) {
                    HtmlPage page = loginService.login(webClient);
                    reptileService.doCourse(webClient, page, teacherName);
                } catch (Exception e) {
                    throw new GlobalException(new Result<>().error(BusinessFailCode.PARAMETER_ERROR).message("抓取课表异常"));
                }
            }
        });
        return new Result<>().success().message("获取成功");
    }

    @ApiOperation("获取课表信息")
    @GetMapping("getCourse/{teacherName}")
    public Result getCourse(
            @PathVariable("teacherName")
            String teacherName
    ) {
        List<Course> courseList = reptileService.getCourse(teacherName);
        return new Result<>().success().message("获取成功").data(courseList);
    }




    public ReptileController(LoginService loginService, ReptileService reptileService) {
        this.loginService = loginService;
        this.reptileService = reptileService;
    }
}