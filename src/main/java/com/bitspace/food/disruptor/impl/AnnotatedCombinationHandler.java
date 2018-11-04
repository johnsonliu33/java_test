package com.bitspace.food.disruptor.impl;


import com.bitspace.food.base.DescriptData;
import com.bitspace.food.base.Event;
import com.bitspace.food.base.EventType;
import com.bitspace.food.base.Response;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.disruptor.inf.EventHandler;
import com.bitspace.food.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.bitspace.food.disruptor.inf.Statistical.*;


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
public class AnnotatedCombinationHandler implements EventHandler<Event> {
    protected static Logger log = LoggerFactory.getLogger(AnnotatedCombinationHandler.class);
    private Map<EventType, AnnotationHandlerAdapter> typeMap = new HashMap<>();
    private AnnotationHandlerAdapter[] handlers;

    private static AnnotationHandlerAdapter[] convert(AnnotatedHandler[] handlers) {
        if (handlers.length == 0) throw new RuntimeException("handlers must not be empty");
        AnnotationHandlerAdapter[] result = new AnnotationHandlerAdapter[handlers.length];
        for (int i = 0; i < handlers.length; i++) {
            result[i] = new AnnotationHandlerAdapter(handlers[i]);
        }
        return result;
    }

    public AnnotatedCombinationHandler(AnnotatedHandler... handlers) {
        this(convert(handlers));
    }

    public AnnotatedCombinationHandler(AnnotationHandlerAdapter... handlers) {
        if (handlers.length == 0) throw new RuntimeException("handlers must not be empty");
        this.handlers = handlers;
        for (AnnotationHandlerAdapter handler : handlers) {
            if (handler.getHandlingTypes() != null)
                for (EventType eventType : handler.getHandlingTypes()) {
                    AnnotationHandlerAdapter existed = typeMap.put(eventType, handler);
                    if (existed != null) LoggerUtil.error(log, eventType + " was binded to " + existed + " and is now rebinded to " + handler);
                }
        }
    }

    public AnnotatedHandler getHandler(Event event) {
        if (null == event || null == event.getType()) return null;
        AnnotationHandlerAdapter adapter = typeMap.get(event.getType());
        if (null == adapter) return null;
        return adapter.getHandler();
    }

    @Override
    public void setResponseHandler(ResponseHandler responseHandler) {
        final AnnotatedCombinationHandler self = this;
        Map<Long, LinkedList<Event>> pendings = new HashMap<>();
        Set<Long> pendingControls = new HashSet<>();
        Map<String, Long> reqIdToUidMappings = new HashMap<>();
        Map<String, Long> localReqIdToUidMappings = new HashMap<>();
        responseHandler.setReceiveEventFilter((event) -> {
            if (event.getData() instanceof DescriptData) {
                Long uid = ((DescriptData) event.getData()).meta.uid;
                if (pendingControls.contains(uid)) {
                    LinkedList<Event> queue = pendings.computeIfAbsent(uid, k -> new LinkedList<>());
                    queue.add(event);
                    return true;
                }
            }
            return false;
        });
        responseHandler.setBeforeRequest((from, to, eventType, request) -> {
            if (request instanceof DescriptData) {
                Long uid = ((DescriptData) request).meta.uid;
                if (!Objects.equals(from, to)) {
                    if (!pendingControls.add(uid)) {
                        List<String> reqIdsToRemove = new ArrayList<>();
                        for (Map.Entry<String, Long> e : reqIdToUidMappings.entrySet()) {
                            if (e.getValue().equals(uid)) {
                                String reqId = e.getKey();
                                LoggerUtil.warn(log,"uid:" + uid + " is already set to pending with reqId:[" + reqId + "] and requesting for another reqId:["+request.getReqId()+"], and this should not happen");
                                reqIdsToRemove.add(reqId);
                            }
                        }
                        reqIdsToRemove.forEach(id->{reqIdToUidMappings.remove(id);});
                    }
                    reqIdToUidMappings.put(request.getReqId(), uid);
                } else {
                    localReqIdToUidMappings.put(request.getReqId(), uid);
                }

            }
        });
        responseHandler.setBeforeHandleResponse(response -> {
            Long uid = reqIdToUidMappings.remove(response.getReqId());
            if (uid != null && pendingControls.remove(uid))
                return uid;
            return null;
        });
        responseHandler.setAfterResponse((response, event, uid, stat) -> {
            List<Event> priorityStack = responseHandler.getPriorityStack();
            while(priorityStack.size()>0){
                long start = System.nanoTime();
                Event x = priorityStack.remove(0);
                if (x.getData() instanceof Response)
                    responseHandler.handleResponse((Response) x.getData(), x);
                else
                    self.onEvent(x);
                if(stat!=null) {
                    stat[PROC_TIME] += ((System.nanoTime() - start) / 1000L);
                    stat[PROC_CNT]++;
                    Long latency = System.currentTimeMillis() - x.getTimeStamp();
                    if (latency > 0L) {
                        stat[TTL_LAT] += latency;
                        stat[MAX_LAT] = Math.max(stat[MAX_LAT], latency);
                    }
                }
            }
            if (uid != null) {
                Queue<Event> queue = pendings.get(uid);
                if (queue != null) {
                    while (true) {
                        if (pendingControls.contains(uid)) {
                            break;
                        }
                        Event evt = queue.poll();
                        if (evt == null) {
                            break;
                        }
                        if (evt.getData() instanceof Response)
                            responseHandler.handleResponse((Response) evt.getData(), evt);
                        else
                            self.onEvent(evt);
                    }
                }
            }
        });
        responseHandler.setResponseFilter((event, response) -> {
            Long uid = localReqIdToUidMappings.remove(response.getReqId());
            if (uid != null) {
                if (pendingControls.contains(uid)) {
                    LinkedList<Event> queue = pendings.computeIfAbsent(uid, k -> new LinkedList<>());
                    queue.add(0, event);
                    return true;
                }
            }
            return false;
        });
        for (EventHandler<Event> h : this.handlers) {
            if (h != null) h.setResponseHandler(responseHandler);
        }
    }

    @Override
    public void onEvent(Event event) {
        AnnotationHandlerAdapter handler = typeMap.get(event.getType());
        if (handler != null) {
            handler.onEvent(event);
        } else {
            LoggerUtil.error(log, "no handler method registration found for " + event.getType());
        }
    }


}
