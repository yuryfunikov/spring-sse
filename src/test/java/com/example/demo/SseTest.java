package com.example.demo;

import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import sun.net.www.http.HttpClient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SseTest {

    @LocalServerPort
    private int port = 8080;

    @Autowired
    SseController sseController;

    @Autowired
    private TestRestTemplate restTemplate;

    HttpClient client;

    @Test
    public void test() throws IOException, InterruptedException {

        Client client = ClientBuilder.newBuilder().register(new SseFeature()).build();
        WebTarget target = client.target("http://localhost:" + port + "/sse");
        EventInput e = null;

        AtomicInteger clientCallsNo = new AtomicInteger();

        Random r = new Random();
        int low = 1;
        int high = 5;

        for (int q = 0; q < 40; q++) {
            int secs = r.nextInt(high - low) + low;
            // we will close the connection after random number of seconds between 1 and 5
            connectAndClose(target.queryParam("callNo", clientCallsNo.get()), e, clientCallsNo, secs);

            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);

                System.out.println(MessageFormat.format("server calls no: {0}, client calls no: {1}", sseController.callsNo, clientCallsNo.get()));
                assertEquals(clientCallsNo.get(), sseController.callsNo);
            }
        }
    }

    private static void connectAndClose(WebTarget target, EventInput e, AtomicInteger clientCallsNo, int secsBeforeClose) {
        for (int i = 0; i < secsBeforeClose; i++) {
            if (e == null || e.isClosed()) {
                // (re)connect
                e = target.request().get(EventInput.class);

                e.setChunkType("text/event-stream");
                System.out.println("connecting...");
                clientCallsNo.incrementAndGet();
            }
            final InboundEvent inboundEvent = e.read();
            if (inboundEvent == null) {
                System.out.println("server stream stopped");
                break;
            } else {
                String data = inboundEvent.readData();
                System.out.println(" client receives: " + data);

            }

        }

        System.out.println("client closes connection");
        e.close();
    }
}
