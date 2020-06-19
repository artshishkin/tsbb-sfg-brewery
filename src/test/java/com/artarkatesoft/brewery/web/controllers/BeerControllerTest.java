package com.artarkatesoft.brewery.web.controllers;

import com.artarkatesoft.brewery.services.BeerService;
import com.artarkatesoft.brewery.web.model.BeerDto;
import com.artarkatesoft.brewery.web.model.BeerPagedList;
import com.artarkatesoft.brewery.web.model.BeerStyleEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.util.Lists;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @MockBean
    BeerService beerService;

    @Autowired
    MockMvc mockMvc;

    private BeerDto validBeer = BeerDto.builder()
            .beerName("BeerName")
            .beerStyle(BeerStyleEnum.PORTER)
            .price(BigDecimal.valueOf(23.3))
            .id(UUID.randomUUID())
            .version(1)
            .quantityOnHand(4)
            .upc(123456789012L)
            .createdDate(OffsetDateTime.now())
            .lastModifiedDate(OffsetDateTime.now())
            .build();
    private BeerPagedList beerPagedList;

    @BeforeEach
    void setUp() {

        List<BeerDto> beers = Lists.list(validBeer, BeerDto.builder()
                .beerName("Beer4")
                .beerStyle(BeerStyleEnum.PORTER)
                .price(BigDecimal.valueOf(23.3))
                .id(UUID.randomUUID())
                .version(2)
                .quantityOnHand(4)
                .upc(123456789012L)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build());

        beerPagedList = new BeerPagedList(beers, PageRequest.of(1, 1), 2);
    }

    @Test
    void getBeerById() throws Exception {
        //given
        given(beerService.findBeerById(any(UUID.class))).willReturn(validBeer);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/beer/{beerId}", validBeer.getId()));
        //then
        MvcResult result = resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is("BeerName")))
                .andExpect(jsonPath("$.createdDate", is(dateTimeFormatter.format(validBeer.getCreatedDate()))))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Nested
    @DisplayName("List Ops:")
    class ListTests{

        @Captor
        ArgumentCaptor<String> beerNameCaptor;

        @Captor
        ArgumentCaptor<BeerStyleEnum> beerStyleEnumCaptor;

        @DisplayName("Test list beers - No parameters")
        @Test
        void listBeersAnything() throws Exception {
            //given
            given(beerService.listBeers(beerNameCaptor.capture(), beerStyleEnumCaptor.capture(), any(PageRequest.class))).willReturn(beerPagedList);
            //when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/beer").accept(APPLICATION_JSON));
            //then
            MvcResult mvcResult = resultActions.andExpect(status().isOk())
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(validBeer.getId().toString())))
                    .andReturn();

//            System.out.println(mvcResult.getResponse().getContentAsString());
        }
    }
}
