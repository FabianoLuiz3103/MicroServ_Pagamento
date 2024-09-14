package br.com.fiap.ms_pagamento.controller;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.model.Status;
import br.com.fiap.ms_pagamento.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PagamentoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    //preparando os dados
    private Long exitingId;
    private Long nonExistingId;
    private PagamentoDTO pagamentoDTO;
    //converte obj para JSON para enviar requisições

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception{
        exitingId = 1L;
        nonExistingId = 50L;
        pagamentoDTO = Factory.createPagamentoDTO();
    }

    @Test
    public void findAllShouldReturnListAllPagamentos() throws Exception {

        mockMvc.perform(get("/pagamentos")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].nome").isString())
                .andExpect(jsonPath("[0].nome").value("Nicodemus C Souza"))
                .andExpect(jsonPath("[5].status").value("CONFIRMADO"));
    }

    @Test
    public void findByIdShouldReturnPagamentoDTOWhenIdExists() throws Exception {

        mockMvc.perform(get("/pagamentos/{id}", exitingId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("nome").isString())
                .andExpect(jsonPath("nome").value("Nicodemus C Souza"))
                .andExpect(jsonPath("status").value("CRIADO"));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        //NotFound - código 404
        mockMvc.perform(get("/pagamentos/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void insertShouldReturnPagamentoDTO() throws Exception {

        var pagamento = Factory.createPagamento();
        pagamento.setId(null);
        String jsonBody = objectMapper.writeValueAsString(pagamento);

        mockMvc.perform(post("/pagamentos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.nome").value("Beach"));
    }

    @Test
    public void insertShouldPersistPagamentoWithRequiredFields() throws Exception {

        var pagamento = new Pagamento(null, BigDecimal.valueOf(25.25), null, null, null,
                null, Status.CRIADO, 7L, 1L);

        String jsonBody = objectMapper.writeValueAsString(pagamento);
        mockMvc.perform(post("/pagamentos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.nome").isEmpty());
    }

    @Test
    @DisplayName("Insert deve lançar exception quando dados inválidos e retornar status 422")
    public void insertShouldThrowsExceptionWhenInvalidData() throws Exception {
        var pagamento = new Pagamento();
        pagamento.setValor(BigDecimal.valueOf(25.25));
        pagamento.setFormaDePagamentoId(1L);

        String bodyJson = objectMapper.writeValueAsString(pagamento);
        mockMvc.perform(post("/pagamentos")
                .content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateShouldUpdateAndReturnPagamentoDTOWhenIdExists() throws Exception {
        //status 200
        String jsonBody = objectMapper.writeValueAsString(pagamentoDTO);
        mockMvc.perform(put("/pagamentos/{id}", exitingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(pagamentoDTO);
        mockMvc.perform(put("/pagamentos/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(delete("/pagamentos/{id}", exitingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        mockMvc.perform(delete("/pagamentos/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
