package com.hzzzzzy.utils;

import cn.hutool.core.bean.BeanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 将entityList转为对应的VOList
 */
public class Entity2VOUtils {
    public static <Entity,VO> List<VO> entity2VO(List<Entity> entityList, Class<VO> vo){
        List<VO> voList = new ArrayList<>();
        entityList.forEach(item->{
            VO properties = BeanUtil.copyProperties(item, vo);
            voList.add(properties);
        });
        return voList;
    }
}