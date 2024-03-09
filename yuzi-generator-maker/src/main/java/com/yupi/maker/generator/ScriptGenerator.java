package com.yupi.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class ScriptGenerator {
    public static void doGenerate(String outputPath,String jarPath){
        //直接写入脚本文件
        //linux
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash").append("\n");
        sb.append(String.format("java -jar %s \"$@\"",jarPath)).append("\n");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8),outputPath);// TODO: 2024/3/9
        //针对linux添加可执行权限

        if (System.getProperty("os.name").toLowerCase().contains("nix")||System.getProperty("os.name").toLowerCase().contains("nux"))  {
            try {
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
                Files.setPosixFilePermissions(Paths.get(outputPath),permissions);
            } catch (IOException e) {
                System.out.println("权限错误");
            }
        }

        //windows权限
        sb = new StringBuilder();
        sb.append("@echo off").append("\n");
        sb.append(String.format("java -jar %s %%*",jarPath)).append("\n");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8),outputPath + ".bat");

    }


}
