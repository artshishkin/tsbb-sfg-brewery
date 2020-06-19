package com.artarkatesoft.brewery.web.controllers;

import com.artarkatesoft.brewery.domain.Customer;
import com.artarkatesoft.brewery.repositories.CustomerRepository;
import com.artarkatesoft.brewery.web.model.BeerOrderPagedList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerTestIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CustomerRepository customerRepository;

    Customer customer;

    @BeforeEach
    void setUp() {
        List<Customer> allCustomers = customerRepository.findAll();
        System.out.println(allCustomers);
        customer = allCustomers.get(0);
//        System.out.println("customer.getCustomerName():" + customer.getCustomerName());
//        System.out.println("customer.getId():" + customer.getId());
    }

    @Test
    void listOrders() {
        BeerOrderPagedList beerOrderPagedList = restTemplate.getForObject("/api/v1/customers/{customerId}/orders", BeerOrderPagedList.class, customer.getId());
        assertThat(beerOrderPagedList).isNotNull().hasSize(1);
//        System.out.println(beerOrderPagedList.getContent());
    }
}
