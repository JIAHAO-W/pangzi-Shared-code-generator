package com.yupi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;
import org.elasticsearch.client.license.LicensesStatus;

import java.util.List;

/**
 * @author fffffood
 * @description 针对表【generator(代码生成器)】的数据库操作Mapper
 * @createDate 2024-03-19 14:05:39
 * @Entity com.yupi.web.model.entity.Generator
 */
public interface GeneratorMapper extends BaseMapper<Generator> {
    @Select("SELECT id,distPath FROM generator WHERE isDelete = 1")
    List<Generator> listDeletedGenerator();

}




