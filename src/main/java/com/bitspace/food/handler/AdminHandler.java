package com.bitspace.food.handler;

import com.bitspace.food.base.*;
import com.bitspace.food.constants.Currency;
import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.entity.Admin;
import com.bitspace.food.entity.AdminKey;
import com.bitspace.food.entity.User;
import com.bitspace.food.entity.UserMoney;
import com.bitspace.food.mapper.AdminKeyMapper;
import com.bitspace.food.mapper.AdminMapper;
import com.bitspace.food.mapper.UserMapper;
import com.bitspace.food.memdb.AdminKeyMemDB;
import com.bitspace.food.memdb.AdminMemDB;
import com.bitspace.food.memdb.UserMemDB;
import com.bitspace.food.util.KeyUtil;
import com.bitspace.food.util.LoggerUtil;
import com.offbynull.coroutines.user.Continuation;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class AdminHandler implements AnnotatedHandler {
    private static Logger log = LoggerFactory.getLogger(AdminHandler.class);

    /**
     * 注册插入
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_REGISTER)
    private void register(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Admin admin = (Admin) req.getData();
        Event event = responseHandler.sendRequest(ProcessorType.ADMIN, EventType.ADMIN_INSERT_DB, admin, req, continuation);
        Response rsp = (Response) event.getData();
        if (!rsp.getResult().isSuccess()) {
            //提示信息修改为 注册失败
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
            return;
        }
        //存入内存中
        AdminMemDB.putUser(admin);
        responseHandler.sendResponse(req, Result.success());
    }

    /**
     * 冻结
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_FROZEN_ADMIN)
    public void updatePwd(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Result result = null;
        SessionContext context = null;
        try {
            context = new SessionContext();
            Map<String, Object> map = (Map<String, Object>) req.getData();
            AdminMapper mapper = context.getMapper(AdminMapper.class);
            mapper.updateStatusByAid(map);
            context.commit();
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
     * 修改流通密码
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_UPDATE_PAYPWD)
    public void updatePayPwd(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Result result = null;
        SessionContext context = null;
        try {
            context = new SessionContext();
            Pair<Long, String> pair = (Pair<Long, String>) req.getData();
            Long aid = pair.getLeft();
            String payPwd = pair.getRight();
            String privateKey = KeyUtil.generatePrivateKey(payPwd+aid);
            Event event = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_WALLET, privateKey, req, continuation);
            Response rsp = (Response)event.getData();
            if(!rsp.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String publicKey = String.valueOf(rsp.getData());
            AdminKey adminKey = new AdminKey(aid,payPwd,privateKey,publicKey);
            AdminKeyMapper mapper = context.getMapper(AdminKeyMapper.class);
            mapper.insertAdminKey(adminKey);
            context.commit();
            AdminKeyMemDB.putAdminKey(adminKey);
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

