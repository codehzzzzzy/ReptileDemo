package com.hzzzzzy.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

/**
 * @author hzzzzzy
 * @date 2023/11/2
 * @description
 */
public interface LoginService {
    /**
     * 使用HtmlUnit进行登录
     *
     * @param webClient webClient对象
     * @throws Exception
     * @return
     */
    HtmlPage login(WebClient webClient) throws IOException;
}
