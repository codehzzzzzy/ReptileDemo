import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.hzzzzzy.utils.WebClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author hzzzzzy
 * @date 2023/10/31
 * @description
 */
@Slf4j
public class testReptile2 {

    public static void main(String[] args) throws IOException {
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

        // day: 星期x
        // clazz-1: 第x节课
        ArrayList<String> list = new ArrayList<>();
        for (int day = 1; day < 7; day++) {
            for (int clazz = 2; clazz < 7; clazz++) {
                String xpath = "/html/body/div[2]/table[1]/tbody/tr[" + clazz + "]/td[" + day + "]/div[1]";
                HtmlElement ele = page.getFirstByXPath(xpath);
                String htmlContent = ele.asXml();
                Document document = Jsoup.parse(htmlContent);
                // 查找课程名称
                Element firstFont = document.select("font").first();
                if (firstFont != null) {
                    list.add("星期" + day + " " + getClassTime(clazz - 1) + "课");
                    list.add("课程名称: " + firstFont.text());
                    // 查找课程编号
                    Element courseCodeElement = document.select("font[name=kch]").first();
                    list.add("课程编号: " + courseCodeElement.text());
                    // 查找教室
                    Element fontElement = document.select("font[title=教室]").first();
                    list.add("教室: " + fontElement.text());
                    // 查找周次
                    Element weekElement = document.select("font[title=周次(节次)]").first();
                    List<Integer> week = splitWeek(weekElement.text());
                    list.add("周次: " + week);
                    list.add("");
                }
            }
        }
        for (String str : list) {
            System.out.println(str);
        }

    }

    /**
     * 将周次进行分离
     *
     * @param originWeek 未分离的周次
     * @return List<第x周>
     */
    public static List<Integer> splitWeek(String originWeek) {
        // （比如2-4,6-7周）
        String week = originWeek.substring(0, originWeek.length() - 1);
        ArrayList<Integer> list = new ArrayList<>();
        if (week.contains(",") && week.contains("-")) {
            String[] strings = week.split(",");
            for (int i = 0; i < strings.length; i++) {
                String[] number = strings[i].split("-");
                for (int j = 0; j < number.length; j++) {
                    list.add(Integer.parseInt(number[j]));
                }
            }
            // （比如2-4周）
        } else if (week.contains("-")) {
            String[] number = week.split("-");
            list.add(Integer.parseInt(number[0]));
            list.add(Integer.parseInt(number[1]));
            // （比如2周）
        } else {
            list.add(Integer.parseInt(week));
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
    public static String getClassTime(int number) {
        HashMap<Integer, String> numberToStringMap = new HashMap<>();
        numberToStringMap.put(1, "第一二节");
        numberToStringMap.put(2, "第三四节");
        numberToStringMap.put(3, "第五节");
        numberToStringMap.put(4, "第六七节");
        numberToStringMap.put(5, "第八九节");
        numberToStringMap.put(6, "第十十一十二节");
        return numberToStringMap.get(number);
    }
}