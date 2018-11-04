package com.bitspace.food.disruptor.impl;


import com.bitspace.food.base.*;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.disruptor.inf.EventHandler;
import com.bitspace.food.util.LoggerUtil;
import com.bitspace.food.util.TupleOf3;
import com.offbynull.coroutines.user.Continuation;
import com.offbynull.coroutines.user.Coroutine;
import com.offbynull.coroutines.user.CoroutineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
public class AnnotationHandlerAdapter implements EventHandler<Event> {
    private static Logger log = LoggerFactory.getLogger(AnnotationHandlerAdapter.class);
    private ResponseHandler responseHandler;
    private Map<EventType, TupleOf3<AnnotatedHandler, Method, Integer>> typeMap = new HashMap<>(); //{eventType:[handler,method,argLength]}
    private String identifier;
    private AnnotatedHandler handler;

    private static class MyCoroutine implements Coroutine {

        private TupleOf3<AnnotatedHandler, Method, Integer> coroutineCurrentMethodInfo;
        private Event currentEvent;
        private ResponseHandler responseHandler;

        public MyCoroutine(TupleOf3<AnnotatedHandler, Method, Integer> coroutineCurrentMethodInfo, Event currentEvent, ResponseHandler responseHandler) {
            this.coroutineCurrentMethodInfo = coroutineCurrentMethodInfo;
            this.currentEvent = currentEvent;
            this.responseHandler = responseHandler;
        }

        @Override
        public void run(Continuation continuation) throws Exception {
            Event event = this.currentEvent;
            if (this.coroutineCurrentMethodInfo != null) {
                try {
                    TupleOf3<AnnotatedHandler, Method, Integer> methodInfo = this.coroutineCurrentMethodInfo;
                    Integer argLength = methodInfo.getThird();
                    if (argLength == 3)
                        methodInfo.getSecond().invoke(methodInfo.getFirst(), event.getData(), this.responseHandler, continuation);
                    else if (argLength == 4)
                        methodInfo.getSecond().invoke(methodInfo.getFirst(), event.getData(), this.responseHandler, continuation, event);
                    else {
                        LoggerUtil.error(log, "incorrect arg length[" + argLength + "] for method:" + methodInfo.getSecond());
                        responseHandler.sendResponse((Request) event.getData(), new Result(ErrMessage.CODE_SYS_UNKNOWN));
                    }
                } catch (Throwable e) {
                    if (e instanceof InvocationTargetException)
                        e = e.getCause();
                    LoggerUtil.error(log, "problem invoking " + coroutineCurrentMethodInfo.getSecond().getName() + "\n" + event + " ", e);
                    responseHandler.sendResponse((Request) event.getData(), new Result(ErrMessage.CODE_SYS_UNKNOWN));
                }
            } else {
                LoggerUtil.error(log, "this should not happen");
                responseHandler.sendResponse((Request) event.getData(), new Result(ErrMessage.CODE_SYS_UNKNOWN));
            }
        }
    }

    final private static Class<?>[] signature = {Object.class, ResponseHandler.class, Continuation.class, Event.class};

    private boolean check(Class<?>[] ptypes) {
        if (ptypes.length == 0 || ptypes.length > signature.length) return false;
        boolean result = true;
        for (int i = 1; i < ptypes.length; i++) {
            if (!Objects.equals(ptypes[i], signature[i])) {
                result = false;
                break;
            }
        }
        return result;
    }

    public AnnotationHandlerAdapter(AnnotatedHandler handler) {
        this.handler = handler;
        Class handlerClass = handler.getClass();
        for (Method m : handlerClass.getDeclaredMethods()) {
            EventMethod annotation = m.getAnnotation(EventMethod.class);
            if (annotation != null) {
                Class<?>[] ptypes = m.getParameterTypes();
                if (check(ptypes)) {
                    m.setAccessible(true);
                    TupleOf3<AnnotatedHandler, Method, Integer> existed = typeMap.put(annotation.value(), new TupleOf3<>(handler, m, ptypes.length));
                    if (existed != null)
                        LoggerUtil.error(log, annotation.value() + " was binded to " + existed.getSecond() + " and is now rebinded to " + m);
                } else {
                    LoggerUtil.error(log, "Annotated method:" + m.toString() + " is not qualified for binding, only method(Data[,ResponseHandler,Continuation,Event]) is allowed");
                }
            }
        }
        this.identifier = handler.getClass().getSimpleName() + "@" + handler.hashCode();
    }

    public AnnotatedHandler getHandler() {
        return handler;
    }

    @Override
    public void onEvent(Event event) {
        TupleOf3<AnnotatedHandler, Method, Integer> methodInfo = typeMap.get(event.getType());
        if (methodInfo != null) {
            Integer argLength = methodInfo.getThird();
            Boolean coroutineSupport = argLength > 2;
            if (Objects.equals(coroutineSupport, true)) {
                CoroutineRunner runner = new CoroutineRunner(new MyCoroutine(methodInfo, event, this.responseHandler));
                responseHandler.setCoroutineRunner(runner);
                runner.execute();
            } else {
                try {
                    if (argLength == 1)
                        methodInfo.getSecond().invoke(methodInfo.getFirst(), event.getData());
                    else if (argLength == 2)
                        methodInfo.getSecond().invoke(methodInfo.getFirst(), event.getData(), responseHandler);
                    else {
                        LoggerUtil.error(log, "incorrect arg length[" + argLength + "] for method:" + methodInfo.getSecond());
                        responseHandler.sendResponse((Request) event.getData(), new Result(ErrMessage.CODE_SYS_UNKNOWN));
                    }
                } catch (Throwable e) {
                    if (e instanceof InvocationTargetException)
                        e = e.getCause();
                    LoggerUtil.error(log, "problem invoking " + methodInfo.getSecond().getName() + "\n" + event + " ", e);
                    responseHandler.sendResponse((Request) event.getData(), new Result(ErrMessage.CODE_SYS_UNKNOWN));
                }
            }
        } else {
            LoggerUtil.error(log, "no handler method binded to eventType:" + event.getType());
            responseHandler.sendResponse((Request) event.getData(), new Result(ErrMessage.CODE_SYS_UNKNOWN));
        }
    }

    @Override
    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        if (handler != null) {
            for (Field f : handler.getClass().getDeclaredFields()) {
                if (Objects.equals(f.getType(), ResponseHandler.class)) {
                    f.setAccessible(true);
                    try {
                        f.set(this.handler, this.responseHandler);
                    } catch (Exception e) {
                        LoggerUtil.error(log, "problem setting responsehandler to AnnotatedHandler." + f.getName(), e);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public Set<EventType> getHandlingTypes() {
        return typeMap.keySet();
    }

}
