package com.bitspace.food.memdb;

import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.entity.UserMoney;
import com.bitspace.food.mapper.UserMoneyMapper;
import com.bitspace.food.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMoneyMemDB {
    private static Logger log = LoggerFactory.getLogger(UserMoneyMemDB.class);

    private static Map<Long, UserMoney> userMoneyMap = new HashMap<>();
    private static Map<String, UserMoney> moneyNameMap = new HashMap<>();

    static {
        init();
    }

    public static UserMoney getUserMoneyByUid(Long uid){
        return userMoneyMap.get(uid);
    }
    public static UserMoney getUserMoney(Long uid){
        return userMoneyMap.get(uid);
    }

    public static UserMoney getUserMoneyByName(String name){
        return moneyNameMap.get(name);
    }
    public static void putUser(UserMoney userMoney){
        Long uid = userMoney.getUid();
        userMoneyMap.put(uid, userMoney);
        if(userMoney.getIntegralName() != null) {
            moneyNameMap.put(userMoney.getIntegralName(), userMoney);
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
        UserMoneyMapper mapper = context.getMapper(UserMoneyMapper.class);
        List<UserMoney> list = mapper.selectAll();
        list.forEach(u->{
            userMoneyMap.put(u.getUid(), u);
            if(u.getIntegralName() != null) {
                moneyNameMap.put(u.getIntegralName(), u);
            }
        });
    }
}
