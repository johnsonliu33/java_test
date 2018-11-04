package com.bitspace.food.disruptor;

import com.bitspace.food.base.*;
import com.bitspace.food.disruptor.impl.ActorDispatchImpl;
import com.bitspace.food.disruptor.impl.PromiseDeferredImpl;
import com.bitspace.food.disruptor.impl.SenderPromiseImpl;
import com.bitspace.food.disruptor.inf.Actor;
import com.bitspace.food.disruptor.inf.Promise;
import com.bitspace.food.disruptor.inf.Sender;
import com.bitspace.food.base.EventType;

import java.util.*;

public class Dispatcher {
    private static Dispatcher container = new Dispatcher();

    public static Dispatcher getInstance() {
        return container;
    }

    private Map<Byte, Sender<Event>> senderMap = new HashMap<>();

    public void registerSender(Byte processorType, Sender<Event> sender) {
        if (senderMap != null)
            this.senderMap.put(processorType, sender);
    }


    public void sendMsg(Byte to, EventType eventType, Object obj){
        if(obj instanceof Request || obj instanceof Response){
            this.senderMap.get(to).send(eventType, obj);
        }else{
            this.senderMap.get(to).send(eventType, new Request(obj));
        }    }


    public void sendMsgDescriptiveData(Byte to, EventType eventType, Object obj, Long uid) {
        this.senderMap.get(to).send(eventType, new DescriptData(uid, obj));
    }



    public Promise<Event> controllerSendMsg(Byte target, EventType eventType, Object object) {
        ControllerRequest controllerRequest = new ControllerRequest(object);
        controllerRequest.setOriginatingProcessor(target);

        Promise<Event> promise = new PromiseDeferredImpl<>();
        controllerRequest.setPromise(promise);
        this.senderMap.get(ProcessorType.CONTROLLER).sendWithResponse(eventType, controllerRequest);

        return promise;
    }

    public Promise<Event> controllerAppSendMsg(Byte target, EventType eventType, Object object) {
        ControllerRequest controllerRequest = new ControllerRequest(object);
        controllerRequest.setOriginatingProcessor(target);

        Promise<Event> promise = new PromiseDeferredImpl<>();
        controllerRequest.setPromise(promise);
        this.senderMap.get(ProcessorType.CONTROLLER_APP).sendWithResponse(eventType, controllerRequest);

        return promise;
    }

    public Promise<Event> controllerSendMsgDescriptiveData(Byte to, EventType eventType, Object object, Long uid) {
        ControllerRequest controllerRequest = new ControllerRequest(new DescriptData(uid, object));
        controllerRequest.setOriginatingProcessor(to);

        Promise<Event> promise = new PromiseDeferredImpl<>();
        controllerRequest.setPromise(promise);
        this.senderMap.get(ProcessorType.CONTROLLER).sendWithResponse(eventType, controllerRequest);

        return promise;
    }

    public Promise<Event> controllerAppSendMsgDescriptiveData(Byte to, EventType eventType, Object object, Long uid) {
        ControllerRequest controllerRequest = new ControllerRequest(new DescriptData(uid, object));
        controllerRequest.setOriginatingProcessor(to);

        Promise<Event> promise = new PromiseDeferredImpl<>();
        controllerRequest.setPromise(promise);
        this.senderMap.get(ProcessorType.CONTROLLER_APP).sendWithResponse(eventType, controllerRequest);

        return promise;
    }

    public Map<Byte, List<Long>> getSystemStats() {
        Iterator<Map.Entry<Byte, Sender<Event>>> iterator = senderMap.entrySet().iterator();
        Map<Byte, List<Long>> map = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<Byte, Sender<Event>> entry = iterator.next();
            List<Long> stat = Collections.emptyList();
            Long[] statData = entry.getValue().getStatistics();
            if (statData != null) {
                stat = Arrays.asList(statData);
            }
            map.put(entry.getKey(), stat);
        }
        return map;
    }

    public Map<Byte, List<Long>> dispatchActorStat(Byte processorType) {
        Map<Byte, List<Long>> map = new HashMap<>();
        Sender<Event> sender = senderMap.get(processorType);
        if (sender instanceof SenderPromiseImpl) {
            Actor<Event> dispatchActor = ((SenderPromiseImpl) sender).getReceiveActor();
            if (dispatchActor instanceof ActorDispatchImpl) {
                List<Actor<Event>> actors = ((ActorDispatchImpl) dispatchActor).getActors();
                int dispatchActorsStartIdx = 0;
                for (Actor<Event> actor : actors) {
                    List<Long> stat = Collections.emptyList();
                    Long[] statData = actor.getStatistics();
                    if (statData != null) {
                        stat = Arrays.asList(statData);
                    }
                    map.put((byte) dispatchActorsStartIdx++, stat);
                }
            }
//            else if (dispatchActor instanceof ActorDisruptorMTImpl) {
//                List<Long[]> stats = ((ActorDisruptorMTImpl) dispatchActor).getStats();
//                int dispatchActorsStartIdx = 0;
//                for (Long[] statData : stats) {
//                    map.put((byte) dispatchActorsStartIdx++, Arrays.asList(statData));
//                }
//            }
        }
        return map;
    }

}
