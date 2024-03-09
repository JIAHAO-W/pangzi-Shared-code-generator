package com.yupi.maker.generator.file;

import cn.hutool.core.io.FileUtil;

public class StaticFileGenerator {

    public static void copyFileByHutool(String inputpath,String outputpath){
        FileUtil.copy(inputpath,outputpath,false);

    }

}
