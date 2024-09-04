package br.com.fiap.ms_pagamento.controller;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.service.PagamentoService;
import br.com.fiap.ms_pagamento.service.exception.ResourceNotFoundException;
import br.com.fiap.ms_pagamento.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

        existingId = 1L;
        nonExistId = 10L;

        // findById service
        when(pagamentoService.findById(existingId)).thenReturn(pagamentoDTO);
        // findById com id não existente
        when(pagamentoService.findById(nonExistId)).thenThrow(ResourceNotFoundException.class);

        when(pagamentoService.insert(any())).thenReturn(pagamentoDTO);
    }

    @Test
    public void findAllShouldReturnListPagamentoDTO() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/pagamentos").accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnPagamentoDTOWhenIdExists() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/pagamentos/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());

        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.valor").exists());
        resultActions.andExpect(jsonPath("$.status").exists());
    }

    @Test
    @DisplayName("findById deve retornar NotFound quando id não existe - Erro 404")
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/pagamentos/{id}", nonExistId)
                .accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnProductDTOCreated() throws Exception{

        //POST tem corpo - JSON
        //Bean objectMapper para converter JAVA para JSON

        PagamentoDTO newDTO = Factory.createNewPagamentoDTO();
        String jsonBody = objectMapper.writeValueAsString(newDTO);

        mockMvc.perform(post("/pagamentos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }


}
