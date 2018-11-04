package com.bitspace.food.handler.db;

import com.bitspace.food.base.ErrMessage;
import com.bitspace.food.base.EventType;
import com.bitspace.food.base.Request;
import com.bitspace.food.base.Result;
import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.entity.User;
import com.bitspace.food.entity.UserMoney;
import com.bitspace.food.mapper.UserMapper;
import com.bitspace.food.mapper.UserMoneyMapper;
import com.bitspace.food.util.LoggerUtil;
import com.offbynull.coroutines.user.Continuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserDBHandler implements AnnotatedHandler {

    private static Logger log = LoggerFactory.getLogger(UserDBHandler.class);


    @EventMethod(EventType.USER_INSERT_DB)
    public void insertUser(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            User user = (User) req.getData();
            context = new SessionContext();
            UserMapper mapper = context.getMapper(UserMapper.class);
            mapper.insertUser(user);
            context.commit();
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    /**
     *插入user_money表
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.USER_OPEN)
    private void insertUserMoney(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            UserMoney userMoney = (UserMoney) req.getData();
            context = new SessionContext();
            UserMoneyMapper mapper = context.getMapper(UserMoneyMapper.class);
            mapper.insertUserMoney(userMoney);
            responseHandler.sendResponse(req, Result.success());
            context.commit();

        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }



}
