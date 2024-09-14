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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTests {

    @Autowired
    private MockMvc mockMvc; //chamar o endpoint
    //controller tem dependência do service
    //dependência mockada

    @MockBean
    private PagamentoService pagamentoService;
    private PagamentoDTO pagamentoDTO;
    private Long existingId;
    private Long nonExistId;

    //Converter para JSON o objeto Java e enviar na requisição
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        //criando um PagamentoDTO
        pagamentoDTO = Factory.createPagamentoDTO();

        //listando PagamentoDTO
        List<PagamentoDTO> list = List.of(pagamentoDTO);

        //simulando comportamento do service - findAll
        when(pagamentoService.findAll()).thenReturn(list);

        existingId = 1L;
        nonExistId = 10L;

        // findById service
        when(pagamentoService.findById(existingId)).thenReturn(pagamentoDTO);
        // findById com id não existente
        when(pagamentoService.findById(nonExistId)).thenThrow(ResourceNotFoundException.class);

        //any() simula o comportamento de qualquer objeto
        when(pagamentoService.insert(any())).thenReturn(pagamentoDTO);

        //quando usamos any() não pode usar objetos simples
        //então precisados do eq()

        when(pagamentoService.update(eq(existingId), any())).thenReturn(pagamentoDTO);
        when(pagamentoService.update(eq(nonExistId), any())).thenThrow(ResourceNotFoundException.class);

        //quando o retorno é void - delete
        doNothing().when(pagamentoService).delete(existingId);
        //quando não existe
        doThrow(ResourceNotFoundException.class).when(pagamentoService).delete(nonExistId);
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
                .contentType(MediaType.APPLICATION_JSON) // requisição
                .accept(MediaType.APPLICATION_JSON)) // resposta
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.status").exists());
    }


    @Test
    public void updateShpuldReturnPagamentoDTOWhenIdExits() throws Exception {

        //PUT tem corpo JSON
        //precisamos passar o corpo da requisição
        //Bean objectMapper para converter JAVA para JSON
        String jsonBody = objectMapper.writeValueAsString(pagamentoDTO);

        mockMvc.perform(put("/pagamentos/{id}", existingId)
                .content(jsonBody) //requisição
                .contentType(MediaType.APPLICATION_JSON) //requisição
                .accept(MediaType.APPLICATION_JSON))//resposta
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.status").exists());
    }


    @Test
    @DisplayName("Update deve retornar NotFound quando id Não existe - Erro 404")
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws  Exception {

        //PUT tem corpo - JSON
        //precisamos passar o corpo da requisição
        //Bean objectMapper para converter JAVA para JSON

        String jsonBody = objectMapper.writeValueAsString(pagamentoDTO);

        mockMvc.perform(put("/pagamento/{id}", nonExistId)
                .content(jsonBody)//req
                .contentType(MediaType.APPLICATION_JSON)//req
                .accept(MediaType.APPLICATION_JSON))//resp
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception{

        mockMvc.perform(delete("/pagamentos/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("delete deve retornar NotFound - 404")
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{

        mockMvc.perform(delete("/pagamentos/{id}", nonExistId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}
