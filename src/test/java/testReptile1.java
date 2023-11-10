import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.hzzzzzy.model.entity.Course;
import com.hzzzzzy.utils.WebClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author hzzzzzy
 * @date 2023/10/31
 * @description
 */
@Slf4j
public class testReptile1 {

    public static void main(String[] args) throws IOException {
        // 登录
        WebClient webClient = WebClientUtils.getWebClient();
        HtmlPage page = webClient.getPage("https://gateway.zhku.edu.cn/sso/login?service=https%3A%2F%2Fgateway.zhku.edu.cn%2Fsso%2Foauth2.0%2FcallbackAuthorize%3Fclient_name%3DCasOAuthClient%26client_id%3Djwgl100001%26redirect_uri%3Dhttps%253A%252F%252Fedu-admin.zhku.edu.cn%252FLogon.do%253Fmethod%253DlogonSSOzkgc%26response_type%3Dcode%26state%3DD1");
        HtmlInput username = page.getHtmlElementById("username");
        HtmlInput password = page.getHtmlElementById("password");
        username.setValueAttribute("xxxxxx");
        password.setValueAttribute("xxxxxx");
        HtmlAnchor loginButton = page.getFirstByXPath("//a[@id='submit']");
        page = loginButton.click();

        // 验证cookie
        CookieManager cookieManager = webClient.getCookieManager();
        Cookie cookie1 = cookieManager.getCookie("SERVERID");
        Cookie cookie2 = cookieManager.getCookie("JSESSIONID");
        if (cookie1 == null || cookie2 == null) {
            log.error("账号或密码错误");
        }

        // 跳转到教师端
        HtmlElement changeButton = page.getFirstByXPath("/html/body/div[2]/div[2]/div[1]/div[2]/div[4]/div/ul/li[2]");
        page = changeButton.click();

        // 跳转教师课表查询页面
        HtmlElement curriculum = page.getHtmlElementById("NEW_JSD_JXFW_WDKB_JSKBCS");
        page = curriculum.click();

        HtmlInput teacherName = page.getHtmlElementById("skjs");
        teacherName.setValueAttribute("郑建华");
        HtmlButton searchButton = page.getFirstByXPath("/html/body/div[1]/div/div[2]/form/div/div[9]/div[2]/button");
        page = searchButton.click();
        HtmlPage coursePage = page;
        Document doc = Jsoup.parse(page.asXml());
        // 提取课程信息
        Elements courseElements = doc.select("td[valign=top] div[class=kbcontent1]");
        List<Course> courseList = new ArrayList<>();
        for (Element courseElement : courseElements) {
            String courseInfo = courseElement.text();
            // 解析课程信息
            int count = 1; // 计数器(统计节数)
            String[] strings = courseInfo.split(" ");
            Course course = new Course();
            course.setCourseName(strings[0]);
            course.setClazz(strings[1]);
            course.setTeacherName(strings[2]);
            course.setWeekNumber(splitWeek(strings[3]));
            course.setClazzRoom(strings[4]);
            course.setClazzPeriods(getClassPeriods(count++));
            courseList.add(course);
        }
        courseList.forEach(System.out::println);

        teacherName = coursePage.getHtmlElementById("skjs");
        teacherName.setValueAttribute("刘双印");
        searchButton = coursePage.getFirstByXPath("/html/body/div[1]/div/div[2]/form/div/div[9]/div[2]/button");
        coursePage = searchButton.click();
        doc = Jsoup.parse(coursePage.asXml());
        // 提取课程信息
        courseElements = doc.select("td[valign=top] div[class=kbcontent1]");
        for (Element courseElement : courseElements) {
            String courseInfo = courseElement.text();
            // 解析课程信息
            int count = 1; // 计数器(统计节数)444444444444444444444444444444444444444444444444444445
            String[] strings = courseInfo.split(" ");
            Course course = new Course();
            course.setCourseName(strings[0]);
            course.setClazz(strings[1]);
            course.setTeacherName(strings[2]);
            course.setWeekNumber(splitWeek(strings[3]));
            course.setClazzRoom(strings[4]);
            course.setClazzPeriods(getClassPeriods(count++));
            courseList.add(course);
        }
        courseList.forEach(System.out::println);
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
     * 将周次进行分离
     *
     * @param originWeek 未分离的周次
     * @return List<第x周>
     */
    public static List<String> splitWeek(String originWeek) {
        // 比如(2-4,6-7周)
        // 去除第一个字符和尾部两个字符
        String week = originWeek.substring(1).substring(0, originWeek.length() - 3);
        ArrayList<String> list = new ArrayList<>();
        if (week.contains(",") && week.contains("-")) {
            String[] strings = week.split(",");
            for (int i = 0; i < strings.length; i++) {
                String[] number = strings[i].split("-");
                list.addAll(Arrays.asList(number));
            }
            // 比如(2-4周)
        } else if (week.contains("-")) {
            String[] number = week.split("-");
            list.addAll(Arrays.asList(number));
            // 比如(2周)或(8双周)
        } else {
            list.add(week);
        }
        return list;
    }
}