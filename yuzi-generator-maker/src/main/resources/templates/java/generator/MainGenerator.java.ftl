package ${basePackage}.generator;

import com.yupi.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "dynamic">
${indent}DynamicGenerator.doGenerator(inputPath,outputPath,model);
<#else >
${indent}StaticGenerator.doGenerator(inputPath,outputPath);
</#if>
</#macro>

/**
 * 静态代码和动态代码结合生成
 */
public class MainGenerator {

    public static void doGenerator(DataModel model) throws IOException, TemplateException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

        <#--获取模型变量-->
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey ??>
        <#list modelInfo.models as subModelInfo>
        ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
        </#list>
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
        </#list>

<#list fileConfig.files as fileInfo>
        <#if fileInfo.groupKey??>
        <#if fileInfo.condition ??>
        if(${fileInfo.condition}){
            <#list fileInfo.files as fileInfo>
            <@generateFile fileInfo=fileInfo indent="            "/>
            </#list>
        }

        <#else>
        <#list fileInfo.files as fileInfo>
        <@generateFile fileInfo=fileInfo indent="        "/>
        </#list>
        </#if>
        <#else >
        <#if fileInfo.condition ??>
        if(${fileInfo.condition}){
            <@generateFile fileInfo=fileInfo indent="            "/>
            }
        <#else>
        <@generateFile fileInfo=fileInfo indent="        "/>
        </#if>
        </#if>
</#list>

    }
}
