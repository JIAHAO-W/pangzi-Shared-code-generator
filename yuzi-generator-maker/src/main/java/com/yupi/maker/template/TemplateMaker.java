package com.yupi.maker.template;
import com.yupi.maker.meta.Meta.ModelConfigDTO;
import com.yupi.maker.meta.Meta.FileConfigDTO;

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
import com.yupi.maker.template.model.TemplateMakerConfig;
import com.yupi.maker.template.model.TemplateMakerFileConfig;
import com.yupi.maker.template.model.TemplateMakerModelConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 */
public class TemplateMaker {

    public static long makeTemplate(TemplateMakerConfig templateMakerConfig){
     Long id = templateMakerConfig.getId();
     Meta meta = templateMakerConfig.getMeta();
     String originProjectPath = templateMakerConfig.getOriginProjectPath();
     TemplateMakerFileConfig fileConfig = templateMakerConfig.getFileConfig();
     TemplateMakerModelConfig modelConfig = templateMakerConfig.getModelConfig();
     return makeTemplate(meta,originProjectPath,fileConfig,modelConfig,id);
    }
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, Long id) {
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
        //输入文件信息，获取到项目根目录

        String sourceRootPath = FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();

        //注意win系统需要对路径进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();

        //二、生成模板文件
        List<FileConfigDTO.FilesInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);

        //处理模型信息
        List<ModelConfigDTO.ModelsInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);

        // 三、生成配置文件
        String metaOutputPath = templatePath + File.separator + "meta.json";
        //如果已有meta文件，说明不是第一次制作，则在meta基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta,oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            //1.追加配置参数
            List<Meta.FileConfigDTO.FilesInfo> filesInfoList = newMeta.getFileConfig().getFiles();
            filesInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfigDTO.ModelsInfo> modelsInfoList = newMeta.getModelConfig().getModels();
            modelsInfoList.addAll(newModelInfoList);

            //配置去重
            newMeta.getFileConfig().setFiles(distincFiles(filesInfoList));
            newMeta.getModelConfig().setModels(distincModels(modelsInfoList));

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
            modelsInfoList.addAll(newModelInfoList);



        }
        //2.输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);


        return id;
    }

    /**
     * 生成模型配置文件
     * @param templateMakerModelConfig
     * @return
     */
    private static List<ModelConfigDTO.ModelsInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        //本次新增的模型配置列表
        List<ModelConfigDTO.ModelsInfo> newModelInfoList = new ArrayList<>();
        if(templateMakerModelConfig == null)return newModelInfoList;

        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if (CollUtil.isEmpty(models))return newModelInfoList;

        //-转换为配置接受的ModelsInfo对象
        List<ModelConfigDTO.ModelsInfo> inputModelInfoList = models
                .stream()
                .map(modelInfoConfig -> {
                    ModelConfigDTO.ModelsInfo modelsInfo = new ModelConfigDTO.ModelsInfo();
                    BeanUtil.copyProperties(modelInfoConfig,modelsInfo);
                    return modelsInfo;
                }
                ).collect(Collectors.toList());

        //如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if(modelGroupConfig != null){
            String condition = modelGroupConfig.getCondition();
            String groupName = modelGroupConfig.getGroupName();
            String groupKey = modelGroupConfig.getGroupKey();

            //新增分组配置
            ModelConfigDTO.ModelsInfo groupModelInfo = new ModelConfigDTO.ModelsInfo();
            groupModelInfo.setGroupKey(groupKey);
            groupModelInfo.setGroupName(groupName);
            groupModelInfo.setCondition(condition);
            //模型全部放在一个分组里
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        }else {
            //不分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /**
     * 生成文件配置
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param sourceRootPath
     *
     * @return
     */
    private static List<FileConfigDTO.FilesInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        List<FileConfigDTO.FilesInfo> newFileInfoList = new ArrayList<>();
        //非空校验
        if(templateMakerFileConfig == null)return newFileInfoList;

        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        if(CollUtil.isEmpty(fileConfigInfoList))return newFileInfoList;
        //生成模板文件
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();
            if(!inputFilePath.startsWith(sourceRootPath)){
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            //获取过滤后的文件列表（不会存在目录）
            List<File> fileList = FileFilter.doFilter(inputFilePath,fileInfoConfig.getFilterConfigList());
            //不处理已经生成的FIL模板文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());
            for (File file : fileList) {
                FileConfigDTO.FilesInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file);
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
            FileConfigDTO.FilesInfo groupFileInfo = new FileConfigDTO.FilesInfo();
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            //文件全部放在一个分组里
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
            
        }
        return newFileInfoList;
    }

    /**
     * 制作文件模板
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static Meta.FileConfigDTO.FilesInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File inputFile) {
        //要挖坑的文件绝对路径（用于制作模板）
        //注意：win系统需要对路径进行转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\","/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入输出相对路径（用于生成配置）
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath +"/","");
        String fileOutputPath = fileInputPath + ".ftl";

        //使用字符串替换，生成模板文件
        String fileContent = null;

        //判断模板文件是否存在
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        //如果已有模板文件，说明不是第一次制作，则在模板基础上再次挖坑
        if (hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        //支持多个模型：对同一个文件的内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            //不是分组
            if(modelGroupConfig == null){
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            }else {
                //是分组
                String groupKey = modelGroupConfig.getGroupKey();
                //注意要多挖一个层级
                replacement = String.format("${%s.%s}", groupKey,modelInfoConfig.getFieldName());
            }
            //多次替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }


        //文件配置信息
        Meta.FileConfigDTO.FilesInfo filesInfo = new Meta.FileConfigDTO.FilesInfo();
        filesInfo.setInputPath(fileOutputPath);
        filesInfo.setOutputPath(fileInputPath);
        filesInfo.setType(FileTypeEnum.FILE.getValue());
        //默认设置生成类型为动态
        filesInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        //是否更改了文件内容
        boolean contentEquals = newFileContent.equals(fileContent);
        //和原文件一致，没有挖坑，则为静态生成
        //之前不存在模板文件，并且没有更改过文件内容，则为静态生成
        if (!hasTemplateFile) {
            if(contentEquals){
                //输出路径 = 输入路径
                filesInfo.setInputPath(fileInputPath);
                filesInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            }else {
                // 没有模板文件，需要挖坑，生成模板文件
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else if (!contentEquals) {
            //有模板文件，且增加了新坑，生成模板文件
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
                        Collectors.toMap(Meta.FileConfigDTO.FilesInfo::getOutputPath, o -> o, (e, r) -> r)
                ).values()));
        return resultList;
    }

    /**
     * 模型去重
     * @param modelsInfoList
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsInfo> distincModels(List<Meta.ModelConfigDTO.ModelsInfo> modelsInfoList) {
        //1.将所有模型配置（modelInfo）分为有分组和无分组的
        //先处理有分组的模型
        //{"groupKey":"a",models:[1,2]},{"groupKey":"a",models:[2,3]},{"groupKey":"b",models:[4,5]}
        //先转换为{"groupKey":"a",models:[[1,2],[2,3]]},{"groupKey":"b",models:[[4,5]]}
        Map<String, List<Meta.ModelConfigDTO.ModelsInfo>> groupKeyModelInfoListMap = modelsInfoList
                .stream()
                .filter(modelsInfo -> StrUtil.isNotBlank(modelsInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.ModelConfigDTO.ModelsInfo::getGroupKey)
                );

        //2.对于有分组的模型配置，如果有相同的分组，同分组内的模型进行合并(merge)，不同分组可同时保留
        //同组内配置合并
        //{"groupKey":"a",models:[[1,2],[2,3]]}
        //{"groupKey":"a",models:[1,2,2,3]}
        //{"groupKey":"a",models:[1,2,3]}
        Map<String,Meta.ModelConfigDTO.ModelsInfo> groupKeyMergedModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfigDTO.ModelsInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfigDTO.ModelsInfo> tempModelInfoList = entry.getValue();
            //[1,2,2,3]
            List<Meta.ModelConfigDTO.ModelsInfo> newModelInfoList =new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelsInfo -> modelsInfo.getModels().stream())
                    .collect(
                            Collectors.toMap(Meta.ModelConfigDTO.ModelsInfo::getFieldName, o -> o, (e, r) -> r)
                    ).values());

            //使用新的group配置
            Meta.ModelConfigDTO.ModelsInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey,newModelInfo);

        }

        //3.创建新的模型配置列表（结果列表），先将合并后的分组添加到结果列表中
        ArrayList<Meta.ModelConfigDTO.ModelsInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

        //4.再将无分组的模型配置列表添加到结果列表
        resultList.addAll(new ArrayList<>(modelsInfoList.stream()
                .filter(modelsInfo -> StrUtil.isBlank(modelsInfo.getGroupKey()))
                .collect(
                        Collectors.toMap(Meta.ModelConfigDTO.ModelsInfo::getFieldName, o -> o, (e, r) -> r)
                ).values()));
        return resultList;
        
    }

    
    public static void main(String[] args) {

        Meta meta =new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "pangzi-generator-demo-projects/springboot-init";
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/resources/application.yml";
        //模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        //-模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
        
        //-模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDescription("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setReplaceText("root");
        modelInfoConfig2.setDefaultValue("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);


        //替换变量
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

        long id = makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,1768815692226600960L);
        System.out.println(id);


    }
}
