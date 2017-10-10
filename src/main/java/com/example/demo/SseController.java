package com.example.demo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Created by oxygen on 18.08.17.
 */
@RestController
public class SseController {
	private static final Logger logger = LoggerFactory.getLogger(SseController.class);


	public AtomicInteger serverCallCount = new AtomicInteger();

	private AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("SSE-");

	@RequestMapping("/sse/{callNumber}")
	public SseEmitter sseEmitter(@PathVariable int callNumber, HttpServletRequest request) {
		final int currentCallCount = serverCallCount.incrementAndGet();
		logger.info("[SERVER] /sse/{}, server call {}, dispatcher {}",
				callNumber, currentCallCount, request.getDispatcherType().toString());
		Assert.state(currentCallCount == callNumber,
				"Server call " + currentCallCount + "; client call " + callNumber +
						"; dispatcher: " + request.getDispatcherType().toString());
		final SseEmitter emitter = new SseEmitter();

		executor.execute(() -> {
			try {
				for (int i = 0; i < 1000; i++) {
					logger.info("[SERVER] next: {}", i);
					emitter.send("next: " + i);
					Thread.sleep(1000L);
				}
				emitter.complete();
			}
			catch (IOException e) {
				logger.info("IOException ");
				emitter.completeWithError(e);
			}
			catch (InterruptedException e) {
				logger.info("[SERVER] interrupted...");
				emitter.completeWithError(e);
			}
		}, AsyncTaskExecutor.TIMEOUT_IMMEDIATE);

		return emitter;
	}

}

