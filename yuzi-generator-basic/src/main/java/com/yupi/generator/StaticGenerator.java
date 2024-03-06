package com.yupi.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
public class StaticGenerator {
    public static void main(String[] args) throws IOException {
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        //得到父目录的绝对路径
        //File parentFile = new File(projectPath).getParentFile();
        String inputPath = projectPath + File.separator + "pangzi-generator-demo-projects"+
                File.separator + "acm-template";
        String outputPath = projectPath;
        System.out.println(inputPath);
//        doGenerator(inputPath,outputPath);

        File input = new File(inputPath);
        File ouput = new File(outputPath);
        copyFileByRecursive(input,ouput);
    }
    public static void doGenerator(String inputpath,String outputpath){
        FileUtil.copy(inputpath,outputpath,false);

    }
    public static void copyFileByRecursive(File inputFile,File outputFile) throws  IOException {
        //判断两个路径是否相等，相等则返回
        if (inputFile.equals(outputFile)){
            return;
        }
        //判断源文件的路径是否正确，错误直接返回
        if(!inputFile.exists()){
            return;
        }
        //判断是文件夹还是文件
        if (inputFile.isFile()){
            //文件的情况下直接复制
            outputFile = new File(outputFile,inputFile.getName());
            Files.copy(inputFile.toPath(),outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        }else {
            //文件夹的情况下先复制再递归
            //复制
            File file = new File(outputFile,inputFile.getName());
            file.mkdirs();
            //递归
            File[] files = inputFile.listFiles();
            for (File f : files) {
                copyFileByRecursive(f, file);
            }
        }
    }

}
