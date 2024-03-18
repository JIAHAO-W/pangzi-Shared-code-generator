package com.yupi.maker.template;

import cn.hutool.core.util.StrUtil;
import com.yupi.maker.meta.Meta;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板制作工具类
 */
public class TemplateMakerUtils {
    public static List<Meta.FileConfigDTO.FilesInfo> removeGroupFilesFromRoot(List<Meta.FileConfigDTO.FilesInfo> fileInfoList){
        //先获取到所有分组
        List<Meta.FileConfigDTO.FilesInfo> groupFileInfoList = fileInfoList.stream()
                .filter(filesInfo -> StrUtil.isNotBlank(filesInfo.getGroupKey()))
                .toList();

        //获取分组内的文件列表
        List<Meta.FileConfigDTO.FilesInfo> groupInnerFileInfoList = groupFileInfoList.stream()
                .flatMap(filesInfo -> filesInfo.getFiles().stream())
                .toList();

        //获取所有分组内文件输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream()
                .map(Meta.FileConfigDTO.FilesInfo::getInputPath)
                .collect(Collectors.toSet());

        //移除所有名称在set中的外层文件
        return  fileInfoList.stream()
                .filter(filesInfo -> !fileInputPathSet.contains(filesInfo.getInputPath()))
                .collect(Collectors.toList());
    }
}
