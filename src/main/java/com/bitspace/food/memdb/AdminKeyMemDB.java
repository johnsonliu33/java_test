package com.bitspace.food.memdb;


import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.entity.AdminKey;
import com.bitspace.food.mapper.AdminKeyMapper;
import com.bitspace.food.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminKeyMemDB {
    private static Logger log = LoggerFactory.getLogger(AdminKeyMemDB.class);
    private static Map<Long, AdminKey> adminKeyMap = new HashMap<>();
    private static Map<String, AdminKey> adminPublicKeyMap = new HashMap<String, AdminKey>();
    private static Map<String, AdminKey> adminPrivateKeyMap = new HashMap<String, AdminKey>();

    static {
        init();
    }

    public static AdminKey getAdminKeyByUid(Long aid) {
        return adminKeyMap.get(aid);
    }
    
    public static AdminKey getAdminKeyByPublicKey(String publicKey) {
        AdminKey publicKeyData= adminPublicKeyMap.get(publicKey);
        if(null==publicKeyData)return null;
        Long aid=publicKeyData.getAid();
        if(null==aid)return null;
        if(!adminKeyMap.containsKey(aid))return null;
        return adminKeyMap.get(aid);
    }
    
    
    public static AdminKey getAdminKeyByPrivateKey(String privateKey) {
        AdminKey privateKeyData= adminPrivateKeyMap.get(privateKey);
        if(null==privateKeyData)return null;
        Long aid=privateKeyData.getAid();
        if(null==aid)return null;
        if(!adminKeyMap.containsKey(aid))return null;
        return adminKeyMap.get(aid);
    }
    
    public static void putAdminKey(AdminKey adminKey) {
        Long aid = adminKey.getAid();
        adminKeyMap.put(aid, adminKey);
        if(null!=adminKey.getPublicKey()){
            AdminKey publicKeyData=new AdminKey(adminKey.getPublicKey(),aid);
            adminPublicKeyMap.put(adminKey.getPublicKey(),publicKeyData);
        }
        if(null!=adminKey.getPrivateKey()){
            AdminKey privateKeyData=new AdminKey(aid,adminKey.getPrivateKey());
            adminPrivateKeyMap.put(adminKey.getPrivateKey(),privateKeyData);
        }
    }

    private static void init() {
        SessionContext context = null;
        try{
            context = new SessionContext();
            AdminKeyMapper mapper = context.getMapper(AdminKeyMapper.class);
            List<AdminKey> list = mapper.selectAll();
            List<AdminKey> publicKeyData = mapper.selectPublicKey();
            List<AdminKey> privateKeyData = mapper.selectPrivateKey();
            list.forEach(u->{
                adminKeyMap.put(u.getAid(), u);
            });
            publicKeyData.forEach(u->{
                adminPublicKeyMap.put(u.getPublicKey(), u);
            });
            privateKeyData.forEach(u->{
                adminPrivateKeyMap.put(u.getPrivateKey(), u);
            });
        }catch (Exception e){
            LoggerUtil.error(log, e);
        }finally {
            SessionContextUtil.closeSilently(context);
        }
    }
}
