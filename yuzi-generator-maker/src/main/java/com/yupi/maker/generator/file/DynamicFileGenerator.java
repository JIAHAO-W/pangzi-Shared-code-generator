package com.yupi.maker.generator.file;

import cn.hutool.core.io.FileUtil;
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
public class DynamicFileGenerator {

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

        //判断文件是否存在，不存在则创建文件和父目录
        if(!FileUtil.exist(outputPath)){
            FileUtil.touch(outputPath);
        }

        //指定生成文件
        Writer out = new FileWriter(outputPath);

        //生成文件
        template.process(model,out);
        //生成文件后关闭文件
        out.close();
    }
}
