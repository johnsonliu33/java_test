package com.bitspace.food.disruptor.impl;


import com.bitspace.food.base.*;
import com.bitspace.food.disruptor.inf.Actor;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.disruptor.inf.ReqOwner;
import com.bitspace.food.disruptor.inf.Statistical;
import com.bitspace.food.disruptor.inf.EventHandler;
import com.bitspace.food.util.LoggerUtil;
import com.lmax.disruptor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.lmax.disruptor.RingBuffer.createMultiProducer;
import static com.lmax.disruptor.RingBuffer.createSingleProducer;

/**
 * Created by Dev on 2017/8/27.
 */
public class ActorDisruptorImpl implements Actor<Event>,ReqOwner {
    private static Logger log = LoggerFactory.getLogger(ActorDisruptorImpl.class);
    public final static int RING_SIZE = 128 * 1024;//16384;//512*1024;
    public final static EventFactory<Event> EVENT_FACTORY = Event::new;
    
    private AtomicLong receivedCnt = new AtomicLong(0);
    private BatchEventProcessor<Event> eventProcessor;
    private RingBuffer<Event> ringBuffer;
    private EventHandler<Event> handler;
    private boolean useSingleInput;
    private ResponseHandler responseHandler;
    
    //priorityStack is not thread-safe
    private List<Event> priorityStack = new LinkedList<>();
    private Long[] stat = Statistical.newStat();
    
    public ActorDisruptorImpl(boolean useSingleInput, EventHandler<Event> handler) {
        this.useSingleInput = useSingleInput;
        this.handler = handler;
        this.responseHandler = new ResponseHandler(this);
        this.responseHandler.setStat(this.stat);
        this.handler.setPromiseMap(this.responseHandler.getPromiseMap());
        this.handler.setResponseHandler(this.responseHandler);
        this.responseHandler.setPriorityStack(this.priorityStack);
        start();
    }
    
    public ActorDisruptorImpl(boolean useSingleInput, EventHandler<Event> handler, Byte processType) {
        this.useSingleInput = useSingleInput;
        this.handler = handler;
        this.responseHandler = new ResponseHandler(this);
        this.responseHandler.setStat(this.stat);
        this.handler.setPromiseMap(this.responseHandler.getPromiseMap());
        this.handler.setResponseHandler(this.responseHandler);
        this.responseHandler.setPriorityStack(this.priorityStack);
        this.responseHandler.setProcessType(processType);
        start();
    }
    
/*    public ActorDisruptorImpl(boolean useSingleInput, AnnotatedHandler handler, Byte processType) {
        this(useSingleInput, new AnnotationHandlerAdapter(handler), processType);
    }
    */
    
    
    public ActorDisruptorImpl(boolean useSingleInput, AnnotatedHandler handler, Byte processType) {
        this(useSingleInput, new AnnotationHandlerAdapter(handler), processType);
    }
    @Override
    public void start() {
        ActorDisruptorImpl self = this;
        ringBuffer = useSingleInput ? createSingleProducer(EVENT_FACTORY, RING_SIZE, new BlockingWaitStrategy()) : createMultiProducer(EVENT_FACTORY, RING_SIZE, new BlockingWaitStrategy());
        
        com.lmax.disruptor.EventHandler<Event> eventHandler = (o, l, b) -> {
            while (priorityStack.size() > 0) {
                Event x = priorityStack.remove(0);
                handle(x);
            }
            if (Objects.equals(o.getType(), EventType.PRIORITY_NOTIFICATION)) return;
            handle(o);
        };
        eventProcessor = new BatchEventProcessor<>(ringBuffer, ringBuffer.newBarrier(), eventHandler);
        eventProcessor.setExceptionHandler(new ExceptionHandler<Event>() {
            @Override
            public void handleEventException(Throwable ex, long sequence, Event event) {
                try {
                    LoggerUtil.error(log, "Exception processing: " + sequence + " " + event, ex);
                    if (event.getData() instanceof Request) {
                        Request request = (Request) event.getData();
                        if(request instanceof ControllerRequest)request.setOriginatingProcessor(ProcessorType.CONTROLLER);
                        Result result = new Result(ErrMessage.CODE_SYS_UNKNOWN);
                        handler.sendResponse(request, result);
                    }
                }catch(Throwable t){
                    LoggerUtil.error(log,"error when handling exception",t);
                }finally {
                    stat[PROC_CNT]++;
                }
            }
            
            @Override
            public void handleOnStartException(Throwable ex) {
                log.warn("Exception during onStart()", ex);
            }
            
            @Override
            public void handleOnShutdownException(Throwable ex) {
                log.warn("Exception during onShutdown()", ex);
            }
            
        });
        ringBuffer.addGatingSequences(eventProcessor.getSequence());
        
        // Each EventProcessor can run on a separate thread
        this.stat[START_AT] = System.currentTimeMillis();
        Thread t = new Thread(eventProcessor);
        if(this.handler!=null)t.setName(this.handler.getIdentifier()+"-"+this.hashCode());
        t.start();
    }
    
    @Override
    public void receive(EventType eventType, Object data) {
        long sequence = ringBuffer.next();
        Event event = ringBuffer.get(sequence);
        event.setType(eventType);
        event.setData(data);
        event.setSequenceNo(sequence);
        event.setTimeStamp(System.currentTimeMillis());
        ringBuffer.publish(sequence);
        this.receivedCnt.incrementAndGet();
    }
    
    @Override
    public void terminate() {
        eventProcessor.halt();
    }
    
    @Override
    public Long[] getStatistics() {
        this.stat[RECV_CNT]=receivedCnt.get();
        return stat;
    }
    
    @Override
    public boolean isMine(String requestId) {
        return responseHandler.isMine(requestId);
    }
    
    /**
     * priorityStack is not thread-safe, so this method is not thread safe, expecting a synchronous call which happens
     * in the same thread
     * @param eventType
     * @param data
     */
    @Override
    public void handle(EventType eventType, Object data) {
        Event o = new Event();
        o.setType(eventType);
        o.setData(data);
        o.setTimeStamp(System.currentTimeMillis());
        priorityStack.add(0,o);
        receive(EventType.PRIORITY_NOTIFICATION,null);
    }
    
    private void handle(Event o){
        long start = System.nanoTime();
        if(!this.responseHandler.filterOutReceivedEvent(o)) {
            if (o.getData() instanceof Response) {
                Response response = (Response) o.getData();
                this.responseHandler.handleResponse(response, o);
            } else {
                this.handler.onEvent(o);
            }
        }
        this.stat[PROC_TIME] += ((System.nanoTime() - start) / 1000L);
        this.stat[PROC_CNT]++;
        Long latency = System.currentTimeMillis()-o.getTimeStamp();
        if(latency>0L) {
            this.stat[TTL_LAT] += latency;
            this.stat[MAX_LAT] = Math.max(this.stat[MAX_LAT],latency);
        }
    }

}
