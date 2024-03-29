package com.yupi.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

public class MetaManager {
    private static volatile Meta meta;

    private MetaManager(){
        //私有构造函数，防止外部实例化
    }
    public static Meta getMetaObject(){
        //运用双检索单例模式
        if (meta == null){
            synchronized (MetaManager.class){
                if (meta == null){
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta(){
        //todo 初始json文件修改
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
//        Meta newMeta = JSONObject.parseObject(metaJson, Meta.class);
        //校验配置文件，处理默认值
        MetaValidator.doValidAndFill(newMeta);
        return newMeta;
    }
}
