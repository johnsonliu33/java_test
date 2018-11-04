package com.bitspace.food.memdb;


import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.entity.Admin;
import com.bitspace.food.entity.Goods;
import com.bitspace.food.entity.GoodsKey;
import com.bitspace.food.mapper.GoodsKeyMapper;
import com.bitspace.food.mapper.GoodsMapper;
import com.bitspace.food.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsMemDB {
    private static Logger log = LoggerFactory.getLogger(GoodsMemDB.class);
    private static Map<Long, Goods> goodsMap = new HashMap<>();

    static {
        init();
    }

    public static Goods getGoodsById(Long id) {
        return goodsMap.get(id);
    }

    public static void putGoods(Goods goods){
        Long id = goods.getId();
        goodsMap.put(id, goods);
    }

    private static void init() {
        SessionContext context = null;
        try{
            context = new SessionContext();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            List<Goods> list = mapper.selectAll();
            list.forEach(u->{
                goodsMap.put(u.getId(), u);
            });
        }catch (Exception e){
            LoggerUtil.error(log, e);
        }finally {
            SessionContextUtil.closeSilently(context);
        }
    }
}
