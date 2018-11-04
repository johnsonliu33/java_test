package com.bitspace.food.util;


import java.util.HashMap;
import java.util.Map;
/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food
 *@class_name   TransactionApplication
 *@auth         Administrator
 *@create_time  18-3-13 下午8:00
 *@company      香港币特空间交易平台有限公司
 *@comments
 *@method_name
 *@return
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class MapUtil {
    public static <K1, K2, V> boolean contains(Map<K1, Map<K2, V>> map, K1 k1, K2 k2) {
        if (map == null)
            return false;
        Map<K2, V> vMap = map.get(k1);
        if (vMap == null)
            return false;
        return vMap.containsKey(k2);
    }

    public static <K1, K2, K3, V> boolean contains(
            Map<K1, Map<K2, Map<K3, V>>> map, K1 k1, K2 k2, K3 k3) {
        if (map == null)
            return false;
        Map<K2, Map<K3, V>> map2 = map.get(k1);
        if (map2 == null)
            return false;
        Map<K3, V> map3 = map2.get(k2);
        return map3 != null && map3.containsKey(k3);
    }

    public static <K1, K2, V> Map<K1, Map<K2, V>> put(Map<K1, Map<K2, V>> map, K1 k1, K2 k2, V value) {
        Map<K2, V> vMap = null;
        if (map == null) {
            map = new HashMap<K1, Map<K2, V>>();
            vMap = new HashMap<K2, V>();
            map.put(k1, vMap);
        } else {
            vMap = map.get(k1);
            if (vMap == null) {
                vMap = new HashMap<K2, V>();
                map.put(k1, vMap);
            }
        }
        vMap.put(k2, value);
        return map;
    }

    // new put struct
    public static <K1, K2, K3, V> Map<K1, Map<K2, Map<K3, V>>> put(
            Map<K1, Map<K2, Map<K3, V>>> map, K1 k1, K2 k2, K3 k3, V value) {
        Map<K2, Map<K3, V>> vMap1 = null;
        Map<K3, V> vMap2 = null;
        if (map == null) {
            map = new HashMap<K1, Map<K2, Map<K3, V>>>();
            vMap1 = new HashMap<K2, Map<K3, V>>();
            vMap2 = new HashMap<K3, V>();
            vMap1.put(k2, vMap2);
            map.put(k1, vMap1);
        } else {
            vMap1 = map.get(k1);
            if (vMap1 == null) {
                vMap1 = new HashMap<K2, Map<K3, V>>();
                vMap2 = new HashMap<K3, V>();
                vMap1.put(k2, vMap2);
                map.put(k1, vMap1);
            } else {
                vMap2 = vMap1.get(k2);
                if (vMap2 == null) {
                    vMap2 = new HashMap<K3, V>();
                    vMap1.put(k2, vMap2);
                }
            }
        }
        vMap2.put(k3, value);
        return map;
    }
    public static <K1, K2, V> V get(Map<K1, Map<K2, V>> map, K1 k1, K2 k2) {
        if (map == null)
            return null;
        Map<K2, V> vMap = map.get(k1);
        if (vMap == null)
            return null;
        return vMap.get(k2);
    }

    public static <K1, K2, K3, V> V get(Map<K1, Map<K2, Map<K3, V>>> map,
                                        K1 k1, K2 k2, K3 k3) {
        if (map == null)
            return null;
        Map<K2, Map<K3, V>> vMap1 = map.get(k1);
        if (vMap1 == null)
            return null;
        Map<K3, V> vMap2 = vMap1.get(k2);
        if (vMap2 == null)
            return null;
        return vMap2.get(k3);

    }

    public static <K1, K2, V> V remove(Map<K1, Map<K2, V>> map, K1 k1, K2 k2) {
        if (map == null)
            return null;
        Map<K2, V> vMap = map.get(k1);
        if (vMap == null)
            return null;
        if (vMap.containsKey(k2)) {
            V v = vMap.remove(k2);
            if (vMap.size() == 0)
                map.remove(k1);
            return v;
        }else{
            return null;
        }
    }

    public static <K1, K2, K3, V> V remove(Map<K1, Map<K2, Map<K3, V>>> map, K1 k1, K2 k2, K3 k3) {
        if (map == null)
            return null;
        Map<K2, Map<K3, V>> vMap1 = map.get(k1);
        if (vMap1 == null)
            return null;
        Map<K3, V> vMap2 = vMap1.get(k2);
        if (vMap2 == null)
            return null;
        if (vMap2.containsKey(k3)) {
            V v = vMap2.remove(k3);
            if (vMap2.size() == 0)
                vMap1.remove(k2);
            if (vMap1.size() == 0)
                map.remove(k1);
            return v;
        }
        return null;
    }
}
