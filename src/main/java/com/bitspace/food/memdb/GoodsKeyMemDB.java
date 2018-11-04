package com.bitspace.food.memdb;


import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.entity.AdminKey;
import com.bitspace.food.entity.GoodsKey;
import com.bitspace.food.mapper.AdminKeyMapper;
import com.bitspace.food.mapper.GoodsKeyMapper;
import com.bitspace.food.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsKeyMemDB {
    private static Logger log = LoggerFactory.getLogger(GoodsKeyMemDB.class);
    private static Map<Long, GoodsKey> goodsKeyMap = new HashMap<>();
    private static Map<String, GoodsKey> goodsPublicKeyMap = new HashMap<String, GoodsKey>();
    private static Map<String, GoodsKey> goodsPrivateKeyMap = new HashMap<String, GoodsKey>();

    static {
        init();
    }

    public static GoodsKey getGoodsKeyByGid(Long gid) {
        return goodsKeyMap.get(gid);
    }
    
    public static GoodsKey getGoodsKeyByPublicKey(String publicKey) {
        GoodsKey publicKeyData= goodsPublicKeyMap.get(publicKey);
        if(null==publicKeyData)return null;
        Long gid=publicKeyData.getGid();
        if(null==gid)return null;
        if(!goodsKeyMap.containsKey(gid))return null;
        return goodsKeyMap.get(gid);
    }
    
    
    public static GoodsKey getAGoodsKeyByPrivateKey(String privateKey) {
        GoodsKey privateKeyData= goodsPrivateKeyMap.get(privateKey);
        if(null==privateKeyData)return null;
        Long gid=privateKeyData.getGid();
        if(null==gid)return null;
        if(!goodsKeyMap.containsKey(gid))return null;
        return goodsKeyMap.get(gid);
    }
    
    public static void putGoodsKey(GoodsKey goodsKey) {
        Long gid = goodsKey.getGid();
        goodsKeyMap.put(gid, goodsKey);
        if(null!=goodsKey.getPublicKey()){
            GoodsKey publicKeyData=new GoodsKey(goodsKey.getPublicKey(),gid);
            goodsPublicKeyMap.put(goodsKey.getPublicKey(),publicKeyData);
        }
        if(null!=goodsKey.getPrivateKey()){
            GoodsKey privateKeyData=new GoodsKey(gid,goodsKey.getPrivateKey());
            goodsPrivateKeyMap.put(goodsKey.getPrivateKey(),privateKeyData);
        }
    }

    private static void init() {
        SessionContext context = null;
        try{
            context = new SessionContext();
            GoodsKeyMapper mapper = context.getMapper(GoodsKeyMapper.class);
            List<GoodsKey> list = mapper.selectAll();
            List<GoodsKey> publicKeyData = mapper.selectPublicKey();
            List<GoodsKey> privateKeyData = mapper.selectPrivateKey();
            list.forEach(u->{
                goodsKeyMap.put(u.getGid(), u);
            });
            publicKeyData.forEach(u->{
                goodsPublicKeyMap.put(u.getPublicKey(), u);
            });
            privateKeyData.forEach(u->{
                goodsPrivateKeyMap.put(u.getPrivateKey(), u);
            });
        }catch (Exception e){
            LoggerUtil.error(log, e);
        }finally {
            SessionContextUtil.closeSilently(context);
        }
    }
}
