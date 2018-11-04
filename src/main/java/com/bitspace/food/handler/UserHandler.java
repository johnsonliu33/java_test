package com.bitspace.food.handler;

import com.bitspace.food.base.*;
import com.bitspace.food.constants.Currency;
import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;

import com.bitspace.food.entity.User;
import com.bitspace.food.entity.UserMoney;
import com.bitspace.food.mapper.UserMapper;
import com.bitspace.food.mapper.UserMoneyMapper;
import com.bitspace.food.memdb.UserMemDB;
import com.bitspace.food.util.*;
import com.offbynull.coroutines.user.Continuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserHandler implements AnnotatedHandler {
    private static Logger log = LoggerFactory.getLogger(UserHandler.class);

    /**
     * 注册插入
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.USER_REGISTER)
    private void register(Request req, ResponseHandler responseHandler, Continuation continuation) {
        User user = (User) req.getData();
        Event event = responseHandler.sendRequest(ProcessorType.DB, EventType.USER_INSERT_DB, user, req, continuation);
        Response rsp = (Response) event.getData();
        if (!rsp.getResult().isSuccess()) {
            //提示信息修改为 注册失败
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
            return;
        }
        //存入内存中
        UserMemDB.putUser(user);
        //创建钱包
        UserMoney userMoney = new UserMoney();
        userMoney.setUid(user.getUid());
        userMoney.setType(Currency.TYPE_VAC);
        userMoney.setBalance(0d);
        userMoney.setUserName(user.getUserName());
        UserMoney.setUpdateTime(userMoney, System.currentTimeMillis());
        responseHandler.sendRequest(ProcessorType.DB, EventType.USER_OPEN, userMoney, req, continuation);

        responseHandler.sendResponse(req, Result.success());
    }
    

    /**
     * 修改登录密码
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.UPDATE_PWD)
    public void updatePwd(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Result result = null;
        SessionContext context = null;
        try {
            context = new SessionContext();
            User user = (User) req.getData();
            UserMapper mapper = context.getMapper(UserMapper.class);
            mapper.updatePwd(user);
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
}

