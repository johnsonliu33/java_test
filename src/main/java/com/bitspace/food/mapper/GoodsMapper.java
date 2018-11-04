package com.bitspace.food.mapper;

import com.bitspace.food.entity.Goods;
import com.bitspace.food.entity.GoodsKey;
import com.bitspace.food.entity.GoodsReports;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.ibatis.annotations.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.mapper
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/25 15:04
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface GoodsMapper {
    
    /**
     * 统计查询商品列表
     * @param param
     * @return
     */
    Integer goodsListCount(Map<String,Object> param);
    
    /**
     * 查询商品列表
     * @param param
     * @return
     */
    List<LinkedHashMap<String,Object>> goodsList(Map<String,Object> param);
    
    /**
     * 查看商品详情
     * @param goodsId
     * @return
     */
    @Select("  SELECT t.id,t.aid aid,t.name name,t.number number,t.circulate_num circulateNum," +
            "   t.recall_num recallNum,t.status status, " +
            "   t.licence licence,t.brand brand,t.specification specification," +
            "t.burden_sheet burdenSheet," +
            "   t.store store,t.expiration_date expirationDate,t.works_name worksName," +
            "t.works_address worksAddress,t.works_phone worksPhone,gk.public_key address,t.sell_aid sellAid, t.create_time createTime, t.cause cause    " +
            " FROM t_goods t " +
            " inner join t_goods_key gk on gk.gid = t.id  WHERE  t.id = #{goodsId} ")
    Goods goodsInfoById(@Param("goodsId") Long goodsId);
    
    /**
     * 查看商品详情
     * @param goodsId
     * @return
     */
    @Select("  SELECT t.id,t.aid aid,t.name name,t.number number,t.circulate_num circulateNum," +
            "   t.recall_num recallNum,t.status status, " +
            "   t.licence licence,t.brand brand,t.specification specification," +
            "t.burden_sheet burdenSheet," +
            "   t.store store,t.expiration_date expirationDate,t.works_name worksName," +
            "t.works_address worksAddress,t.works_phone worksPhone,gk.public_key address   " +
            " FROM t_goods t " +
            " inner join t_goods_key gk on gk.gid = t.id  WHERE  t.id = #{goodsId} ")
    LinkedHashMap<String,Object> goodsInfoByIdMap(@Param("goodsId") Long goodsId);
    
    /**
     * 查看商品memo
     * @param goodsId
     * @return
     */
    @Select("SELECT t.id,a.name,t.content,t.create_time FROM t_memo t " +
            "  INNER JOIN t_admin a ON t.aid = a.aid WHERE t.goodsid = #{goodsId} ORDER BY t.create_time DESC")
    List<LinkedHashMap<String,Object>> getMemo(@Param("goodsId") Long goodsId);
    
    /**
     * 插入Memo
     * @param map
     * @return
     */
    @Insert("   INSERT INTO t_memo(aid,goodsid,content,create_time,hash_id)  " +
            "       VALUES(#{aid},#{goodsId},#{content},#{createTime},#{hashId})   ")
    int insetMemo(Map<String,Object> map);
    
    /**
     * 查看商品详情
     * @param goodsId
     * @return
     */
    @Select("  SELECT t.sell_aid sellAid FROM t_goods t WHERE  t.id = #{goodsId} ")
    Long getGoodsStatusAndSellAid(@Param("goodsId") Long goodsId);
    
    /**
     * 插入goods
     * @param goods
     * @return
     */
    @Insert("   INSERT INTO t_goods(aid,name,number,status,create_time,hash_id)  " +
            "       VALUES(#{aid},#{name},#{number},#{status},#{createTime},#{hashId})   ")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insetGoods(Goods goods);
    
    /**
     * 审核
     * @param map
     * @return
     */
    @Update("UPDATE t_goods t  SET t.status = #{status},t.cause = #{cause}  WHERE t.id = #{goodsId} ")
    int auditGoods(Map<String,Object> map);
    
    /**
     * 更改商品状态
     * @param map
     * @return
     */
    @Update("UPDATE t_goods t  SET t.status = #{status}  WHERE t.id = #{goodsId} ")
    int updateStatus(Map<String,Object> map);
    
    /**
     * 修改商品信息
     * @param goods
     * @return
     */
    int updateGoods(Goods goods);
    
    /**
     * 插入质检报告
     * @param goodsReports
     * @return
     */
    @Insert("  INSERT INTO food.t_goods_reports " +
            "   (gid,facade,moisture,impurity,unsound_grain,oleaginousness,aflatoxin,gong_content,hch_residual,ddt_residual,defective_particle," +
            "       annual_output_of,place_of_origin,reportsid,create_time,hash_id)" +
            "  VALUES(#{gid},#{facade},#{moisture},#{impurity},#{unsoundGrain},#{oleaginousness},#{aflatoxin},#{gongContent},#{hchResidual}," +
            "   #{ddtResidual},#{defectiveParticle},#{annualOutputOf},#{placeOfOrigin},#{reportsId},#{createTime},#{hashId}) ")
    int insertGoodsReports(GoodsReports goodsReports);
    
    /**
     * 查询质检报告
     * @param goodsId
     * @return
     */
    @Select("   SELECT t.id,t.gid,t.facade,t.moisture,t.impurity,t.unsound_grain unsoundGrain,t.oleaginousness" +
            "       ,t.aflatoxin,t.gong_content gongContent,t.hch_residual hchResidual,t.ddt_residual ddtResidual,t.defective_particle defectiveParticle, " +
            "       t.annual_output_of annualOutputOf,t.place_of_origin placeOfOrigin,t.reportsid reportsId,a.name reportsName,t.create_time createTime " +
            "       FROM t_goods_reports t  " +
            "       JOIN t_admin a ON t.reportsid = a.aid    " +
            "       WHERE t.gid = #{goodsId} ")
    LinkedHashMap<String,Object> getGoodsReports(@Param("goodsId") Long goodsId);
    
    /**
     * 供应商id统计商品总数
     * @param aid
     * @return
     */
    @Select("   SELECT COUNT(t.id) FROM t_goods t WHERE t.aid = #{aid} ")
    Integer countNumByAid(@Param("aid") Long aid);
    
    /**
     * 供应商id 和 status 统计商品总数
     * @param aid
     * @return
     */
    @Select("   SELECT COUNT(t.id) FROM t_goods t WHERE t.aid = #{aid} AND t.status = #{status}")
    Integer countNumByAidAndStatus(@Param("aid") Long aid,@Param("status")Integer status);
    
    /**
     * 经销商id统计商品总数
     * @param sellAid
     * @return
     */
    @Select("   SELECT COUNT(t.id) FROM t_goods t WHERE t.sell_aid = #{sellAid} and t.status = 4")
    Integer countNumBySellAid(@Param("sellAid") Long sellAid);
    
    /**
     * 经销商id 和 status 统计商品总数
     * @param sellAid
     * @return
     */
    @Select("   SELECT COUNT(t.id) FROM t_goods t WHERE t.sell_aid = #{sellAid} AND t.status = #{status}")
    Integer countNumBySellAidAndStatus(@Param("sellAid") Long sellAid,@Param("status")Integer status);
    
    /**
     * 统计商品总数
     * @return
     */
    @Select("   SELECT COUNT(t.id) FROM t_goods t  ")
    Integer countNum();
    
    /**
     * status 统计商品总数
     * @param status
     * @return
     */
    @Select("   SELECT COUNT(t.id) FROM t_goods t WHERE  t.status = #{status}")
    Integer countNumByStatus(@Param("status")Integer status);

    /**
     * 查看商品明细
     * @param map
     * @return
     */
    @Select("SELECT t.public_key AS publicKey, t1.licence AS licence, t1.brand AS brand, t1.specification AS specification, " +
            "t1.burden_sheet AS burdenSheet, t1.store AS store, t1.expiration_date AS expirationDate, " +
            "t1.works_name AS worksName, t1.works_address AS worksAddress, t1.works_phone AS worksPhone, " +
            "t.create_time AS createTime, t1.status AS 'status', t2.facade AS facade, t2.moisture AS moisture, t2.impurity AS impurity, " +
            "t2.unsound_grain AS unsoundGrain, t2.oleaginousness AS oleaginousness, t2.aflatoxin AS aflatoxin, t2.gong_content AS gongContent, " +
            "t2.hch_residual AS hchResidual, t2.ddt_residual AS ddtResidual, t2.defective_particle AS defectiveParticle, t2.annual_output_of AS annualOutput, " +
            "t2.place_of_origin AS placeOrigin,t1.cause AS cause " +
            "FROM t_goods_key t " +
            "LEFT JOIN t_goods t1 ON t.gid = t1.id " +
            "LEFT JOIN t_goods_reports t2 ON t1.id = t2.gid " +
            "WHERE 1 = 1 AND t.public_key = #{publicKey} ")
    List<LinkedHashMap<String, Object>> getGoodsDetailByCode(Map<String, Object> map);


    /**
     * 查看所有商品
     * @return
     */
    @Select("SELECT " +
            "id, " +
            "aid, " +
            "NAME 'name' ," +
            "number ," +
            "circulate_num circulateNum, " +
            "recall_num recallNum, " +
            "private_key privateKey, " +
            "money_address moneyAddress, " +
            "STATUS 'status', " +
            "cause, " +
            "sell_aid sellAid, " +
            "create_time createTime, " +
            "report_num reportNum, " +
            "dispose_num disposeNum, " +
            "licence, " +
            "brand, " +
            "specification, " +
            "burden_sheet burdenSheet, " +
            "store, " +
            "expiration_date expirationDate, " +
            "works_name worksName, " +
            "works_address worksAddress, " +
            "works_phone worksPhone, " +
            "hash_id hashId " +
            "FROM t_goods ")
    public List<Goods> selectAll();

    /**
     * 根据商品公钥查看商品hash值
     * @param map
     * @return
     */
    @Select("SELECT t2.hash_id hashStr FROM t_goods_key t1 LEFT JOIN t_goods t2 ON t1.gid = t2.id WHERE t1.public_key = #{publicKey}")
    public String getGoodsHashByPublicKey(Map<String, Object> map);

    /**
     * 根据商品公钥查看商品记录hash值
     * @param map
     * @return
     */
    @Select("SELECT t2.hash_id hashStr FROM t_goods_key t1 LEFT JOIN t_goods_reports t2 ON t1.gid = t2.gid " +
            "WHERE t1.public_key = #{publicKey} ORDER BY t2.create_time DESC LIMIT 1 ")
    public String getReportsHashByPublicKey(Map<String, Object> map);

    /**
     * 查看memo明细
     * @param map
     * @return
     */
    @Select("SELECT t2.aid aid, t2.goodsid gid, t2.hash_id hashId FROM t_goods_key t1 LEFT JOIN t_memo t2 ON t1.gid = t2.goodsid " +
            "WHERE t1.public_key = #{publicKey} ")
    List<LinkedHashMap<String, Object>> getMemoHashListByPublicKey(Map<String, Object> map);
}
