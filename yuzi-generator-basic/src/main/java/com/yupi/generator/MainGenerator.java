package com.yupi.generator;

import com.yupi.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 静态代码和动态代码结合生成
 */
public class MainGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        //静态代码生成
        String projectPath = System.getProperty("user.dir");
        //projectPath:D:\JAVA\PROJECTS\YUPI\pangzi-generator
        String inputPath = projectPath + File.separator + "pangzi-generator-demo-projects"+
                File.separator + "acm-template";
        String outputPath = projectPath;
        File input = new File(inputPath);
        File ouput = new File(outputPath);
        StaticGenerator.copyFileByRecursive(input,ouput);
        System.out.println("静态代码复制完成");

        //动态代码生成

        String dynamicinputPath = projectPath + File.separator +"yuzi-generator-basic"+ File.separator  + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicoutputPath = outputPath + File.separator +"acm-template/src/com/yupi/acm"+File.separator + "MainTemplate.java";
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("JHWU");
        mainTemplateConfig.setOutputText("求和结果为");
        mainTemplateConfig.setLoop(false);

        DynamicGenerator.doGenerator(dynamicinputPath,dynamicoutputPath,mainTemplateConfig);
    }
}
