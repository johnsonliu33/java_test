package com.bitspace.food.handler;

import com.bitspace.food.base.*;
import com.bitspace.food.base.EventType;
import com.bitspace.food.base.ProcessorType;
import com.bitspace.food.base.Result;
import com.bitspace.food.base.Request;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.impl.PromiseDeferredImpl;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.disruptor.inf.Promise;
import com.bitspace.food.util.HttpParam;
import com.bitspace.food.util.LoggerUtil;
import com.offbynull.coroutines.user.Continuation;
import org.apache.commons.lang3.tuple.Pair;
import org.asynchttpclient.*;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.asynchttpclient.Dsl.asyncHttpClient;


public class HttpHandler implements AnnotatedHandler {
    private static Logger log = LoggerFactory.getLogger(HttpHandler.class);
    private static Map<String, Promise<Object>> httpPromiseRequest = new HashMap<>();
    private static AsyncHttpClientConfig cf = new DefaultAsyncHttpClientConfig.Builder()
            .setConnectTimeout(20000)
            .setRequestTimeout(20000)
            .setReadTimeout(30000)
            .build();
    private static AsyncHttpClient client = asyncHttpClient(cf);
    
    private static Promise<Object> asyncHttpGetPromise(Request req, HttpParam httpParam){
        Promise<Object> p = new PromiseDeferredImpl<>();
        httpPromiseRequest.put(req.getReqId(), p);
        BoundRequestBuilder b = client.prepareGet(httpParam.url);
        if(null != httpParam.headerMap) b.setHeaders(httpParam.headerMap);
        if(null != httpParam.bodyParams) b.setBody(httpParam.bodyParams);
        if(null != httpParam.queryParam) b.setQueryParams(httpParam.queryParam);
        if(null != httpParam.formParam)  b.setFormParams(httpParam.formParam);
        b.execute()
                .toCompletableFuture()
                .exceptionally(throwable -> {
                    Dispatcher.getInstance().sendMsg(ProcessorType.HTTP, EventType.HTTP_RESOLVE_COMMON, Pair.of(req.getReqId(), throwable));
                    return null;
                })
                .thenAccept(rsp->{
                    if(null == rsp) return;
                    Dispatcher.getInstance().sendMsg(ProcessorType.HTTP, EventType.HTTP_RESOLVE_COMMON, Pair.of(req.getReqId(), rsp));
                });
        return p;
    }
    
    private static Promise<Object> asyncHttpPostPromise(Request req, HttpParam httpParam) {
        Promise<Object> p = new PromiseDeferredImpl<>();
        httpPromiseRequest.put(req.getReqId(), p);
        BoundRequestBuilder b = client.preparePost(httpParam.url).setCharset(Charset.forName("UTF-8"));
        if(null != httpParam.headerMap) b.setHeaders(httpParam.headerMap);
        if(null != httpParam.bodyParams) b.setBody(httpParam.bodyParams);
        if(null != httpParam.queryParam) b.setQueryParams(httpParam.queryParam);
        if(null != httpParam.formParam)  b.setFormParams(httpParam.formParam);
        
        b.execute()
                .toCompletableFuture()
                .exceptionally(throwable -> {
                    Dispatcher.getInstance().sendMsg(ProcessorType.HTTP, EventType.HTTP_RESOLVE_COMMON, Pair.of(req.getReqId(), throwable));
                    return null;
                })
                .thenAccept(rsp -> {
                    if(null == rsp) return;
                    Dispatcher.getInstance().sendMsg(ProcessorType.HTTP, EventType.HTTP_RESOLVE_COMMON, Pair.of(req.getReqId(), rsp));
                });
        return p;
    }
    
    private void handleHttpRsp(HttpParam requestParams, Object o, Request req, ResponseHandler responseHandler){
        if(o instanceof Response)
            responseHandler.sendResponse(req, Result.success(), o);
        else if (o instanceof Throwable)
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_HTTP_WITHDRAW_FAIL));
            //responseHandler.sendResponse(req, new Result(ErrMessage.CODE_HTTP_WITHDRAW_FAIL));
        else
            LoggerUtil.error(log, "Http request Apply for a withdrawal error"+o.toString());
    }
    
    @EventMethod(EventType.HTTP_COMMON)
    private void doHttp(Request req, ResponseHandler responseHandler, Continuation continuation){
        HttpParam requestParams = (HttpParam)req.getData();// method, url, params
        if(requestParams.method.equalsIgnoreCase("POST")){
            asyncHttpPostPromise(req, requestParams).thenAccept(o->{
                handleHttpRsp(requestParams, o, req, responseHandler);
            });
        }else if(requestParams.method.equalsIgnoreCase("GET")){
            asyncHttpGetPromise(req, requestParams).thenAccept(o->{
                handleHttpRsp(requestParams, o, req, responseHandler);
            });
        }else{
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_HTTP_UNKNOWN_METHOD));
        }
    }
    
    @EventMethod(EventType.HTTP_RESOLVE_COMMON)
    private void resolveHttpPromise(Request req, ResponseHandler responseHandler, Continuation continuation){
        Pair<String, Object> pair = (Pair<String, Object>)req.getData();
        Promise<Object> promise = httpPromiseRequest.remove(pair.getLeft());
        if(promise != null){
            promise.resolve(pair.getRight());
        }
        responseHandler.sendResponse(req, Result.success());
    }
}
