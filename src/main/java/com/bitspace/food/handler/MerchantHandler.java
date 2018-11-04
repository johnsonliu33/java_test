package com.bitspace.food.handler;

import com.bitspace.food.base.ErrMessage;
import com.bitspace.food.base.EventType;
import com.bitspace.food.base.Request;
import com.bitspace.food.base.Result;
import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.entity.Admin;
import com.bitspace.food.mapper.AdminMapper;
import com.bitspace.food.mapper.MerchantMapper;
import com.bitspace.food.memdb.AdminMemDB;
import com.bitspace.food.util.LoggerUtil;
import com.offbynull.coroutines.user.Continuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.handler
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/25 15:16
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class MerchantHandler implements AnnotatedHandler {
    private static Logger log = LoggerFactory.getLogger(MerchantHandler.class);
    
    /**
     *  商户管理列表
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_MERCHANT_MANAGE_LIST)
    public void goodsList(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        try {
            Map<String, Object> map = (Map<String, Object>) req.getData();
            context = new SessionContext();
            MerchantMapper mapper = context.getMapper(MerchantMapper.class);
            long total = mapper.getMerchantListCount(map);
            if (total != 0) {
                dataMap = mapper.getMerchantList(map);
            }
            responseHandler.sendResponse(req, Result.success(total), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    /**
     * 冻结/解冻
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_MERCHANT_FROZEN)
    public void updatePwd(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Result result = null;
        SessionContext context = null;
        try {
            context = new SessionContext();
            Map<String, Object> map = (Map<String, Object>) req.getData();
            AdminMapper mapper = context.getMapper(AdminMapper.class);
            int num = mapper.updateStatusByAid(map);
            if(num > 0){
                //更新缓存
                Long aid = Long.parseLong(map.get("aid").toString());
                Long updateTime = Long.parseLong(map.get("updateTime").toString());
                Integer status = Integer.parseInt(map.get("status").toString());
                Admin admin = AdminMemDB.getAdminByAid(aid);
                admin.setUpdateTime(updateTime);
                admin.setStatus(status);
                AdminMemDB.putUser(admin);
                context.commit();
            }
            result= Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    /**
     * 审核通过/审核不通过
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_MERCHANT_AUDIT)
    public void auditAdmin(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Result result = null;
        SessionContext context = null;
        try {
            context = new SessionContext();
            Map<String, Object> map = (Map<String, Object>) req.getData();

            AdminMapper mapper = context.getMapper(AdminMapper.class);
            int num = mapper.updateAuditStatusByAid(map);
            if(num > 0){
                //更新缓存
                Long aid = Long.parseLong(map.get("aid").toString());
                Long updateTime = Long.parseLong(map.get("updateTime").toString());
                Integer status = Integer.parseInt(map.get("status").toString());
                String auditOpinion = map.get("auditOpinion").toString();
                Admin admin = AdminMemDB.getAdminByAid(aid);
                admin.setUpdateTime(updateTime);
                admin.setStatus(status);
                admin.setAuditOpinion(auditOpinion);
                AdminMemDB.putUser(admin);
                context.commit();
            }

            result= Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
}
