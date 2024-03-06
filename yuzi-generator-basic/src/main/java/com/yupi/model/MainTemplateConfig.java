package com.yupi.model;


import lombok.Data;

/**
 * 动态配置模板
 */
@Data
public class MainTemplateConfig {
    //作者姓名
    private String Author;
    //输出信息
    private String OutputText;

    //是否循环
    private Boolean loop;
}
