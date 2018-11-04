package com.bitspace.food.disruptor.impl;


import com.bitspace.food.disruptor.inf.*;
import org.jdeferred.Deferred;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.impl.DeferredObject;
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
public class PromiseDeferredImpl<T> implements Promise<T> {
    private Deferred<T, T, Void> d;
    private org.jdeferred.Promise<T, T, Void> jdeferredPromise;

    public PromiseDeferredImpl() {
        this.d = new DeferredObject<>();
        this.jdeferredPromise = d.promise();
    }

    public PromiseDeferredImpl(org.jdeferred.Promise<T, T, Void> jdeferredPromise) {
        this.jdeferredPromise = jdeferredPromise;
    }

    private void setJdeferredPromise(org.jdeferred.Promise<T, T, Void> jdeferredPromise) {
        this.jdeferredPromise = jdeferredPromise;
    }

    @Override
    public <R> Promise<R> thenApply(final Function<T, R> func) {
        return new PromiseDeferredImpl<>(this.jdeferredPromise.then((DoneFilter<T, R>) func::apply));
    }

    @Override
    public <R> Promise<R> thenCompose(final EventDonePipe<T, R> pipe) {
        final PromiseDeferredImpl<R> _promise = new PromiseDeferredImpl<>();

        _promise.setJdeferredPromise(jdeferredPromise.then((DonePipe<T, R, R, Void>) result -> {
            return ((PromiseDeferredImpl<R>) pipe.pipeDone(result)).jdeferredPromise;
        }));
        return _promise;
    }

    @Override
    public Promise<T> thenAccept(final EventDoneCallBack<T> callback) {
        return new PromiseDeferredImpl<>(jdeferredPromise.done(callback::onDone));
    }

    @Override
    public Promise<T> fail(final EventFailCallBack<T> callback) {
        return new PromiseDeferredImpl<>(jdeferredPromise.fail(callback::onFail));
    }

    @Override
    public void resolve(T event) {
        if (d != null) d.resolve(event);
    }

    @Override
    public void reject(T event) {
        if (d != null) d.reject(event);
    }

}
