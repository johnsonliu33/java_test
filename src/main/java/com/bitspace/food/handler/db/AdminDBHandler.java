package com.bitspace.food.handler.db;

import com.bitspace.food.base.*;
import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.entity.Admin;
import com.bitspace.food.entity.User;
import com.bitspace.food.mapper.AdminMapper;
import com.bitspace.food.mapper.UserMapper;
import com.bitspace.food.memdb.AdminMemDB;
import com.bitspace.food.util.LoggerUtil;
import com.offbynull.coroutines.user.Continuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AdminDBHandler implements AnnotatedHandler {
    private static Logger log = LoggerFactory.getLogger(AdminDBHandler.class);

    /**
     * 注册插入
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_INSERT_DB)
    private void register(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            Admin admin = (Admin) req.getData();
            context = new SessionContext();
            AdminMapper mapper = context.getMapper(AdminMapper.class);
            mapper.insertAdmin(admin);
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
}

