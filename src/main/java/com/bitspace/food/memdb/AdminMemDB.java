package com.bitspace.food.memdb;



import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.entity.Admin;
import com.bitspace.food.mapper.AdminMapper;
import com.bitspace.food.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dev on 2017/8/31.
 */
public class AdminMemDB {
    private static Logger log = LoggerFactory.getLogger(AdminMemDB.class);

    private static Map<Long, Admin> adminMap = new HashMap<>();
    private static Map<String, Admin> adminLoginNameMap = new HashMap<>();

    static {
        init();
    }
    
    /**
     *根据用户名获取数据
     * @param loginName
     * @return
     */
    public static Admin getAdminByLoginName(String loginName){
        Admin nameData=adminLoginNameMap.get(loginName);
        if(null==nameData)return null;
        Long aid=nameData.getAid();
        if(null==aid)return null;
        if(!adminMap.containsKey(aid))return null;
        return adminMap.get(aid);
    }

    public static Admin getAdminByAid(Long aid){
        if(!adminMap.containsKey(aid))return null;
        return adminMap.get(aid);
    }
    
    
    public static void putUser(Admin admin){
        Long aid = admin.getAid();
        adminMap.put(aid, admin);
        if(null!=admin.getLoginName()){
            Admin nameData=new Admin(aid,admin.getLoginName());
            adminLoginNameMap.put(admin.getLoginName(),nameData);
        }
    }

    private static void init(){
        SessionContext context = null;
        try{
            context = new SessionContext();
            AdminMapper mapper = context.getMapper(AdminMapper.class);
            List<Admin> list = mapper.selectAll();
            List<Admin> loginNameData=mapper.selectAllLoginName();
            list.forEach(u->{
                adminMap.put(u.getAid(), u);
                           });
            if(null !=loginNameData){
                loginNameData.forEach(n->{
                    adminLoginNameMap.put(n.getLoginName(), n);
                });
            }
        }catch (Exception e){
            LoggerUtil.error(log, e);
        }finally {
            SessionContextUtil.closeSilently(context);
        }
    }
}
