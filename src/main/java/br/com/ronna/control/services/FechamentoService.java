package br.com.ronna.control.services;

import br.com.ronna.control.dtos.FiltroFechamentoDto;
import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.FechamentoModel;
import br.com.ronna.control.models.VisitaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FechamentoService {

    Page<FechamentoModel> findAll(Pageable pageable);

    Optional<FechamentoModel> findById(UUID fechamentoId);

    void save(FechamentoModel fechamentoModel);

    void delete(FechamentoModel fechamentoModel);

    Page<FechamentoModel> findFechamentoModelsByCliente(ClienteModel clienteId, Pageable pageable);

    Optional<FechamentoModel> findFechamentoModelsByClienteIdAndPeriodo(UUID cliente, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal);

    Optional<FechamentoModel> findFechamentoModelByLocalIdEPeriodo(UUID clienteLocalId, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal);

    Page<FechamentoModel> filtrarPorClienteInicioFim(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable);

    Page<FechamentoModel> filtrarPorClienteInicio(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable);

    Page<FechamentoModel> filtrarPorClienteFim(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable);

    Page<FechamentoModel> filtrarPorInicioFim(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable);

    Page<FechamentoModel> filtrarPorCliente(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable);

    Page<FechamentoModel> filtrarPorInicio(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable);

    Page<FechamentoModel> filtrarPorFim(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable);

    Optional<FechamentoModel> findFechamentoModelByVisita(VisitaModel visitaModel);

    Optional<FechamentoModel> findByIdWithVisitas(UUID fechamentoId);
}