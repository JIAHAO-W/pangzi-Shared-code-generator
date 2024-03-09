package ${basePackage}.generator;


import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 静态代码和动态代码结合生成
 */
public class MainGenerator {

    public static void doGenerator(Object model) throws IOException, TemplateException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
<#list fileConfig.files as fileInfo>
        inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();

        <#if fileInfo.generateType == "dynamic">
        //生成动态文件
        DynamicGenerator.doGenerator(inputPath,outputPath,model);
        <#else >
        //生成静态文件
        StaticGenerator.doGenerator(inputPath,outputPath);
        </#if>

</#list>



    }
}
