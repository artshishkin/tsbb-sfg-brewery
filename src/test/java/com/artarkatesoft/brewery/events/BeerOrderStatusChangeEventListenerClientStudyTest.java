package com.artarkatesoft.brewery.events;

import com.artarkatesoft.brewery.domain.BeerOrder;
import com.artarkatesoft.brewery.domain.OrderStatusEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BeerOrderStatusChangeEventListener.class)
class BeerOrderStatusChangeEventListenerClientStudyTest {

    @Autowired
    private BeerOrderStatusChangeEventListener listener;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

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

    @Test
    @Disabled("Internal calls lead to an error")
    void studyMockServerError() {
        //given
        server.expect(requestTo("/test"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("Hello Art", MediaType.TEXT_PLAIN));
        RestTemplate restTemplate = restTemplateBuilder.build();

        //when
        String answer = restTemplate.getForObject("/test", String.class);

        //then
        Assertions.assertThat(answer).isEqualTo("Hello Art");
        server.verify();
    }

    @Test
    void studyMockServerGet() {
        //given
        server.expect(requestTo("/test"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("Hello Art", MediaType.TEXT_PLAIN));

        //when
        String answer = listener.getRequest("/test");

        //then
        Assertions.assertThat(answer).isEqualTo("Hello Art");
        server.verify();
    }

    @Test
    void studyMockServerGetAsync() throws InterruptedException, TimeoutException, ExecutionException {
        //given
        server.expect(requestTo("/test"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("Hello Art", MediaType.TEXT_PLAIN));

        //when
        Future<String> futureAnswer = listener.getRequestAsync("/test");

        //then
//        String answer = futureAnswer.get(500, TimeUnit.MILLISECONDS);
        Thread.sleep(500);
        server.verify();
//        Assertions.assertThat(answer).isEqualTo("Hello Art");
    }
}
