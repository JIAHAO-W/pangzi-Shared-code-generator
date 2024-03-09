package com.yupi.maker.generator.file;

import com.yupi.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 静态代码和动态代码结合生成
 */
public class FileGenerator {
//    public static void main(String[] args) throws TemplateException, IOException {
//        DataModel dataModel = new DataModel();
//        dataModel.setAuthor("JHWU");
//        dataModel.setOutputText("求和结果为");
//        dataModel.setLoop(false);
//
//        doGenerator(dataModel);
//    }
    public static void doGenerator(Object model) throws IOException, TemplateException {

        //静态代码生成
        String projectPath = System.getProperty("user.dir");
        //projectPath:D:\JAVA\PROJECTS\YUPI\pangzi-generator
        String inputPath = projectPath + File.separator + "pangzi-generator-demo-projects"+
                File.separator + "acm-template";
        String outputPath = projectPath;
        StaticFileGenerator.copyFileByHutool(inputPath,outputPath);

        //动态代码生成

        String dynamicinputPath = projectPath + File.separator +"yuzi-generator-maker"+ File.separator  + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicoutputPath = outputPath + File.separator +"acm-template/src/com/yupi/acm"+File.separator + "MainTemplate.java";

        DynamicFileGenerator.doGenerator(dynamicinputPath,dynamicoutputPath,model);
    }
}
