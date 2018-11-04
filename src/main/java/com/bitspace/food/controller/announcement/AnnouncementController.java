package com.bitspace.food.controller.announcement;

import com.bitspace.food.base.EventType;
import com.bitspace.food.base.JsonResultWrapper;
import com.bitspace.food.base.ProcessorType;
import com.bitspace.food.base.Response;
import com.bitspace.food.constants.Pager;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.entity.Announcement;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AnnouncementController {

    /**
     * 首页公告列表
     * @param currentPage
     * @param count
     * @return
     */
    @RequestMapping(value = "/api/json/announcement/list", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<String> homeListAnnouncement(@RequestParam(value = "currentPage", required = false) Long currentPage,
                                                       @RequestParam(value = "count", required = false) Long count) {
        if (currentPage == null)
            currentPage = Pager.START_PAGE;
        if (count == null)
            count = Pager.DEFAULT_COUNT;
        Map<String, Object> params = new HashMap<>();
        params.put("startNumber", (currentPage - 1) * count);
        params.put("endNumber", count);

        DeferredResult<String> dr = new DeferredResult<>();

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.DB, EventType.ANNOUNCEMENT_LIST, params)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });

        return dr;
    }

    /**
     * 监管部门公告列表
     * @param request
     * @param title
     * @param startTime
     * @param endTime
     * @param status
     * @param currentPage
     * @param count
     * @return
     */
    @RequestMapping(value = "/api/json/announcement/list/detail", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<String> listAnnouncementDetail(HttpServletRequest request,
                                                         @RequestParam(value = "title", required = false) String title,
                                                         @RequestParam(value = "startTime", required = false) Long startTime,
                                                         @RequestParam(value = "endTime", required = false) Long endTime,
                                                         @RequestParam(value = "status", required = false) Long status,
                                                         @RequestParam(value = "currentPage", required = false) Long currentPage,
                                                         @RequestParam(value = "count", required = false) Long count) {
        if (currentPage == null)
            currentPage = Pager.START_PAGE;
        if (count == null)
            count = Pager.DEFAULT_COUNT;
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("status", status);
        params.put("startNumber", (currentPage - 1) * count);
        params.put("endNumber", count);

        DeferredResult<String> dr = new DeferredResult<>();

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.DB, EventType.ANNOUNCEMENT_DETAIL_LIST, params)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData(), rsp.getResult().getRecordTotal()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });

        return dr;
    }

    /**
     * 公告内容
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/json/announcement/body", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<String> getAnnouncementBodyById(@RequestParam(value = "id", required = true) String id) {
        DeferredResult<String> dr = new DeferredResult<>();

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.DB, EventType.ANNOUNCEMENT_GET_BYID, id)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });

        return dr;
    }

    /**
     * 查看公告内容
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/json/announcement/getAnnouncementBody", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<String> getAnnouncementBody(@RequestParam(value = "id", required = true) String id) {
        DeferredResult<String> dr = new DeferredResult<>();

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.DB, EventType.ANNOUNCEMENT_GET_BODY, id)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });

        return dr;
    }

    /**
     * 新增公告
     * @param title
     * @param body
     * @return
     */
    @RequestMapping(value = "/api/json/announcement/addAnnouncement", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<String> addAnnouncement(@RequestParam(value = "title", required = true) String title,
                                                  @RequestParam(value = "body", required = true) String body) {
        DeferredResult<String> dr = new DeferredResult<>();

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setBody(body);
        announcement.setCreateTime(System.currentTimeMillis());

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.DB, EventType.ANNOUNCEMENT_ADD, announcement)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });

        return dr;
    }

    /**
     * 关闭或发布公告
     * @param id
     * @param status
     * @return
     */
    @RequestMapping(value = "/api/json/announcement/closeOrPublishAnnouncement", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<String> closeOrPublishAnnouncementById(@RequestParam(value = "id", required = true) String id,
                                                                 @RequestParam(value = "status", required = true) String status) {
        DeferredResult<String> dr = new DeferredResult<>();

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.DB, EventType.ANNOUNCEMENT_CLOSE, params)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });

        return dr;
    }

    /**
     * 删除公告
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/json/announcement/deleteAnnouncement", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<String> deleteAnnouncementById(@RequestParam(value = "id", required = true) String id) {
        DeferredResult<String> dr = new DeferredResult<>();

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.DB, EventType.ANNOUNCEMENT_DELETE, id)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });

        return dr;
    }
}
