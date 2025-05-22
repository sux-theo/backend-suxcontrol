package br.com.ronna.control.services;

import br.com.ronna.control.models.AtivoModel;
import br.com.ronna.control.models.EmpresaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtivoService {

    List<AtivoModel> findAll();

    Optional<AtivoModel> findById(UUID ativoId);

    void delete(AtivoModel ativoModel);

    void save(AtivoModel ativoModel);

    Page<AtivoModel> findAll(Pageable pageable);

    boolean ativoHasContrato(UUID ativoId);

    boolean ativoInContrato(UUID ativoId, UUID contratoId);

    List<AtivoModel> findAtivosNaoAlugados ();
}
