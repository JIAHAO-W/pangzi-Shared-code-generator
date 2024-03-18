package com.yupi.maker.template.model;

import com.yupi.maker.meta.Meta;
import lombok.Data;

/**
 * 用户输入模板制作工具
 */
@Data
public class TemplateMakerConfig {
    private Long id;
    private Meta meta = new Meta();
    private String originProjectPath;
    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();
    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
