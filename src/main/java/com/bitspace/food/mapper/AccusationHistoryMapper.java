package com.bitspace.food.mapper;

import com.bitspace.food.entity.AccusationHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.mapper
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/28 17:27
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AccusationHistoryMapper {
    
    /**
     * 统计列表总数
     * @return
     */
    Integer getListCount(Map<String,Object> map);
    
    /**
     * \获取列表
     * @return
     */
    List<LinkedHashMap<String,Object>> getList(Map<String,Object> map);
    
    /**
     * 批量处理
     * @param map
     * @return
     */
    int banchDispose(Map<String,Object> map);
    
    /**
     * 根据status统计数量
     * @param status
     * @return
     */
    @Select("SELECT COUNT(t.id) FROM t_accusation_history t WHERE t.status = #{status}")
    Integer countByStatus(@Param("status")Integer status);

    /**
     * 新增举报
     * @param accusationHistory
     * @return
     */
    @Insert("INSERT INTO t_accusation_history(goodsid, name, identity_card, phone, content, urlA, urlB, urlC, urlD, urlE, urlF, urlG, urlH, type, status, create_time) " +
            "VALUES(#{goodsid}, #{name}, #{identityCcard}, #{phone}, #{content}, #{urlA}, #{urlB}, #{urlC}, #{urlD}, #{urlE}, #{urlF}, #{urlG}, #{urlH}, #{type}, #{status}, #{createTime}) ")
    int insertAccusationHistory(AccusationHistory accusationHistory);
}
