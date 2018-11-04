package com.bitspace.food.mapper;

import com.bitspace.food.entity.AdminKey;
import com.bitspace.food.entity.GoodsKey;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.mapper
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/29 11:22
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface GoodsKeyMapper {
    
    @Select("select gid as `gid`"
            + ",private_key as `privateKey`"
            + ",public_key as `publicKey`"
            + ",pay_pwd as `payPwd`"
            + ",create_time as `createTime`"
            + " from t_goods_key")
    public List<GoodsKey> selectAll();
    
    
    @Select("select gid as `gid`"
            + ",public_key as `publicKey`"
            + " from t_goods_key")
    public List<GoodsKey> selectPublicKey();
    
    
    @Select("select gid as `gid`"
            + ",private_key as `privateKey`"
            + " from t_goods_key")
    public List<GoodsKey> selectPrivateKey();
    
    @Insert("insert into t_goods_key "
            + "(gid"
            + ",pay_pwd"
            + ",private_key"
            + ",public_key"
            + ",create_time)"
            + "values"
            + "(#{gid}"
            + ",#{payPwd}"
            + ",#{privateKey}"
            + ",#{publicKey}"
            + ",#{createTime})")
    int insertGoodsKey(GoodsKey goodsKey);
    
    @Select("select gid from t_goods_key where private_key=#{privateKey}")
    Long getGoodsPrivateKeyUid(String privateKey);
    
    
    @Select("select gid from t_goods_key where public_key=#{publicKey}")
    Long getGoodsPublicKeyUid(String publicKey);
    
    /**
     * 修改流通密码
     * @param goodsKey
     * @return
     */
    @Update("UPDATE t_goods_key t  SET t.pay_pwd = #{payPwd}  WHERE t.gid = #{gid} ")
    int updatePayPwd(GoodsKey goodsKey);
}
