package com.bitspace.food.disruptor.impl;



import com.bitspace.food.base.Event;
import com.bitspace.food.base.EventType;
import com.bitspace.food.disruptor.inf.Actor;
import com.bitspace.food.disruptor.inf.Statistical;

import java.util.List;
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
public abstract class ActorImpl implements Actor<Event> {
    protected Long[] stat = Statistical.newStat();
    protected List<Actor<Event>> actors;

    public ActorImpl(List<Actor<Event>> actors) {
        if(actors==null||actors.size()==0) throw new IllegalArgumentException("actors can't be empty for ActorRoundRobinDispatch");
        for(Actor<Event> actor : actors){
            if(actor==null) throw new IllegalArgumentException("all actors must not be null");
        }
        this.actors = actors;
        stat[START_AT]=System.currentTimeMillis();
    }

    public List<Actor<Event>> getActors(){
        return this.actors;
    }

    protected abstract Actor<Event> getActor(EventType eventType, Object data);

    @Override
    public Long[] getStatistics() {
        stat[RECV_CNT]=0L;
        stat[PROC_CNT]=0L;
        stat[PROC_TIME]=0L;
        stat[TTL_LAT]=0L;
        stat[MAX_LAT]=0L;
        actors.forEach((actor)->{
            Long[] actorStat = actor.getStatistics();
            stat[RECV_CNT]+=actorStat[RECV_CNT];
            stat[PROC_CNT]+=actorStat[PROC_CNT];
            stat[PROC_TIME]+=actorStat[PROC_TIME];
            stat[TTL_LAT]+=actorStat[TTL_LAT];
            stat[MAX_LAT]=Math.max(stat[MAX_LAT],actorStat[MAX_LAT]);
        });
        return stat;
    }

    @Override
    public void start() {

    }

    @Override
    public void receive(EventType eventType, Object data) {
        getActor(eventType, data).receive(eventType, data);
    }

    @Override
    public void receive(EventType eventType, Object data, String id, Long delay, Long interval) {
        getActor(eventType, data).receive(eventType, data, id, delay, interval);
    }

    @Override
    public void stop(String id) {

    }

    @Override
    public void terminate() {
        if(this.actors!=null)
            this.actors.stream().forEach((actor)->actor.terminate());
    }

    @Override
    public void handle(EventType eventType, Object data) {
        getActor(eventType, data).handle(eventType, data);
    }
}
