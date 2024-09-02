package br.com.fiap.ms_pagamento.service;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.repository.PagamentoRepository;
import br.com.fiap.ms_pagamento.tests.Factory;
import exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.action.internal.EntityActionVetoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class PagamentoServiceTests {

    @InjectMocks
    private PagamentoService pagamentoService;

    @Mock
    private PagamentoRepository pagamentoRepository;

    private Long existingId;
    private Long nonExistingId;

    private Pagamento pagamento;
    private PagamentoDTO pagamentoDTO;





    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 10L;

        //delete - quando id existe
        Mockito.when(pagamentoRepository.existsById(existingId)).thenReturn(true);
        //delete - quando id não existe
        Mockito.when(pagamentoRepository.existsById(nonExistingId)).thenReturn(false);
        //delete - primeiro caso onde ele deleta
        Mockito.doNothing().when(pagamentoRepository).deleteById(existingId);

        pagamento = Factory.createPagamento();
        pagamentoDTO = new PagamentoDTO(pagamento);

        //findById
        Mockito.when(pagamentoRepository.findById(existingId)).thenReturn(Optional.of(pagamento));
        Mockito.when(pagamentoRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Insert
        Mockito.when(pagamentoRepository.save(any())).thenReturn(pagamento);

        //Update - Id existe
        Mockito.when(pagamentoRepository.getReferenceById(nonExistingId)).thenReturn(pagamento);

        //Update - Id não existe
        Mockito.when(pagamentoRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
    }

    @Test
    public void insertShouldReturnPagamentoDTO(){
        PagamentoDTO result = pagamentoService.insert(pagamentoDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), pagamento.getId());
    }

    @Test
    public void findByIdShouldReturnPagamentoDTOWhenIdExists(){
        PagamentoDTO result = pagamentoService.findById(existingId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingId);
        Assertions.assertEquals(result.getValor(), pagamento.getValor());
    }

    @Test
    public void updateShouldReturnPagamentoDTOWhenIdExists(){

        PagamentoDTO result = pagamentoService.update(pagamento.getId(), pagamentoDTO);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingId);
        Assertions.assertEquals(result.getValor(), pagamento.getValor());
    }
    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesnotExists(){

       Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           pagamentoService.update(nonExistingId, pagamentoDTO);
       });
    }


    @Test
    @DisplayName("delete Deveria não fazer nada quando Id existe")
    public void deleteShouldDoNothingWhenIdExists(){
        Assertions.assertDoesNotThrow(
                () -> {
                    pagamentoService.delete(existingId);
                }
        );
    }
}
