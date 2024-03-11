package com.yupi.maker.generator.main;

import freemarker.template.TemplateException;
import java.io.IOException;

public class MainGenerator extends GenerateTemplate {

    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
        //不再输出简易版代码
        System.out.println("不要输出dist啦！");
    }


}
