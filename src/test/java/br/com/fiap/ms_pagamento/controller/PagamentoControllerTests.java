package br.com.fiap.ms_pagamento.controller;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.service.PagamentoService;
import br.com.fiap.ms_pagamento.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;

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
}
