package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.file.FileGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;


import java.util.concurrent.Callable;

/**
 * 代码生成器的核心命令，作用是接受参数并生成代码
 */
@CommandLine.Command(name = "generate",description = "生成代码",mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {

    <#list modelConfig.models as modelInfo>
    @CommandLine.Option(names = {<#if modelInfo.abbr ??>"-${modelInfo.abbr}"</#if>,"--${modelInfo.fieldName}"},arity = "0..1",<#if modelInfo.description??>description = ${modelInfo.description}</#if>,interactive = true,echo = true}
    private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??>= ${modelInfo.defaultValue?c}</#if>
    </#list>
    @Override
    public Integer call() throws Exception {
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        System.out.println("配置信息"+ dataModel);
        FileGenerator.doGenerator(dataModel);
        return 0;
    }
}
