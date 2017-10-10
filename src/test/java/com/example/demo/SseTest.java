package com.example.demo;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SseTest {

	@LocalServerPort
	private int port = 8080;

	@Autowired
	SseController sseController;

	private static final Logger logger = LoggerFactory.getLogger(SseTest.class);

	@Test
	public void test() throws IOException, InterruptedException {

		Client client = ClientBuilder.newBuilder().register(new SseFeature()).build();
		WebTarget target = client.target("http://localhost:" + port);
		EventInput e = null;

		AtomicInteger clientCallsNo = new AtomicInteger();

		Random r = new Random();
		int low = 1;
		int high = 5;

		for (int q = 0; q < 40; q++) {
			int secs = r.nextInt(high - low) + low;
			// we will close the connection after random number of seconds between 1 and 5
			connectAndClose(target.path("/sse/" + clientCallsNo.incrementAndGet()), e, secs);
		}
	}

	private void connectAndClose(WebTarget target, EventInput e, int secsBeforeClose) {
		if (e == null || e.isClosed()) {
			// (re)connect
			logger.info("[CLIENT] connecting...");
			e = target.request().get(EventInput.class);
			e.setChunkType("text/event-stream");
		}

		for (int i = 0; i < secsBeforeClose; i++) {
			final InboundEvent inboundEvent = e.read();
			if (inboundEvent == null) {
				logger.info("[CLIENT] server stream stopped");
				break;
			}
			else {
				String data = inboundEvent.readData();
				logger.info("[CLIENT] client receives: " + data);

			}
		}
		logger.info("[CLIENT] client closes connection");
		e.close();
	}
}
