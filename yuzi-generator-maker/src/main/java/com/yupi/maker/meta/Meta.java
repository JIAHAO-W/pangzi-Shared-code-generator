package com.yupi.maker.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class Meta {

    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfigDTO fileConfig;
    private ModelConfigDTO modelConfig;

    @NoArgsConstructor
    @Data
    public static class FileConfigDTO implements Serializable {
        private String inputRootPath;
        private String outputRootPath;
        private String type;
        private String sourceRootPath;
        private List<FilesInfo> files;

        @NoArgsConstructor
        @Data
        public static class FilesInfo implements Serializable{
            private String inputPath;
            private String outputPath;

            private String type;
            private String generateType;
            private String condition;
            private String groupKey;
            private String groupName;
            private List<FilesInfo> files;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfigDTO implements Serializable{
        private List<ModelsInfo> models;

        @NoArgsConstructor
        @Data
        public static class ModelsInfo implements Serializable{
            private String fieldName;
            private String type;
            private String description;
            private Object defaultValue;
            private String abbr;
            private String groupKey;
            private String groupName;
            private List<ModelsInfo> models;
            private String condition;

            //中间参数
            //该分组下所有参数拼接为字符串
            private String allArgsStr;
        }
    }
}
