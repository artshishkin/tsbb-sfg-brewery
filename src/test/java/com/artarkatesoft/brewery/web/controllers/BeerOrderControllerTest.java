package com.artarkatesoft.brewery.web.controllers;

import com.artarkatesoft.brewery.domain.Customer;
import com.artarkatesoft.brewery.services.BeerOrderService;
import com.artarkatesoft.brewery.web.model.*;
import org.assertj.core.util.Lists;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sun.print.resources.serviceui;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    private Customer customer;
    private BeerOrderDto validOrder;
    private List<BeerOrderDto> orders;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .customerName("Art")
                .id(UUID.randomUUID())
                .build();

        validOrder = BeerOrderDto.builder()
                .customerId(customer.getId())
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .id(UUID.randomUUID())
                .orderStatus(OrderStatusEnum.NEW)
                .build();

        BeerOrderDto validOrder2 = BeerOrderDto.builder()
                .customerId(customer.getId())
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .id(UUID.randomUUID())
                .orderStatus(OrderStatusEnum.NEW)
                .build();

        orders = Lists.list(validOrder, validOrder2);
    }

    @AfterEach
    void tearDown() {
        //why we need to reset MockBean???
        reset(beerOrderService);
    }

    @DisplayName("Order List Test")
    @Test
    void listOrders() throws Exception {

        BeerOrderPagedList beerOrderPagedList = new BeerOrderPagedList(orders);

        given(beerOrderService.listOrders(any(), any())).willReturn(beerOrderPagedList);

        UUID customerId = UUID.randomUUID();
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/customers/{customerId}/orders", customerId)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].customerId", Is.is(customer.getId().toString())))
                .andExpect(jsonPath("$.content[1].customerId", Is.is(customer.getId().toString())))
                .andReturn();

        printMvcResult(mvcResult);
        verify(beerOrderService).listOrders(any(), any());
        verifyNoMoreInteractions(beerOrderService);
    }


    @Test
    void getOrder() throws Exception {
        when(beerOrderService.getOrderById(any(UUID.class), any(UUID.class)))
                .thenReturn(validOrder);

        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/customers/{customerId}/orders/{orderId}", customer.getId(), UUID.randomUUID())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId", Is.is(customer.getId().toString())))
                .andExpect(jsonPath("$.id", Is.is(validOrder.getId().toString())))
                .andReturn();
//        printMvcResult(mvcResult);
        verify(beerOrderService).getOrderById(any(), any());
        verifyNoMoreInteractions(beerOrderService);
    }

    private void printMvcResult(MvcResult mvcResult) throws UnsupportedEncodingException {
        System.out.println("------------------HELLO------------------");
        System.out.println(mvcResult.getResponse().getContentAsString());
        System.out.println("------------------BYE------------------");
    }


}
