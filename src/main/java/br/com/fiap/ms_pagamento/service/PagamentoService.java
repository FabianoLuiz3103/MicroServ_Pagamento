package br.com.fiap.ms_pagamento.service;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.repository.PagamentoRepository;
import br.com.fiap.ms_pagamento.service.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

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

    @Transactional
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
