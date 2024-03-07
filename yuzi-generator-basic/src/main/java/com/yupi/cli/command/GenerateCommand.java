package com.yupi.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.yupi.generator.MainGenerator;
import com.yupi.model.MainTemplateConfig;
import lombok.Data;
import picocli.CommandLine;


import java.util.concurrent.Callable;

/**
 * 代码生成器的核心命令，作用是接受参数并生成代码
 */
@CommandLine.Command(name = "generate",description = "生成代码",mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-l","--loop"},arity = "0..1",description = "是否循环",interactive = true,echo = true)
    private boolean loop;

    @CommandLine.Option(names = {"-a","--author"},arity = "0..1",description = "作者",interactive = true,echo = true)
    private String author = "JHWU";

    @CommandLine.Option(names = {"-o","--outputText"},arity = "0..1",description = "输出文本",interactive = true,echo = true)
    private String outputText = "sum = ";
    @Override
    public Integer call() throws Exception {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        System.out.println("配置信息"+ mainTemplateConfig);
        MainGenerator.doGenerator(mainTemplateConfig);
        return 0;
    }
}
