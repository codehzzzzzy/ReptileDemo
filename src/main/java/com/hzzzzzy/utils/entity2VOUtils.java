package com.hzzzzzy.utils;

import cn.hutool.core.bean.BeanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 将entityList转为对应的VOList
 */
public class entity2VOUtils {
    public static <Eneity,VO> List<VO> eneity2VO(List<Eneity> eneityList, Class<VO> vo){
        List<VO> voList = new ArrayList<>();
        eneityList.forEach(item->{
            VO properties = BeanUtil.copyProperties(item, vo);
            voList.add(properties);
        });
        return voList;
    }
}
