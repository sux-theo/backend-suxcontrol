package br.com.ronna.control.services;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.ContratoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContratoService {

    List<ContratoModel> findAll();

    Optional<ContratoModel> findById(UUID contratoId);

    void delete(ContratoModel contratoModel);

    void save(ContratoModel contratoModel);

    Page<ContratoModel> findAll(Pageable pageable);

    Boolean existsContratoModelByCliente(ClienteModel clienteId);

    boolean findByIdAndCliente(UUID contratoId, UUID clienteId);

    Optional<ContratoModel> findContratoModelByCliente(ClienteModel cliente);
}
