import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMarkerTest {
    @Test
    public void test() throws IOException, TemplateException {
        //new出Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

//        System.out.println(System.getProperty("user.dir"));
        // 指定模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(new File("yuzi-generator-basic/src/main/resources/templates"));


        //设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        //创建模板对象，加载指定模板
        Template template = configuration.getTemplate("myweb.html.ftl");

        //数据模型
        Map<String,Object> dataModel = new HashMap<>();
        dataModel.put("currentYear",2023);
        List<Map<String,Object>> menuItems = new ArrayList<>();

        Map<String, Object> menuItem1 = new HashMap<>();
        menuItem1.put("url","http://codefather.cn");
        menuItem1.put("label","编程导航");

        Map<String, Object> menuItem2 = new HashMap<>();
        menuItem2.put("url","http://laoyujianli.com");
        menuItem2.put("label","老鱼简历");

        menuItems.add(menuItem1);
        menuItems.add(menuItem2);

        dataModel.put("menuItems",menuItems);

        //指定生成文件
        Writer out = new FileWriter("myweb.html");

        //生成文件
        template.process(dataModel,out);
        //生成文件后关闭文件
        out.close();
    }
}
