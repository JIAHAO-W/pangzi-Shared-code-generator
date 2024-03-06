package com.yupi.generator;

import com.yupi.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * 动态文件生成
 */
public class DynamicGenerator {
    public static void main(String[] args) throws IOException, TemplateException {

        String projectPath = System.getProperty("user.dir")+ File.separator +"yuzi-generator-basic";
        System.out.println(projectPath);
        String inputPath = projectPath + File.separator  + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "MainTemplate.java";
        System.out.println(inputPath);
        System.out.println(outputPath);
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("JHWU");
        mainTemplateConfig.setOutputText("求和结果为");
        mainTemplateConfig.setLoop(false);

       doGenerator(inputPath,outputPath,mainTemplateConfig);
    }

    /**
     *
     * @param inputPath  模板文件输入路径
     * @param outputPath    输出路径
     * @param model 数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerator(String inputPath,String outputPath, Object model) throws IOException, TemplateException {
        //new出Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        // 指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        //设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        //创建模板对象，加载指定模板
        Template template = configuration.getTemplate(new File(inputPath).getName());

        //指定生成文件
        Writer out = new FileWriter(outputPath);

        //生成文件
        template.process(model,out);
        //生成文件后关闭文件
        out.close();
    }
}
