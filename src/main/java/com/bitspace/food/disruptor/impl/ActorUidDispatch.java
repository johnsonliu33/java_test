package com.bitspace.food.disruptor.impl;


import com.bitspace.food.base.*;
import com.bitspace.food.disruptor.inf.Actor;
import com.bitspace.food.disruptor.inf.ReqOwner;

import java.util.List;
import java.util.Objects;
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
 * uid 分配到固定的actor;
 */

public class ActorUidDispatch extends ActorDispatchImpl{
    public ActorUidDispatch(List<Actor<Event>> actors) {
        super(actors);
        actorSize = actors.size();
        seq = new AtomicLong(0);
    }

    private AtomicLong seq;
    private int actorSize = 0;

    private Byte processorType = 127;

    public ActorUidDispatch(List<Actor<Event>> actors, Byte processorType) {
        this(actors);
        this.processorType = processorType;
    }

    @Override
    protected Actor<Event> getActor(EventType eventType, Object data) {
        if (data instanceof Response) {
            String reqId = ((Response) data).getReqId();
            for (Actor<Event> actor : this.actors) {
                if (actor instanceof ReqOwner) {
                    if (((ReqOwner) actor).isMine(reqId)) {
                        return actor;
                    }
                }
            }
        }
        Long uid = null;
        if (data instanceof DescriptData) {
            uid = ((DescriptData) data).meta.uid;
        }
        if (uid != null) {
            if (Objects.equals(this.processorType, ProcessorType.DB)) {
                Long idx = uid % (actorSize - 1);
                return this.actors.get(idx.intValue());
            }
            Long idx = (uid % actorSize);
            return this.actors.get(idx.intValue());
        } else {
            Long no = seq.getAndIncrement();
            Long idx = no % actorSize;
            return this.actors.get(idx.intValue());
        }
    }
}
