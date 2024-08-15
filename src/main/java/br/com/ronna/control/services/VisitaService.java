package br.com.ronna.control.services;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.FuncionarioModel;
import br.com.ronna.control.models.LocalModel;
import br.com.ronna.control.models.VisitaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface VisitaService {

    Optional<VisitaModel> findById(UUID visitaId);

    void save(VisitaModel visitaModel);

    Page<VisitaModel> listarVisitasPorClienteLocalEPeriodo(UUID localId, LocalDateTime periodoInicio, LocalDateTime periodoFinal, Pageable pageable);

    Page<VisitaModel> listarVisitasPorClienteEPeriodo(ClienteModel clienteModel, LocalDateTime periodoInicio, LocalDateTime periodoFinal, Pageable pageable);

    Page<VisitaModel> findAll(Pageable pageable);

    Page<VisitaModel> findByFuncionarioId(FuncionarioModel funcionarioId, Pageable pageable);

    Page<VisitaModel> findByFuncionarioIdAndPeriodo(FuncionarioModel funcionarioModel, LocalDateTime visitaInicio, LocalDateTime visitaFinal,Pageable pageable);

    Set<VisitaModel> listarVisitasPorClienteEPeriodoFechamento(ClienteModel clienteModel, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal);

    Set<VisitaModel> setVisitasPorLocalEPeriodo(UUID localId, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal);
}
