package br.com.ronna.control.services.Impl;

import br.com.ronna.control.dtos.FiltroFechamentoDto;
import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.FechamentoModel;
import br.com.ronna.control.models.VisitaModel;
import br.com.ronna.control.repositories.FechamentoRepository;
import br.com.ronna.control.services.FechamentoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Log4j2
public class FechamentoServiceImpl implements FechamentoService {
    
    @Autowired
    FechamentoRepository fechamentoRepository;

    @Override
    public Page<FechamentoModel> findAll(Pageable pageable) {
        return fechamentoRepository.findAll(pageable);
    }

    @Override
    public Optional<FechamentoModel> findById(UUID fechamentoId) {
        return fechamentoRepository.findById(fechamentoId);
    }

    @Override
    public void save(FechamentoModel fechamentoModel) {
        fechamentoRepository.save(fechamentoModel);
    }

    @Override
    public void delete(FechamentoModel fechamentoModel) {
        fechamentoRepository.delete(fechamentoModel);
    }

    @Override
    public Page<FechamentoModel> findFechamentoModelsByCliente(ClienteModel cliente, Pageable pageable) {
        return fechamentoRepository.findByCliente(cliente, pageable);
    }

    @Override
    public Optional<FechamentoModel> findFechamentoModelsByClienteIdAndPeriodo(UUID cliente, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal) {
        return fechamentoRepository.findFechamentoModelByClienteIdEPeriodo(cliente, fechamentoInicio, fechamentoFinal);
    }

    @Override
    public Optional<FechamentoModel> findFechamentoModelByLocalIdEPeriodo(UUID clienteLocalId, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal) {
        return fechamentoRepository.findFechamentoModelByLocalIdEPeriodo(clienteLocalId, fechamentoInicio, fechamentoFinal);
    }

    @Override
    public Page<FechamentoModel> filtrarPorClienteInicioFim(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable) {
        return fechamentoRepository.filtrarPorClienteInicioFim(filtroFechamentoDto.getCliente(), filtroFechamentoDto.getInicio(), filtroFechamentoDto.getFim(), pageable);
    }

    @Override
    public Page<FechamentoModel> filtrarPorClienteInicio(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable) {
        return fechamentoRepository.filtrarPorClienteInicio(filtroFechamentoDto.getCliente(), filtroFechamentoDto.getInicio(), pageable);
    }

    @Override
    public Page<FechamentoModel> filtrarPorClienteFim(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable) {
        //TODO: não implementado!
        return fechamentoRepository.filtrarPorClienteInicio(filtroFechamentoDto.getCliente(), filtroFechamentoDto.getInicio(), pageable);
    }

    @Override
    public Page<FechamentoModel> filtrarPorInicioFim(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable) {
        return fechamentoRepository.filtrarPorInicioFim(filtroFechamentoDto.getInicio(), filtroFechamentoDto.getFim(), pageable);
    }

    @Override
    public Page<FechamentoModel> filtrarPorCliente(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable) {
        return fechamentoRepository.filtrarPorCliente(filtroFechamentoDto.getCliente(), pageable);
    }

    @Override
    public Page<FechamentoModel> filtrarPorInicio(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable) {
        return fechamentoRepository.filtrarPorInicio(filtroFechamentoDto.getInicio(), pageable);
    }

    @Override
    public Page<FechamentoModel> filtrarPorFim(FiltroFechamentoDto filtroFechamentoDto, Pageable pageable) {
        return fechamentoRepository.filtrarPorFim(filtroFechamentoDto.getFim(), pageable);
    }

    @Override
    public Optional<FechamentoModel> findFechamentoModelByVisita(VisitaModel visitaModel) {
        return fechamentoRepository.findFechamentoModelByVisitas(visitaModel);
    }

    @Override
    public Optional<FechamentoModel> findByIdWithVisitas(UUID fechamentoId) {
        return fechamentoRepository.findByIdWithVisitas(fechamentoId);
    }
}