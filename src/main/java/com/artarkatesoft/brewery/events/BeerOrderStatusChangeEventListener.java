/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.artarkatesoft.brewery.events;

import com.artarkatesoft.brewery.web.mappers.DateMapper;
import com.artarkatesoft.brewery.web.model.OrderStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Component
public class BeerOrderStatusChangeEventListener {

    RestTemplate restTemplate;
    DateMapper dateMapper = new DateMapper();

    public BeerOrderStatusChangeEventListener(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    @EventListener
    public void listen(BeerOrderStatusChangeEvent event) {
        System.out.println("I got an order status change event");
        System.out.println(event);

        OrderStatusUpdate update = OrderStatusUpdate.builder()
                .id(event.getBeerOrder().getId())
                .orderId(event.getBeerOrder().getId())
                .version(event.getBeerOrder().getVersion() != null ? event.getBeerOrder().getVersion().intValue() : null)
                .createdDate(dateMapper.asOffsetDateTime(event.getBeerOrder().getCreatedDate()))
                .lastModifiedDate(dateMapper.asOffsetDateTime(event.getBeerOrder().getLastModifiedDate()))
                .orderStatus(event.getBeerOrder().getOrderStatus().toString())
                .customerRef(event.getBeerOrder().getCustomerRef())
                .build();
        try {
            log.debug("Posting to callback url");
            restTemplate.postForObject(event.getBeerOrder().getOrderStatusCallbackUrl(), update, String.class);
        } catch (Throwable t) {
            log.error("Error Preforming callback for order: " + event.getBeerOrder().getId(), t);
        }
    }

    public String getRequest(String url){
        return restTemplate.getForObject(url, String.class);
    }
    @Async
    public Future<String> getRequestAsync(String url){
        return CompletableFuture.completedFuture(restTemplate.getForObject(url, String.class));
    }
}
