import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author hzzzzzy
 * @date 2023/11/3
 * @description
 */
public class testDigest {
    public static void main(String[] args) {
        String str = DigestUtils.md5DigestAsHex("黄梓阳".getBytes(StandardCharsets.UTF_8));
        System.out.println(str);
    }
}
