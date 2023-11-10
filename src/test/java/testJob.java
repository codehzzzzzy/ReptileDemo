import cn.hutool.json.JSONUtil;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.hzzzzzy.constants.BusinessFailCode;
import com.hzzzzzy.exception.GlobalException;
import com.hzzzzzy.model.entity.Course;
import com.hzzzzzy.model.entity.Result;
import com.hzzzzzy.utils.ReadExcelUtils;
import com.hzzzzzy.utils.ReptileUtils;
import com.hzzzzzy.utils.WebClientUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hzzzzzy.constants.Common.SALT;
import static com.hzzzzzy.constants.RedisKeyPrefix.TEACHER_ALL_COURSE_CACHE_PREFIX;

/**
 * @author hzzzzzy
 * @date 2023/11/3
 * @description
 */
@Slf4j
@Component
public class testJob {

    public static ThreadPoolExecutor getThreadPoolExecutor(){
        return new ThreadPoolExecutor(1,36,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
    }

    @Test
    public void test1() throws IOException {
        List<String> keyList = new ArrayList<>();
        ThreadPoolExecutor executor = getThreadPoolExecutor();
        log.info("缓存预热开始");
        // 登录
        WebClient webClient = WebClientUtils.getWebClient();
        HtmlPage page = webClient.getPage("https://gateway.zhku.edu.cn/sso/login?service=https%3A%2F%2Fgateway.zhku.edu.cn%2Fsso%2Foauth2.0%2FcallbackAuthorize%3Fclient_name%3DCasOAuthClient%26client_id%3Djwgl100001%26redirect_uri%3Dhttps%253A%252F%252Fedu-admin.zhku.edu.cn%252FLogon.do%253Fmethod%253DlogonSSOzkgc%26response_type%3Dcode%26state%3DD1");
        HtmlInput username = page.getHtmlElementById("username");
        HtmlInput password = page.getHtmlElementById("password");
        username.setValueAttribute("");
        password.setValueAttribute("");
        HtmlAnchor loginButton = page.getFirstByXPath("//a[@id='submit']");
        page = loginButton.click();
        // 验证cookie
        CookieManager cookieManager = webClient.getCookieManager();
        Cookie cookie1 = cookieManager.getCookie("SERVERID");
        Cookie cookie2 = cookieManager.getCookie("JSESSIONID");
        if (cookie1 == null || cookie2 == null) {
            throw new GlobalException(new Result<>().error(BusinessFailCode.PARAMETER_ERROR).message("账号不存在或密码错误"));
        }
        // 跳转到教师端
        HtmlElement changeButton = page.getFirstByXPath("/html/body/div[2]/div[2]/div[1]/div[2]/div[4]/div/ul/li[2]");
        page = changeButton.click();
        // 跳转教师课表查询页面
        HtmlElement curriculum = page.getHtmlElementById("NEW_JSD_JXFW_WDKB_JSKBCS");
        HtmlPage searchPage = curriculum.click();
        // 解析Excel数据
        List<String> list = ReadExcelUtils.simpleRead();
        for (String teacherName : list) {
            // 在页面加载完成前等待
//            webClient.waitForBackgroundJavaScript(1000);
            // 查找输入框元素
            HtmlInput teacherNameInput = searchPage.getHtmlElementById("skjs");
            teacherNameInput.setValueAttribute(teacherName);
            HtmlButton searchButton = searchPage.getFirstByXPath("/html/body/div[1]/div/div[2]/form/div/div[9]/div[2]/button");
            HtmlPage xmlPage = searchButton.click();

            // 提取课程信息
            Document doc = Jsoup.parse(xmlPage.asXml());
            List<Course> courseList = new ArrayList<>();
            executor.execute(() -> {
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
                // 加密姓名
                String nameByDigest = DigestUtils.md5DigestAsHex((teacherName + SALT).getBytes(StandardCharsets.UTF_8));
                String key = TEACHER_ALL_COURSE_CACHE_PREFIX + nameByDigest;
                // 存在key 删除缓存 然后再次进行重建
//                if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
//                    redisTemplate.delete(key);
//                }
//                redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(courseList));
                keyList.add(key);
            });
        }
        // 关闭线程池
        executor.shutdown();
        while (!executor.isTerminated()) {
            // 等待线程池任务执行完毕
        }
        log.info("缓存预热结束");
        for (String key : keyList) {
            System.out.println("key = " + key);
        }
    }
}
