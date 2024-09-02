package br.com.fiap.ms_pagamento.tests;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.model.Status;

import java.math.BigDecimal;

public class Factory {

    public static Pagamento createPagamento(){

        Pagamento pagamento = new Pagamento(1L, BigDecimal.valueOf(32.25),
                "Beach", "23651459365541245", "07/28", "585", Status.CRIADO,
                1L, 2L);
        return pagamento;
    }

    public static PagamentoDTO createPagamentoDTO(){
        Pagamento pagamento = createPagamento();
        return new PagamentoDTO(pagamento);
    }
}
