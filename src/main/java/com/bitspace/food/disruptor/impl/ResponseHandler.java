package com.bitspace.food.disruptor.impl;



import com.bitspace.food.base.*;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.disruptor.inf.Actor;
import com.bitspace.food.disruptor.inf.Promise;
import com.bitspace.food.disruptor.inf.ReqOwner;
import com.offbynull.coroutines.user.Continuation;
import com.offbynull.coroutines.user.CoroutineRunner;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

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
public class ResponseHandler implements ReqOwner {
    public interface QuadConsumer<T1, T2, T3, T4> {
        void accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
    }

    public interface TriConsumer<T1, T2, T3> {
        void accept(T1 arg1, T2 arg2, T3 arg3);
    }

    private Map<String, Promise<Event>> promiseMap = new HashMap<>();
    private Map<String, CoroutineRunner> coroutineRunnerMap = new HashMap<>();
    private CoroutineRunner coroutineRunner;
    private QuadConsumer<Response, Event, Long, Long[]> afterResponse;
    private Function<Response, Long> beforeHandleResponse;
    private QuadConsumer<Byte, Byte, EventType, Request> beforeRequest;
    private Function<Event, Boolean> receiveEventFilter;
    private BiFunction<Event, Response, Boolean> responseFilter;
    private List<Event> priorityStack;
    private Byte processType;
    private Actor owner;
    private Long[] stat;

    public ResponseHandler(Actor owner) {
        this.owner = owner;
    }

    public void setProcessType(Byte processType) {
        this.processType = processType;
    }

    public List<Event> getPriorityStack() {
        return priorityStack;
    }

    public void setPriorityStack(List<Event> priorityStack) {
        this.priorityStack = priorityStack;
    }

    public void setAfterResponse(QuadConsumer<Response, Event, Long, Long[]> afterResponse) {
        this.afterResponse = afterResponse;
    }

    public void setBeforeHandleResponse(Function<Response, Long> beforeHandleResponse) {
        this.beforeHandleResponse = beforeHandleResponse;
    }

    public void setBeforeRequest(QuadConsumer<Byte, Byte, EventType, Request> beforeRequest) {
        this.beforeRequest = beforeRequest;
    }

    public void setResponseFilter(BiFunction<Event, Response, Boolean> responseFilter) {
        this.responseFilter = responseFilter;
    }

    public void setReceiveEventFilter(Function<Event, Boolean> receiveEventFilter) {
        this.receiveEventFilter = receiveEventFilter;
    }

    public void setStat(Long[] stat) {
        this.stat = stat;
    }

    public Boolean filterOutReceivedEvent(Event event) {
        if (this.receiveEventFilter != null) return receiveEventFilter.apply(event);
        else return false;
    }

    public boolean handleResponse(Response response, Event event) {
        boolean handled = false;
        Long uid = null;
        if (StringUtils.isNotEmpty(response.getReqId())) {
            if (responseFilter != null && responseFilter.apply(event, response))
                return handled;
            Promise<Event> promise = promiseMap.remove(response.getReqId());
            if (promise != null) {
                if (this.beforeHandleResponse != null)
                    uid = this.beforeHandleResponse.apply(response);
                if (response.getResult().isSuccess()) {
                    promise.resolve(event);
                } else {
                    promise.reject(event);
                }
                handled = true;
            } else {
                CoroutineRunner runner = coroutineRunnerMap.remove(response.getReqId());
                if (runner != null) {
                    if (this.beforeHandleResponse != null)
                        uid = this.beforeHandleResponse.apply(response);
                    this.setCoroutineRunner(runner);
                    runner.setContext(event);
                    runner.execute();
                    handled = true;
                } else {
                    //TODO for response cant find related coroutineRunner or promise
                }
            }
            if (handled && this.afterResponse != null) {
                this.afterResponse.accept(response, event, uid, stat);
            }
        }
        return handled;
    }

//    public Promise<Event> asyncSendRequest(Byte to, EventType eventType, Request request) {
//        return asyncSendRequest(this.processType, to, eventType, request);
//    }
//
//    public Promise<Event> asyncSendRequest(Byte from, Byte to, EventType eventType, Request request) {
//        Byte processType = (this.processType == null ? from : this.processType);
//        if (beforeRequest != null) beforeRequest.accept(processType, to, eventType, request);
//        if (Objects.equals(from, to)) {
//            request.setOriginatingProcessor(processType);
//            Promise<Event> promise = new PromiseDeferredImpl<>();
//            promiseMap.put(request.getReqId(), promise);
//            this.owner.handle(eventType, request);
//            return promise;
//        } else
//            return Dispatcher.getInstance().sendMsg(processType, to, eventType, request, this.promiseMap);
//    }

    public Event sendRequest(Byte to, EventType eventType, Object o, Request req,   Continuation continuation){
        if (req instanceof DescriptData) {
            Long uid = ((DescriptData) req).meta.uid;
            return sendRequest(this.processType, to, eventType, new DescriptData(uid, o), continuation);
        } else
            return sendRequest(this.processType, to, eventType, new Request(o), continuation);
    }

//    private Event sendRequest(Byte to, EventType eventType, Request request, Continuation continuation) {
//        return sendRequest(this.processType, to, eventType, request, continuation);
//    }

    private Event sendRequest(Byte from, Byte to, EventType eventType, Request request, Continuation continuation) {
        Byte processType = (this.processType == null ? from : this.processType);
        if (beforeRequest != null) beforeRequest.accept(processType, to, eventType, request);
        coroutineRunnerMap.put(request.getReqId(), coroutineRunner);
        request.setOriginatingProcessor(processType);
        if (Objects.equals(processType, to))
            this.owner.handle(eventType, request);
        else
            Dispatcher.getInstance().sendMsg(to, eventType, request);
        continuation.suspend();
        return (Event) continuation.getContext();
    }

    public void setCoroutineRunner(CoroutineRunner coroutineRunner) {
        this.coroutineRunner = coroutineRunner;
    }

    public CoroutineRunner getCoroutineRunner() {
        return this.coroutineRunner;
    }

    public Map<String, Promise<Event>> getPromiseMap() {
        return this.promiseMap;
    }

    @Override
    public boolean isMine(String requestId) {
        return promiseMap.containsKey(requestId) || coroutineRunnerMap.containsKey(requestId);
    }

    public void sendResponse(Request request, Result result) {
        sendResponse(request, result, null);
    }

    public void sendResponse(Request request, Result result, Object data) {
        if (request.getOriginatingProcessor() != null) {
            Response response = new Response();
            response.setReqId(request.getReqId());
            response.setResult(result);
            response.setData(data);
            if (Objects.equals(processType, request.getOriginatingProcessor()))
                this.owner.handle(EventType.RESPONSE, response);
            else
                Dispatcher.getInstance().sendMsg(request.getOriginatingProcessor(), EventType.RESPONSE, response);
        }
    }
}
