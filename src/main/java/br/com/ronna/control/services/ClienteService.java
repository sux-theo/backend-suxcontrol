package br.com.ronna.control.services;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.PessoaJuridicaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ClienteService {

    Page<ClienteModel> findAll(Pageable pageable);

    Optional<ClienteModel> findById(UUID clienteId);

    void delete(ClienteModel clienteModel);

    void save(ClienteModel clienteModel);

}
