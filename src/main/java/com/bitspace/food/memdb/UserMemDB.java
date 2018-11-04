package com.bitspace.food.memdb;

import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.entity.User;
import com.bitspace.food.mapper.UserMapper;
import com.bitspace.food.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMemDB {
    private static Logger log = LoggerFactory.getLogger(UserMemDB.class);

    private static Map<Long, User> userMap = new HashMap<>();
    private static Map<String, User> phoneMap = new HashMap<>();
    private static Map<String, Long> phoneUidMap = new HashMap<>(); // key phone , value uid;

    static {
        init();
    }

    public static User getUserByUid(Long uid){
        return userMap.get(uid);
    }
    public static User getUser(Long uid){
        return userMap.get(uid);
    }
    public static User getUserByPhone(String mobile){
        User tUser = new User();
        for(Map.Entry<Long, User> uMap :userMap.entrySet()){
            tUser = uMap.getValue();
            if(tUser.getMobile().equals(mobile)){
                return tUser;
            }
        }
        return null;
    }
    public static void putUser(User user){
        Long uid = user.getUid();
        userMap.put(uid, user);
        if(user.getUserName() != null) {
            phoneMap.put(user.getCountryUserName(), user);
        }
        if(user.getPhone() != null) {
            phoneMap.put(user.getCountryPhone(), user);
        }
    }

  private static void init(){
        SessionContext context = null;
        try{
            context = new SessionContext();
            selectData(context);
        }catch (Exception e){
            LoggerUtil.error(log, e);
        }finally {
            SessionContextUtil.closeSilently(context);
        }
    }

    private static void selectData(SessionContext context){
        UserMapper mapper = context.getMapper(UserMapper.class);
        List<User> list = mapper.selectAll();
        list.forEach(u->{
            userMap.put(u.getUid(), u);
            if(u.getUserName() != null) {
                phoneMap.put(u.getCountryUserName(), u);
            }
            if(u.getPhone() !=null){
                phoneMap.put(u.getCountryPhone(),u);
            }
        });
    }
//    public static User getUserByPhone(String phone){
//        Long uid = phoneUidMap.get(phone);
//        if(uid == null)return null;
//        return userMap.get(uid);
//    }
}
