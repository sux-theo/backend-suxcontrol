package br.com.ronna.control.services.Impl;

import br.com.ronna.control.dtos.FiltroFechamentoDto;
import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.FechamentoModel;
import br.com.ronna.control.repositories.FechamentoRepository;
import br.com.ronna.control.services.FechamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
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
    public Set<FechamentoModel> filtrarPorClienteInicioFim(FiltroFechamentoDto filtroFechamentoDto) {
        return fechamentoRepository.filtrarPorClienteInicioFim(filtroFechamentoDto.getCliente(), filtroFechamentoDto.getInicio(), filtroFechamentoDto.getFim());
    }

    @Override
    public Set<FechamentoModel> filtrarPorClienteInicio(FiltroFechamentoDto filtroFechamentoDto) {
        return fechamentoRepository.filtrarPorClienteInicio(filtroFechamentoDto.getCliente(), filtroFechamentoDto.getInicio());
    }

    @Override
    public Set<FechamentoModel> filtrarPorClienteFim(FiltroFechamentoDto filtroFechamentoDto) {
        //TODO: não implementado!
        return fechamentoRepository.filtrarPorClienteInicio(filtroFechamentoDto.getCliente(), filtroFechamentoDto.getInicio());
    }

    @Override
    public Set<FechamentoModel> filtrarPorInicioFim(FiltroFechamentoDto filtroFechamentoDto) {
        return fechamentoRepository.filtrarPorInicioFim(filtroFechamentoDto.getInicio(), filtroFechamentoDto.getFim());
    }

    @Override
    public Set<FechamentoModel> filtrarPorCliente(FiltroFechamentoDto filtroFechamentoDto) {
        return fechamentoRepository.filtrarPorCliente(filtroFechamentoDto.getCliente());
    }

    @Override
    public Set<FechamentoModel> filtrarPorInicio(FiltroFechamentoDto filtroFechamentoDto) {
        return fechamentoRepository.filtrarPorInicio(filtroFechamentoDto.getInicio());
    }

    @Override
    public Set<FechamentoModel> filtrarPorFim(FiltroFechamentoDto filtroFechamentoDto) {
        return fechamentoRepository.filtrarPorFim(filtroFechamentoDto.getFim());
    }
}