package br.com.fiap.ms_pagamento.service;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
//import br.com.fiap.ms_pagamento.http.PedidoClient;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.model.Status;
import br.com.fiap.ms_pagamento.repository.PagamentoRepository;
import br.com.fiap.ms_pagamento.service.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

//    @Autowired
//    private PedidoClient pedidoClient;

    @Transactional(readOnly = true)
    public List<PagamentoDTO> findAll(){
        List<Pagamento> list = repository.findAll();
        return list.stream().map(PagamentoDTO::new).collect(Collectors.toList());
    }
//    @Transactional(readOnly = true)
//    public Page<PagamentoDTO> findAll(Pageable pageable){
//        Page<Pagamento> page = repository.findAll(pageable);
//        return page.map(PagamentoDTO::new);
//    }

    @Transactional(readOnly = true)
    public PagamentoDTO findById(Long id){
        Pagamento entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado! Id: " + id)
        ) ;

        return new PagamentoDTO(entity);
    }

    @Transactional
    public PagamentoDTO insert (PagamentoDTO dto){
        Pagamento entity = new Pagamento();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new PagamentoDTO(entity);
    }

    //PROPAGATION_SUPPORTS: Mantém uma transação existente,
    //executa o método sem transação caso nenhuma exista
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(! repository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try{
            repository.deleteById(id);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional
    public PagamentoDTO update(Long id, PagamentoDTO dto){
        try{
            // não vai no DB, obj monitorado pelo JPA
            Pagamento entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new PagamentoDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Recurso não encontrado! Id: " + id);
        }
    }

//    @Transactional
//    public void confirmarPagamentoDePedido(Long id){
//
//        Optional<Pagamento> pagamento = repository.findById(id);
//
//        if(pagamento.isEmpty()){
//            throw new ResourceNotFoundException("Recurso não encontrado! Id: " + id);
//        }
//
//        //Set status do pagamento para CONFIRMADO
//        pagamento.get().setStatus(Status.CONFIRMADO);
//        repository.save(pagamento.get());
//        //chama o pedidoClient para fazer a atualização passando o ID do pedido
//        //passando o ID do pedido e quem tem essa informação é o pagamento.get().getPedidoId().
//        //Esse get() é o que possui a informação do pedido, pedidoClient foi injetado na depêndencia
//        pedidoClient.atualizarPagamentoDoPedido(pagamento.get().getPedidoId());
//    }


    private void copyDtoToEntity(PagamentoDTO dto, Pagamento entity) {
        entity.setValor(dto.getValor());
        entity.setNome(dto.getNome());
        entity.setNumeroDoCartao(dto.getNumeroDoCartao());
        entity.setValidade(dto.getValidade());
        entity.setCodigoDeSeguranca(dto.getCodigoDeSeguranca());
        entity.setStatus(dto.getStatus());
        entity.setPedidoId(dto.getPedidoId());
        entity.setFormaDePagamentoId(dto.getFormaDePagamentoId());
    }
}
