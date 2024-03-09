package com.yupi.generator;

import com.yupi.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 静态代码和动态代码结合生成
 */
public class MainGenerator {

    public static void doGenerator(Object model) throws IOException, TemplateException {

//        //静态代码生成
//        String projectPath = System.getProperty("user.dir");
//        //projectPath:D:\JAVA\PROJECTS\YUPI\pangzi-generator
//        String inputPath = projectPath + File.separator + "pangzi-generator-demo-projects"+
//                File.separator + "acm-template";
//        String outputPath = projectPath;
//        File input = new File(inputPath);
//        File ouput = new File(outputPath);
//        StaticGenerator.copyFileByRecursive(input,ouput);
//        System.out.println("静态代码复制完成");
//
//        //动态代码生成
//
//        String dynamicinputPath = projectPath + File.separator +"yuzi-generator-basic"+ File.separator  + "src/main/resources/templates/MainTemplate.java.ftl";
//        String dynamicoutputPath = outputPath + File.separator +"acm-template/src/com/yupi/acm"+File.separator + "MainTemplate.java";
//
//        DynamicGenerator.doGenerator(dynamicinputPath,dynamicoutputPath,model);

        String inputRootPath = "D:\\JAVA\\PROJECTS\\YUPI\\pangzi-generator\\pangzi-generator-demo-projects\\acm-template-pro";
        String outputRootPath = "D:\\JAVA\\PROJECTS\\YUPI\\pangzi-generator\\acm-template-pro";

        String inputPath;
        String outputPath;

        inputPath = new File(inputRootPath,"src/com/yupi/acm/MainTemplate.java.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath,"src/com/yupi/acm/MainTemplate.java").getAbsolutePath();
        //生成动态文件
        DynamicGenerator.doGenerator(inputPath,outputPath,model);

        //生成静态文件
        inputPath = new File(inputRootPath,".gitignore").getAbsolutePath();
        outputPath = new File(outputRootPath,".gitignore").getAbsolutePath();
        StaticGenerator.doGenerator(inputPath,outputPath);

        inputPath = new File(inputRootPath,".README.md").getAbsolutePath();
        outputPath = new File(outputRootPath,".README.md").getAbsolutePath();
        StaticGenerator.doGenerator(inputPath,outputPath);


    }
}
