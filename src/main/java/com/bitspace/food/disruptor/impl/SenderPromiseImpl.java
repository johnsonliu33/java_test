package com.bitspace.food.disruptor.impl;


import com.bitspace.food.base.Event;
import com.bitspace.food.base.EventType;
import com.bitspace.food.disruptor.inf.Actor;
import com.bitspace.food.disruptor.inf.Promise;
import com.bitspace.food.disruptor.inf.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dev on 2017/8/27.
 */
public class SenderPromiseImpl implements Sender<Event> {
    private static Logger log = LoggerFactory.getLogger(SenderPromiseImpl.class);
    private Actor<Event> receiveActor;

    public SenderPromiseImpl(Actor<Event> receiveActor) {
        this.receiveActor = receiveActor;
    }

    @Override
    public Promise<Event> sendWithResponse(EventType eventType, Object request) {//receiveActor = new ActorDisruptorImpl(false, OrderHandler.getInstance())
        Promise<Event> promise = new PromiseDeferredImpl<>();
        receiveActor.receive(eventType, request);
        return promise;
    }

    @Override
    public void send(EventType eventType, Object data) {
        receiveActor.receive(eventType, data);
    }

    @Override
    public Long[] getStatistics() {
        return receiveActor.getStatistics();
    }

    public Actor<Event> getReceiveActor(){
        return this.receiveActor;
    }
}
