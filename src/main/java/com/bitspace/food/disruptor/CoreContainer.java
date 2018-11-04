package com.bitspace.food.disruptor;



import com.bitspace.food.base.ProcessorType;
import com.bitspace.food.disruptor.impl.*;
import com.bitspace.food.disruptor.inf.Actor;
import com.bitspace.food.entity.Admin;
import com.bitspace.food.handler.*;
import com.bitspace.food.handler.db.AdminDBHandler;
import com.bitspace.food.handler.db.AnnouncementDBHandler;
import com.bitspace.food.handler.db.UserDBHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CoreContainer {
    private Dispatcher d;
    private boolean init = false;
    private static CoreContainer cc;

    private Actor accountActor;
    private Actor userActor;
    private Actor controllerAppActor;
    private Actor controllerActor;
    private Actor dbActor;
    private Actor userDBdbActor;
    private Actor captchaActor;
    private Actor httpActor;
    private Actor goodsActor;
    private Actor adminActor;
    private Actor merchantActor;


    private  Logger log = LoggerFactory.getLogger(CoreContainer.class);
    static {
        if (cc == null)
            cc = new CoreContainer();
    }

    private CoreContainer() {
    }

    public static CoreContainer getInstance() {
        return cc;
    }

    @SuppressWarnings("unchecked")
    public void init() {
        if (init)
            return;


        System.setProperty("jsse.enableSNIExtension", "false");
        d = Dispatcher.getInstance();

        controllerActor = new ActorRoundRobinDispatch(
                IntStream.range(0, 10).mapToObj(
                        i -> new ActorDisruptorImpl(false, new ControllerHandler())
                ).collect(Collectors.toList())
        );
        d.registerSender(ProcessorType.CONTROLLER, new SenderPromiseImpl(controllerActor));

        controllerAppActor = new ActorRoundRobinDispatch(
                IntStream.range(0, 10).mapToObj(
                        i -> new ActorDisruptorImpl(false, new ControllerAppHandler())
                ).collect(Collectors.toList())
        );
        d.registerSender(ProcessorType.CONTROLLER_APP, new SenderPromiseImpl(controllerAppActor));

        dbActor = new ActorUidDispatch(
                IntStream.range(0, 10).mapToObj(
                        i -> new ActorDisruptorImpl(false,
                                new AnnotatedCombinationHandler(new UserDBHandler(), new AnnouncementDBHandler()),ProcessorType.DB)
                ).collect(Collectors.toList()),
                ProcessorType.DB

        );
        d.registerSender(ProcessorType.DB, new SenderPromiseImpl(dbActor));

        captchaActor = new ActorDisruptorImpl(false, new CaptchaHandler(), ProcessorType.CAPTCHA);
        d.registerSender(ProcessorType.CAPTCHA, new SenderPromiseImpl(captchaActor));


        userActor = new ActorDisruptorImpl(false, new AnnotatedCombinationHandler(new UserHandler()),ProcessorType.USER);

        adminActor = new ActorDisruptorImpl(false, new AnnotatedCombinationHandler(new AdminHandler(), new AdminDBHandler()),ProcessorType.ADMIN);

        d.registerSender(ProcessorType.ADMIN, new SenderPromiseImpl(adminActor));

        merchantActor = new ActorDisruptorImpl(false, new AnnotatedCombinationHandler(new MerchantHandler()),ProcessorType.MERCHANT);

        d.registerSender(ProcessorType.MERCHANT, new SenderPromiseImpl(merchantActor));

        d.registerSender(ProcessorType.USER, new SenderPromiseImpl(userActor));

        d.registerSender(ProcessorType.DB, new SenderPromiseImpl(dbActor));

        httpActor=new ActorDisruptorImpl(false, new HttpHandler(),ProcessorType.HTTP);
        d.registerSender(ProcessorType.HTTP, new SenderPromiseImpl(httpActor));
    
        goodsActor=new ActorDisruptorImpl(false, new GoodsHandler(),ProcessorType.GOODS);
        d.registerSender(ProcessorType.GOODS, new SenderPromiseImpl(goodsActor));

    }

    public void terminate() {
        if (!init)
            return;

        this.accountActor.terminate();
        this.userActor.terminate();
        this.userDBdbActor.terminate();
        this.controllerActor.terminate();
        this.controllerAppActor.terminate();
        this.dbActor.terminate();
        this.httpActor.terminate();
        this.goodsActor.terminate();
    }
}