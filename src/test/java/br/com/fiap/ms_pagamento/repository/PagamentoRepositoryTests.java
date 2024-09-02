package br.com.fiap.ms_pagamento.repository;

import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class PagamentoRepositoryTests {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalPagamento;

    @BeforeEach
    void setup() throws  Exception{
        existingId = 1L;
        nonExistingId = 100L;
        countTotalPagamento = 3L;
    }

    @Test
    @DisplayName("Deveria excluir pagamento quando o Id existe")
    public void deleteShouldDeletObjectWhenIdExists(){
        pagamentoRepository.deleteById(existingId);

        Optional<Pagamento> result = pagamentoRepository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDosNotExist(){
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            pagamentoRepository.deleteById(nonExistingId);
        });
    }

    @Test
    @DisplayName("sava Deveria salvar o objeto com auto incremento quando o id é nulo")
    public void saveShouldPersistWhitAutoIncrementWhenIdIsNull(){

        Pagamento pagamento = Factory.createPagamento();
        pagamento.setId(null);
        pagamentoRepository.save(pagamento);
        Assertions.assertNotNull(pagamento.getId());

        Assertions.assertEquals(countTotalPagamento + 1, pagamento.getId());
    }

    @Test
    @DisplayName("findById Deveria retornar um Optional não vazio quando o Id existe")
    public void findByIdShoulReturnNonEmptyOptionalWhenExistsId(){
        Optional<Pagamento> result = pagamentoRepository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("findById Deveria retornar um Optional vazio quando o Id não existe")
    public void findByIdShoulReturnEmptyOptionalWhenExistsId(){
        Optional<Pagamento> result = pagamentoRepository.findById(nonExistingId);
        Assertions.assertFalse(result.isPresent());
        // OU
        Assertions.assertTrue(result.isEmpty());
    }
}
