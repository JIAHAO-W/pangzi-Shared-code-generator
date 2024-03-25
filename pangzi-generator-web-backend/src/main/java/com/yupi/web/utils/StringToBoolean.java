package com.yupi.web.utils;

import com.yupi.maker.meta.Meta;

import java.util.List;

public class StringToBoolean {
    public static Meta  StrToBoolean(Meta meta){
        Meta.ModelConfigDTO modelConfig = meta.getModelConfig();
        List<Meta.ModelConfigDTO.ModelsInfo> models = modelConfig.getModels();
        for (Meta.ModelConfigDTO.ModelsInfo model : models) {
            if(model.getType().equals("boolean")){

                model.setDefaultValue(Boolean.valueOf(model.getDefaultValue().toString()));
            }
        }
        return meta;
    }
}
