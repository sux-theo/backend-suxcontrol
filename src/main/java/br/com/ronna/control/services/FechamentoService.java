package br.com.ronna.control.services;

import br.com.ronna.control.dtos.FiltroFechamentoDto;
import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.FechamentoModel;
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

    Set<FechamentoModel> filtrarPorClienteInicioFim(FiltroFechamentoDto filtroFechamentoDto);

    Set<FechamentoModel> filtrarPorClienteInicio(FiltroFechamentoDto filtroFechamentoDto);

    Set<FechamentoModel> filtrarPorClienteFim(FiltroFechamentoDto filtroFechamentoDto);

    Set<FechamentoModel> filtrarPorInicioFim(FiltroFechamentoDto filtroFechamentoDto);

    Set<FechamentoModel> filtrarPorCliente(FiltroFechamentoDto filtroFechamentoDto);

    Set<FechamentoModel> filtrarPorInicio(FiltroFechamentoDto filtroFechamentoDto);

    Set<FechamentoModel> filtrarPorFim(FiltroFechamentoDto filtroFechamentoDto);
}