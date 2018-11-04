package com.bitspace.food.disruptor.impl;



import com.bitspace.food.base.Event;
import com.bitspace.food.base.EventType;
import com.bitspace.food.base.Response;
import com.bitspace.food.disruptor.inf.Actor;
import com.bitspace.food.disruptor.inf.ReqOwner;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
/**
 * actor轮流使用
 */
public class ActorRoundRobinDispatch extends ActorDispatchImpl{

    private AtomicLong seq = new AtomicLong(0);

    private int actorSize = 0;

    public ActorRoundRobinDispatch(List<Actor<Event>> actors) {
        super(actors);
        this.actorSize = actors.size();
    }

    @Override
    protected Actor<Event> getActor(EventType eventType, Object data) {
        //if this is a response, just try to find the actor who sent correlated request
        if (data instanceof Response) {
            String reqId = ((Response) data).getReqId();
            for (Actor<Event> actor : this.actors) {
                if (actor instanceof ReqOwner && ((ReqOwner) actor).isMine(reqId)) {
                    return actor;
                }
            }
        }
        Long no = seq.getAndIncrement();
        return this.actors.get(no.intValue() % actorSize);
    }

}
