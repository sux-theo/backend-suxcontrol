package br.com.ronna.control.services.Impl;

import br.com.ronna.control.dtos.ClienteVisitasDto;
import br.com.ronna.control.dtos.FiltroVisitaDto;
import br.com.ronna.control.models.*;
import br.com.ronna.control.repositories.ContratoRepository;
import br.com.ronna.control.repositories.VisitaRepository;
import br.com.ronna.control.services.VisitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class VisitaServiceImpl implements VisitaService {

    @Autowired
    VisitaRepository visitaRepository;
    @Autowired
    private ContratoRepository contratoRepository;

    @Override
    public Optional<VisitaModel> findById(UUID visitaId) {
        return visitaRepository.findById(visitaId);
    }

    @Override
    public void save(VisitaModel visitaModel) {

        visitaRepository.save(visitaModel);
    }

    @Override
    public Page<VisitaModel> listarVisitasPorClienteLocalEPeriodo(UUID localId, LocalDateTime periodoInicio, LocalDateTime periodoFinal, Pageable pageable) {
        return visitaRepository.listarVisitasPorClienteLocalEPeriodo(localId, periodoInicio, periodoFinal, pageable);
    }

    @Override
    public Page<VisitaModel> listarVisitasPorClienteEPeriodo(ClienteModel clienteModel, LocalDateTime periodoInicio, LocalDateTime periodoFinal, Pageable pageable) {
        return visitaRepository.findVisitaModelByClienteAndVisitaInicioAfterAndVisitaFinalBefore(clienteModel, periodoInicio, periodoFinal, pageable);
    }
    @Override
    public Set<VisitaModel> listarVisitasPorClienteEPeriodoFechamento(ClienteModel clienteModel, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal) {
        return visitaRepository.findVisitaModelByClienteAndVisitaInicioAfterAndVisitaFinalBefore(clienteModel, fechamentoInicio, fechamentoFinal);
    }

    @Override
    public Set<VisitaModel> setVisitasPorLocalEPeriodo(UUID localId, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal) {
        return visitaRepository.listarVisitasPorClienteLocalEPeriodoFechamento(localId, fechamentoInicio, fechamentoFinal);
    }

    @Override
    public Page<VisitaModel> filtrarVisitaClienteFuncionarioEPeriodo(FiltroVisitaDto filtroVisitaDto, Pageable pageable) {
        return visitaRepository.filtrarVisitaClienteFuncionarioEPeriodo(filtroVisitaDto.getCliente(), filtroVisitaDto.getFuncionario(),
                filtroVisitaDto.getVisitaInicio(), filtroVisitaDto.getVisitaFinal(), pageable);
    }

    @Override
    public Page<VisitaModel> filtrarVisitaClienteEPeriodo(FiltroVisitaDto filtroVisitaDto, Pageable pageable) {
        return visitaRepository.filtrarVisitaClienteEPeriodo(filtroVisitaDto.getCliente(), filtroVisitaDto.getVisitaInicio(), filtroVisitaDto.getVisitaFinal(), pageable);
    }

    @Override
    public Page<VisitaModel> filtrarVisitaPeriodo(FiltroVisitaDto filtroVisitaDto, Pageable pageable) {
        return visitaRepository.filtrarVisitaPeriodo(filtroVisitaDto.getVisitaInicio(), filtroVisitaDto.getVisitaFinal(), pageable);
    }

    @Override
    public Page<VisitaModel> filtrarVisitaFuncionarioEPeriodo(FiltroVisitaDto filtroVisitaDto, Pageable pageable) {
        return visitaRepository.filtrarVisitaFuncionarioEPeriodo(filtroVisitaDto.getFuncionario(), filtroVisitaDto.getVisitaInicio(), filtroVisitaDto.getVisitaFinal(), pageable);
    }

    @Override
    public void delete(VisitaModel visitaModel) {
        visitaRepository.delete(visitaModel);
    }

    @Override
    public Set<VisitaModel> listarVisitasPorFechamento(FechamentoModel fechamentoModel) {
        return visitaRepository.findVisitaModelByFechamento(fechamentoModel);
    }

    @Override
    public Long analiseVisitas(LocalDateTime periodoInicio, LocalDateTime periodoFinal) {
        return visitaRepository.countTotalVisitas(periodoInicio, periodoFinal);
    }

    @Override
    public Set<ClienteVisitasDto> contarVisitasPorCliente(LocalDateTime periodoInicio, LocalDateTime periodoFinal) {
        return visitaRepository.contarVisitasPorCliente(periodoInicio, periodoFinal);
    }

    @Override
    public Double visitasComValor(Pageable pageable) {
        LocalDateTime periodoInicio = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime periodoFinal = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        Page<VisitaModel> visitasPage = visitaRepository.filtrarVisitaPeriodo(periodoInicio, periodoFinal, pageable);

        AtomicReference<Double> valorTotal = new AtomicReference<>(0.0);
        contratoRepository.findAll().forEach(contratoModel -> {
            visitasPage.forEach(visitaModel -> {
                if (visitaModel.getCliente().getClienteId().equals(contratoModel.getCliente().getClienteId())) {
                    if (visitaModel.isVisitaRemoto()) {
                        valorTotal.updateAndGet(v -> (v + contratoModel.getContratoValorRemoto() * visitaModel.getVisitaTotalHoras()) + visitaModel.getVisitaValorProdutos());
                    } else {
                        valorTotal.updateAndGet(v -> (v + contratoModel.getContratoValorVisita() * visitaModel.getVisitaTotalHoras()) + visitaModel.getVisitaValorProdutos());
                    }
                }
            });
        });
        System.out.println("Valor Total: " + valorTotal.get());

        return valorTotal.get();
    }

    @Override
    public Page<VisitaModel> findAll(Pageable pageable) {
        return visitaRepository.findAll(pageable);
    }

    @Override
    public Page<VisitaModel> findByFuncionarioId(FuncionarioModel funcionarioId, Pageable pageable) {
        return visitaRepository.findVisitaModelsByFuncionariosContaining(funcionarioId, pageable);
    }

    @Override
    public Page<VisitaModel> findByFuncionarioIdAndPeriodo(FuncionarioModel funcionarioModel, LocalDateTime visitaInicio, LocalDateTime visitaFinal, Pageable pageable) {
        return visitaRepository.findVisitaModelsByFuncionariosContainingAndVisitaFinalBeforeAndVisitaInicioAfter(funcionarioModel, visitaFinal,visitaInicio, pageable);
    }

}
