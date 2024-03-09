package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;

public class StaticGenerator {

    public static void doGenerator(String inputpath,String outputpath){
        FileUtil.copy(inputpath,outputpath,false);

    }

}
