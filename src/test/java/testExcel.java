import com.hzzzzzy.utils.ReadExcelUtils;

import java.util.List;

/**
 * @author hzzzzzy
 * @date 2023/11/3
 * @description
 */
public class testExcel {
    public static void main(String[] args) {
        List<String> list = ReadExcelUtils.simpleRead();
        list.forEach(System.out::println);
    }
}
