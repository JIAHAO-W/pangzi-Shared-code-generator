package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

/**
 * 作用是用来遍历输出所有要生成的文件列表
 */
@CommandLine.Command(name = "list",description = "查看文件列表",mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{
    @Override
    public void run() {

        //输入路径
        String inputPath = "${fileConfig.inputRootPath}";

        //直接使用Hutool库提供的FileUtil.loopFiles(inputPath) 方法来遍历该目录下的所有文件即可。
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }

    }
}
