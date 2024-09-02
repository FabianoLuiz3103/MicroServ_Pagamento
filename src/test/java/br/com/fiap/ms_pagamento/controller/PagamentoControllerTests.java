package br.com.fiap.ms_pagamento.controller;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.service.PagamentoService;
import br.com.fiap.ms_pagamento.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoService pagamentoService;
    private PagamentoDTO pagamentoDTO;
    private Long existingId;
    private Long nonExistId;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        pagamentoDTO = Factory.createPagamentoDTO();

        List<PagamentoDTO> list = List.of(pagamentoDTO);

        when(pagamentoService.findAll()).thenReturn(list);
    }

    @Test
    public void findAllShouldReturnListPagamentoDTO() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/pagamentos").accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
    }
}
