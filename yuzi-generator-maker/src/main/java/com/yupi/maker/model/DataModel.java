package com.yupi.maker.model;


import lombok.Data;

/**
 * 动态配置模板
 */
@Data
public class DataModel {


    //是否循环
    public boolean loop = false;

    //是否生成.gitignore文件
    public boolean needGit = true;

    public MainTemplate mainTemplate = new MainTemplate();

    /**
     * 核心模板
     */
    @Data
    public static class MainTemplate{
        /**
         * 作者注释
         */
        public String author = "JHWU";
        /**
         * 输出信息
         */
        public String outputText = "sum = ";
    }
}
