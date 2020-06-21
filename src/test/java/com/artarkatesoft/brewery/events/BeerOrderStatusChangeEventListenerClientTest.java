package com.artarkatesoft.brewery.events;

import com.artarkatesoft.brewery.domain.BeerOrder;
import com.artarkatesoft.brewery.domain.OrderStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BeerOrderStatusChangeEventListener.class)
class BeerOrderStatusChangeEventListenerClientTest {

    @Autowired
    private BeerOrderStatusChangeEventListener listener;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void listen() throws InterruptedException {
        //given
        BeerOrder beerOrder = BeerOrder.builder()
                .orderStatus(OrderStatusEnum.READY)
                .id(UUID.randomUUID())
                .orderStatusCallbackUrl("/update")
                .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        BeerOrderStatusChangeEvent event = new BeerOrderStatusChangeEvent(beerOrder, OrderStatusEnum.NEW);

        server.expect(requestTo("/update"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        //when
        listener.listen(event);

        //then
        Thread.sleep(500);//Because listen() is asynchronous method we need to set pause
        server.verify();
    }
}
