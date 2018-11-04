package com.bitspace.food.mapper;

import com.bitspace.food.entity.CirculateHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.mapper
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/26 16:32
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface CirculateHistoryMapper {
    
    /**
     * 插入流通记录
     * @param circulateHistory
     * @return
     */
    @Insert("   INSERT INTO t_circulate_history (goodsid,from_aid,from_address,peer_aid,peer_address,amount,type,create_time,hash_id) " +
            "      VALUES(#{goodsId},#{fromAid},#{fromAddress},#{peerAid},#{peerAddress},#{amount},#{type},#{createTime},#{hashId}) ")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(CirculateHistory circulateHistory);
    
    /**
     * 根据商品id查询流通记录
     * @param goodsId
     * @return
     */
    @Select("   SELECT t.id id,t.goodsid goodsId,t.from_aid fromAid,fa.name fromName,t.from_address fromAddress,    " +
            "       t.peer_aid peerAid,pa.name peerName,t.peer_address peerAddress,t.amount,t.type,t.create_time createTime, t.hash_id hashId " +
            "   from t_circulate_history t" +
            "   INNER JOIN t_admin fa ON fa.aid = t.from_aid    " +
            "   INNER JOIN t_admin pa ON pa.aid = t.peer_aid    " +
            "   WHERE t.goodsid = #{goodsId}    " +
            "   ORDER BY t.create_time ASC    ")
    List<LinkedHashMap<String,Object>> getCircuLateHistoryListByGoodsId(@Param("goodsId") Long goodsId);

    /**
     * 根据商品id查询流通记录
     * @param hashId
     * @return
     */
    @Select("   SELECT t.id id,t.goodsid goodsId,t.from_aid fromAid,fa.name fromName,t.from_address fromAddress,    " +
            "       t.peer_aid peerAid,pa.name peerName,t.peer_address peerAddress,t.amount,t.type,t.create_time createTime, t.hash_id hashId " +
            "   from t_circulate_history t" +
            "   INNER JOIN t_admin fa ON fa.aid = t.from_aid    " +
            "   INNER JOIN t_admin pa ON pa.aid = t.peer_aid    " +
            "   WHERE t.hash_id = #{hashId}    " +
            "   ORDER BY t.create_time ASC    ")
    List<LinkedHashMap<String,Object>> getCircuLateHistoryListByHashId(@Param("hashId") String hashId);

    /**
     * 最新流通记录
     * @return
     */
    @Select("   SELECT t.id id,t.goodsid goodsId,t.from_aid fromAid,fa.name fromName,t.from_address fromAddress,    " +
            "       t.peer_aid peerAid,pa.name peerName,t.peer_address peerAddress,t.amount,t.type,t.create_time createTime, t.hash_id hashId " +
            "   from t_circulate_history t" +
            "   INNER JOIN t_admin fa ON fa.aid = t.from_aid    " +
            "   INNER JOIN t_admin pa ON pa.aid = t.peer_aid    " +
            "   WHERE 1 = 1    " +
            "   ORDER BY t.create_time DESC LIMIT 10    ")
    List<LinkedHashMap<String,Object>> getCircuLateHistoryListNew();
}
