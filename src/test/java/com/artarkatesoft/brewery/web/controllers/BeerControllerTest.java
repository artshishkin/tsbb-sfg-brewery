package com.artarkatesoft.brewery.web.controllers;

import com.artarkatesoft.brewery.services.BeerService;
import com.artarkatesoft.brewery.web.model.BeerDto;
import com.artarkatesoft.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BeerControllerTest {

    @InjectMocks
    BeerController beerController;

    @Mock
    BeerService beerService;

    private MockMvc mockMvc;

    private BeerDto validBeer;

    @BeforeEach
    void setUp() {

        validBeer = BeerDto.builder()
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

        mockMvc = MockMvcBuilders.standaloneSetup(beerController).build();
    }

    @Test
    void getBeerById() throws Exception {
        //given
        given(beerService.findBeerById(any(UUID.class))).willReturn(validBeer);
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/beer/{beerId}", validBeer.getId()));
        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is("BeerName")));
    }


}
