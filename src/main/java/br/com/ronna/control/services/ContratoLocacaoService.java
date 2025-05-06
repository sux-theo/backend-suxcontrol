package br.com.ronna.control.services;

import br.com.ronna.control.models.ContratoLocacaoModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContratoLocacaoService {
    void save(ContratoLocacaoModel contrato);

    Optional<ContratoLocacaoModel> findById(UUID id);

    List<ContratoLocacaoModel> findAll();

    List<ContratoLocacaoModel> findAtivosLocados();
}
