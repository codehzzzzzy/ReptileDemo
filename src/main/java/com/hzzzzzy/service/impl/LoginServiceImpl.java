package com.hzzzzzy.service.impl;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.hzzzzzy.constants.BusinessFailCode;
import com.hzzzzzy.exception.GlobalException;
import com.hzzzzzy.model.entity.Result;
import com.hzzzzzy.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author hzzzzzy
 * @date 2023/11/2
 * @description
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Value("${teacher.account}")
    private String account;
    @Value("${teacher.pwd}")
    private String pwd;

    @Override
    public HtmlPage login(WebClient webClient) throws IOException {
        HtmlPage page = webClient.getPage("https://gateway.zhku.edu.cn/sso/login?service=https%3A%2F%2Fgateway.zhku.edu.cn%2Fsso%2Foauth2.0%2FcallbackAuthorize%3Fclient_name%3DCasOAuthClient%26client_id%3Djwgl100001%26redirect_uri%3Dhttps%253A%252F%252Fedu-admin.zhku.edu.cn%252FLogon.do%253Fmethod%253DlogonSSOzkgc%26response_type%3Dcode%26state%3DD1");
        HtmlInput username = page.getHtmlElementById("username");
        HtmlInput password = page.getHtmlElementById("password");
        username.setValueAttribute(account);
        password.setValueAttribute(pwd);
        HtmlAnchor loginButton = page.getFirstByXPath("//a[@id='submit']");
        page = loginButton.click();

        // 验证cookie
        CookieManager cookieManager = webClient.getCookieManager();
        Cookie cookie1 = cookieManager.getCookie("SERVERID");
        Cookie cookie2 = cookieManager.getCookie("JSESSIONID");
        if (cookie1 == null || cookie2 == null) {
            throw new GlobalException(new Result<>().error(BusinessFailCode.PARAMETER_ERROR).message("账号不存在或密码错误"));
        }
        return page;
    }
}
