package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * Created by oxygen on 18.08.17.
 */
@RestController
public class SseController {
    private static final Logger logger = LoggerFactory.getLogger(SseController.class);


//    @RequestMapping("/exception")
//    public ResponseEntity<String> exception() {
//        throw new RuntimeException("test");
//    }

    public volatile int callsNo = 0;

    @RequestMapping("/sse")
    public SseEmitter sseEmitter(int callNo) {
        logger.info("/sse/{}", callNo);
        callsNo ++;
        SseEmitter emitter = new SseEmitter();

        new Thread(()->{
            for (int i = 0; i < 1000; i++) {
                try {
                    logger.info("next: {}", i);
                    emitter.send("next: " + i);
                } catch (IOException e) {
                    logger.info("IOException ");
                    break;
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    logger.info("interrupted...");
                    break;
                }
            }
            emitter.complete();
        }).start();

        return emitter;

    }

//    @RequestMapping("/sse2")
//    public SseEmitter sse2() {
//
//        System.err.println("/sse");
//
//        SseEmitter emitter = new SseEmitter();
//
//
//        Disposable s = Flux.create(fluxSink -> {
//
//            AtomicBoolean cancelled = new AtomicBoolean(false);
////                fluxSink.onDispose(d->System.err.println("disposed"));
//            fluxSink
//                    .onDispose(() -> {
//                        System.err.println("onCancel");
//                        cancelled.set(true);
//                    });
//
////            fluxSink.onDipose(() -> cancelled.set(true));
//
//            for (int i = 0; i < 10000; i++) {
//                System.err.println(i);
//                if (cancelled.get()) {
//                    System.err.println("isCanceled");
//                    break;
//                }
//                try {
//                    fluxSink.next("next " + i);
//                } catch (RuntimeException e) {
//                    System.err.println("error in sink: "+e.getMessage());
//                    if (cancelled.get()) {
//                        break;
//                    }
//                }
//                try {
//                    Thread.sleep(1000L);
//                } catch (InterruptedException e) {
//                    System.err.println("interrupted");
//                    break;
//                }
//            }
//
//            System.err.println("complete2");
//            fluxSink.complete();
//        })
//                .subscribeOn(Schedulers.single())
//                .doOnSubscribe(d-> System.err.println("subscribe"))
////                .onerror
//                .subscribe(m->{
//            try {
//                if ("next 10".equals(m)) {
//                    throw new RuntimeException("dasdsad");
////                    emitter.completeWithError(new RuntimeException("dasdsad"));
////                    emitter.send(SseEmitter.event().reconnectTime(0));
////                    emitter.complete();
////                    emitter.send(SseEmitter.event().reconnectTime(0));
//                }
//                emitter.send(m);
//            } catch (IOException e) {
//                System.err.println("IO exception");
//                throw new RuntimeException(e);
//            }
//        }, ex -> {
//            System.err.println("completeWithError");
//            emitter.complete();
////            emitter.completeWithError(ex);
//
////            throw new RuntimeException(ex);
//        }, () -> {
//            System.err.println("complete");
//
//            emitter.complete();
//        });
//
//        emitter.onCompletion(() -> {
//            System.err.println("onCompletion");
////            s.dispose();
//        });
//        emitter.onTimeout(()-> System.err.println("onTimeerr"));
//
//        return emitter;
//    }
//


}

