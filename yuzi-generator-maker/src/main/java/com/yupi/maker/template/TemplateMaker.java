package com.yupi.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.maker.meta.Meta;
import com.yupi.maker.meta.enums.FileGenerateTypeEnum;
import com.yupi.maker.meta.enums.FileTypeEnum;
import com.yupi.maker.template.enums.FileFilterRangeEnum;
import com.yupi.maker.template.enums.FileFilterRuleEnum;
import com.yupi.maker.template.model.FileFilterConfig;
import com.yupi.maker.template.model.TemplateMakerFileConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 */
public class TemplateMaker {
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, Meta.ModelConfigDTO.ModelsInfo modelsInfo , String searchStr, Long id) {
        //没有id ,则生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        //复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        //是否为首次制作模板
        // 目录不存在，则是首次制作
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(tempDirPath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }


        //一、输入信息
        //输入文件信息
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        //注意win系统需要对路径进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();

        //二、生成模板文件

        List<Meta.FileConfigDTO.FilesInfo> newFileInfoList = new ArrayList<>();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();
            if(!inputFilePath.startsWith(sourceRootPath)){
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            //获取过滤后的文件列表（不会存在目录）
            List<File> fileList = FileFilter.doFilter(inputFilePath,fileInfoConfig.getFilterConfigList());
            for (File file : fileList) {
                Meta.FileConfigDTO.FilesInfo fileInfo = makeFileTemplate(modelsInfo, searchStr, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        }

        //如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if(fileGroupConfig != null){
            String condition = fileGroupConfig.getCondition();
            String groupName = fileGroupConfig.getGroupName();
            String groupKey = fileGroupConfig.getGroupKey();

            //新增分组配置
            Meta.FileConfigDTO.FilesInfo groupFileInfo = new Meta.FileConfigDTO.FilesInfo();
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            //文件全部放在一个分组里
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
            
        }

        // 三、生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";
        //如果已有meta文件，说明不是第一次制作，则在meta基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta,oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            //1.追加配置参数
            List<Meta.FileConfigDTO.FilesInfo> filesInfoList = oldMeta.getFileConfig().getFiles();
            filesInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfigDTO.ModelsInfo> modelsInfoList = oldMeta.getModelConfig().getModels();
            modelsInfoList.add(modelsInfo);

            //配置去重
            oldMeta.getFileConfig().setFiles(distincFiles(filesInfoList));
            oldMeta.getModelConfig().setModels(distincModels(modelsInfoList));

        } else {
            //1. 构造配置参数

            Meta.FileConfigDTO fileConfigDTO = new Meta.FileConfigDTO();
            newMeta.setFileConfig(fileConfigDTO);
            fileConfigDTO.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfigDTO.FilesInfo> filesInfoList = new ArrayList<>();
            fileConfigDTO.setFiles(filesInfoList);


            filesInfoList.addAll(newFileInfoList);

            Meta.ModelConfigDTO modelConfigDTO = new Meta.ModelConfigDTO();
            newMeta.setModelConfig(modelConfigDTO);
            List<Meta.ModelConfigDTO.ModelsInfo> modelsInfoList = new ArrayList<>();
            modelConfigDTO.setModels(modelsInfoList);
            modelsInfoList.add(modelsInfo);



        }
        //2.输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);


        return id;
    }

    /**
     * 制作文件模板
     * @param modelsInfo
     * @param searchStr
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static Meta.FileConfigDTO.FilesInfo makeFileTemplate(Meta.ModelConfigDTO.ModelsInfo modelsInfo, String searchStr, String sourceRootPath, File inputFile) {
        //要挖坑的文件绝对路径（用于制作模板）
        //注意：win系统需要对路径进行转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\","/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入输出相对路径（用于生成配置）
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath +"/","");
        String fileOutputPath = fileInputPath + ".ftl";

        //使用字符串替换，生成模板文件
        String fileContent = null;
        //如果已有模板文件，说明不是第一次制作，则在模板基础上再次挖坑
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        String replacement = String.format("${%s}", modelsInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        //文件配置信息
        Meta.FileConfigDTO.FilesInfo filesInfo = new Meta.FileConfigDTO.FilesInfo();
        filesInfo.setInputPath(fileInputPath);
        filesInfo.setOutputPath(fileOutputPath);
        filesInfo.setType(FileTypeEnum.FILE.getValue());

        //和原文件一致，没有挖坑，则为静态生成
        if(newFileContent.equals(fileContent)){
            //输出路径 = 输入路径
            filesInfo.setOutputPath(fileInputPath);
            filesInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        }else {
            // 生成模板文件
            filesInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return filesInfo;
    }

    /**
     * 文件去重
     * @param filesInfoList
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesInfo> distincFiles(List<Meta.FileConfigDTO.FilesInfo> filesInfoList) {
        //1.将所有文件配置（fileInfo）分为有分组和无分组的
        //先处理有分组的文件
        //{"groupKey":"a",files:[1,2]},{"groupKey":"a",files:[2,3]},{"groupKey":"b",files:[4,5]}
        //先转换为{"groupKey":"a",files:[[1,2],[2,3]]},{"groupKey":"b",files:[[4,5]]}
        Map<String, List<Meta.FileConfigDTO.FilesInfo>> groupKeyFileInfoListMap = filesInfoList.stream()
                .filter(filesInfo -> StrUtil.isNotBlank(filesInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.FileConfigDTO.FilesInfo::getGroupKey)
                );

        //2.对于有分组的文件配置，如果有相同的分组，同分组内的文件进行合并(merge)，不同分组可同时保留
        //同组内配置合并
        //{"groupKey":"a",files:[[1,2],[2,3]]}
        //{"groupKey":"a",files:[1,2,2,3]}
        //{"groupKey":"a",files:[1,2,3]}
        Map<String,Meta.FileConfigDTO.FilesInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfigDTO.FilesInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfigDTO.FilesInfo> tempFileInfoList = entry.getValue();
            //[1,2,2,3]
            List<Meta.FileConfigDTO.FilesInfo> newFileInfoList =new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(filesInfo -> filesInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(Meta.FileConfigDTO.FilesInfo::getInputPath, o -> o, (e, r) -> r)
                    ).values());

            //使用新的group配置
            Meta.FileConfigDTO.FilesInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey,newFileInfo);

        }

        //3.创建新的文件配置列表（结果列表），先将合并后的分组添加到结果列表中
        ArrayList<Meta.FileConfigDTO.FilesInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        //4.再将无分组的文件配置列表添加到结果列表
        resultList.addAll(new ArrayList<>(filesInfoList.stream()
                .filter(filesInfo -> StrUtil.isBlank(filesInfo.getGroupKey()))
                .collect(
                        Collectors.toMap(Meta.FileConfigDTO.FilesInfo::getInputPath, o -> o, (e, r) -> r)
                ).values()));
        return resultList;
    }

    /**
     * 模型去重
     * @param ModelsInfoList
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsInfo> distincModels(List<Meta.ModelConfigDTO.ModelsInfo> ModelsInfoList) {


        List<Meta.ModelConfigDTO.ModelsInfo> newModelInfoList = new ArrayList<>(ModelsInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfigDTO.ModelsInfo::getFieldName, o -> o, (e, r) -> r)
                ).values()
        );
        return newModelInfoList;
        
    }

    
    public static void main(String[] args) {

        Meta meta =new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "pangzi-generator-demo-projects/springboot-init";
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/controller";
        List<String> inputFilePathList = Arrays.asList(inputFilePath1,inputFilePath2);


        //输入模型参数信息(首次)
//        Meta.ModelConfigDTO.ModelsInfo modelsInfo = new Meta.ModelConfigDTO.ModelsInfo();
//        modelsInfo.setFieldName("outputText");
//        modelsInfo.setType("String");
//        modelsInfo.setDefaultValue("sum = ");
        //输入模型参数信息（第二次）
        Meta.ModelConfigDTO.ModelsInfo modelsInfo = new Meta.ModelConfigDTO.ModelsInfo();
        modelsInfo.setFieldName("className");
        modelsInfo.setType("String");


//        String searchStr = "Sum: ";
        String searchStr = "BaseResponse";

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        List<FileFilterConfig> filterConfigList = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        filterConfigList.add(fileFilterConfig);
        fileInfoConfig1.setFilterConfigList(filterConfigList);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig1,fileInfoConfig2);

        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(fileInfoConfigList);

        //分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("测试分组");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);


        System.out.println(templateMakerFileConfig.toString());

        long id = makeTemplate(meta,originProjectPath,templateMakerFileConfig,modelsInfo,searchStr,null);
        System.out.println(id);


    }
}
