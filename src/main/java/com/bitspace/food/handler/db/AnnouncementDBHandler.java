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
import com.bitspace.food.entity.Announcement;
import com.bitspace.food.mapper.AnnouncementMapper;
import com.bitspace.food.util.LoggerUtil;
import com.offbynull.coroutines.user.Continuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementDBHandler implements AnnotatedHandler {

    private static Logger log = LoggerFactory.getLogger(AnnouncementDBHandler.class);

    @EventMethod(EventType.ANNOUNCEMENT_LIST)
    public void selectAnnouncementList(Request req, ResponseHandler responseHandler, Continuation continuation) {
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        SessionContext context = null;
        Result result = null;
        try {
            Map<String, Object> map = (Map<String, Object>) req.getData();
            context = new SessionContext();
            AnnouncementMapper mapper = context.getMapper(AnnouncementMapper.class);

            dataMap = mapper.listAnnouncement(map);

            context.commit();
            responseHandler.sendResponse(req, Result.success(), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    @EventMethod(EventType.ANNOUNCEMENT_DETAIL_LIST)
    public void selectAnnouncementDetailList(Request req, ResponseHandler responseHandler, Continuation continuation) {
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        SessionContext context = null;
        Result result = null;
        try {
            Map<String, Object> map = (Map<String, Object>) req.getData();
            context = new SessionContext();
            AnnouncementMapper mapper = context.getMapper(AnnouncementMapper.class);
            Long total = mapper.listAnnouncementDetailCount(map);
            if(total > 0){
                dataMap = mapper.listAnnouncementDetail(map);
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

    @EventMethod(EventType.ANNOUNCEMENT_GET_BYID)
    public void selectAnnouncementBodyById(Request req, ResponseHandler responseHandler, Continuation continuation) {
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        SessionContext context = null;
        Result result = null;
        try {
            int id = Integer.parseInt((String) req.getData());
            context = new SessionContext();
            AnnouncementMapper mapper = context.getMapper(AnnouncementMapper.class);
            dataMap = mapper.getAnnouncementBodyById(id);
            //跟新公告浏览次数
            int num = mapper.updateAnnouncementTimesViewById(id);
            if(num > 0){
                context.commit();
            }
            responseHandler.sendResponse(req, Result.success(), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    @EventMethod(EventType.ANNOUNCEMENT_GET_BODY)
    public void getAnnouncementBody(Request req, ResponseHandler responseHandler, Continuation continuation) {
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        SessionContext context = null;
        Result result = null;
        try {
            int id = Integer.parseInt((String) req.getData());
            context = new SessionContext();
            AnnouncementMapper mapper = context.getMapper(AnnouncementMapper.class);
            dataMap = mapper.getAnnouncementBodyById(id);
            responseHandler.sendResponse(req, Result.success(), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    @EventMethod(EventType.ANNOUNCEMENT_ADD)
    public void insertAnnouncement(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            AnnouncementMapper mapper = context.getMapper(AnnouncementMapper.class);
            Announcement announcement = (Announcement) req.getData();
            mapper.addAnnouncement(announcement);
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

    @EventMethod(EventType.ANNOUNCEMENT_CLOSE)
    public void closeAnnouncement(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            AnnouncementMapper mapper = context.getMapper(AnnouncementMapper.class);
            mapper.updateAnnouncementWithStatusById((Map) req.getData());
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

    @EventMethod(EventType.ANNOUNCEMENT_DELETE)
    public void removeAnnouncement(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            AnnouncementMapper mapper = context.getMapper(AnnouncementMapper.class);
            mapper.deleteAnnouncementById((Integer.parseInt((String) req.getData())));
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
