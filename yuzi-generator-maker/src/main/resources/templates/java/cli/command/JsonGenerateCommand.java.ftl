package ${basePackage}.cli.command;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;


/**
 * 读取JSON 文件生成代码
 */
@CommandLine.Command(name = "json-generate", description = "读取JSON文件生成代码", mixinStandardHelpOptions = true)
@Data
public class JsonGenerateCommand implements Callable<Integer> {


    @CommandLine.Option(names = {"-f", "--file"}, arity = "0..1", description = "JSON文件路径", interactive = true, echo = true)
    private String filePath ;


    public Integer call() throws Exception {
        //读取json文件，转换为数据类型
        String jsonStr = FileUtil.readUtf8String(filePath);
        DataModel dataModel = JSONUtil.toBean(jsonStr, DataModel.class);
        MainGenerator.doGenerator(dataModel);
        return 0;
    }
}