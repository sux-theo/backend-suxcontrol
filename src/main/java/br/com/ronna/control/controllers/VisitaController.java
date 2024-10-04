package br.com.ronna.control.controllers;

import br.com.ronna.control.dtos.FiltroVisitaDto;
import br.com.ronna.control.dtos.PeriodoDto;
import br.com.ronna.control.dtos.VisitaDto;
import br.com.ronna.control.models.FechamentoModel;
import br.com.ronna.control.models.FuncionarioModel;
import br.com.ronna.control.models.VisitaModel;
import br.com.ronna.control.services.*;
import br.com.ronna.control.utils.CalculoHoras;
import lombok.extern.log4j.Log4j2;
import lombok.var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@Log4j2
@RequestMapping("/visita")
@CrossOrigin(value = "*", maxAge = 3600)
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private LocalService localService;

    @Autowired
    private FechamentoService fechamentoService;

    @GetMapping
    public ResponseEntity<Page<VisitaModel>> listaTodasVisitas(@PageableDefault(page = 0, size = 50, sort = "visitaInicio", direction = Sort.Direction.ASC)Pageable pageable) {
        log.debug("Listando todas as visitas...");

        Page<VisitaModel> visitaModelPage = visitaService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(visitaModelPage);
    }

    @GetMapping("/{visitaId}")
    public ResponseEntity<Object> buscaVisita(@PathVariable (value = "visitaId")UUID visitaId){
        var visitaModelOptional = visitaService.findById(visitaId);
        if(!visitaModelOptional.isPresent()) {
            log.warn("Erro: Visita com ID: {} não encontrada!", visitaId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Visita não encontrada!");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(visitaModelOptional.get());
        }
    }

    @PostMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<Object> buscaVisitaFuncionario(@PathVariable (value = "funcionarioId")UUID funcionarioId,
                                                         @PageableDefault(page = 0, size = 50, sort = "visitaInicio", direction = Sort.Direction.ASC)Pageable pageable
                                                         ,@RequestBody FiltroVisitaDto fitlroVisitaDto){
        var funcionarioModelOptional = funcionarioService.findById(funcionarioId);
        if(!funcionarioModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Funcionario selecionado não encontrado!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(visitaService.findByFuncionarioIdAndPeriodo(funcionarioModelOptional.get(), fitlroVisitaDto.getVisitaInicio(),
                fitlroVisitaDto.getVisitaFinal(),pageable));
        //return ResponseEntity.status(HttpStatus.OK).body(visitaService.findByFuncionarioId(funcionarioModelOptional.get(), pageable));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Object> listarVisitasPorClienteEPeriodo(@PathVariable (value = "clienteId") UUID clienteId, @RequestBody PeriodoDto periodoDto,
                                                                  @PageableDefault(page = 0, size = 50, sort = "visitaInicio", direction = Sort.Direction.ASC) Pageable pageable) {

        var clienteModelOptional = clienteService.findById(clienteId);
        if(!clienteModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Cliente selecionado não encontrado!");
        }

        Page<VisitaModel> visitaModelPage = visitaService.listarVisitasPorClienteEPeriodo(clienteModelOptional.get(), periodoDto.getPeriodoInicio(), periodoDto.getPeriodoFinal(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(visitaModelPage);
        //return ResponseEntity.status(HttpStatus.OK).body(visitaService.listarVisitasPorClienteEPeriodo(clienteId, periodoDto.getPeriodoInicio(), periodoDto.getPeriodoFinal()));
    }


    @PostMapping("/novo")
    public ResponseEntity<Object> criarVisita(@RequestBody VisitaDto visitaDto) {
        var visitaModel = new VisitaModel();
        log.debug("Criação de nova visita...");
        log.debug("Visita: {}", visitaDto);

        // Ajusta o Horário recebido do navegador para o GMT-3
        // visitaModel.setVisitaInicio(visitaDto.getVisitaInicio().minusHours(3));
        // visitaModel.setVisitaFinal(visitaDto.getVisitaFinal().minusHours(3));

        visitaModel.setVisitaInicio(visitaDto.getVisitaInicio());
        visitaModel.setVisitaFinal(visitaDto.getVisitaFinal());
        visitaModel.setVisitaRemoto(visitaDto.isVisitaRemoto());
        visitaModel.setVisitaTotalAbono(visitaDto.getVisitaTotalAbono());
        visitaModel.setVisitaValorProdutos(visitaDto.getVisitaValorProdutos());
        visitaModel.setVisitaDescricao(visitaDto.getVisitaDescricao());

        var clienteModelOptional = clienteService.findById(visitaDto.getCliente());
        if(!clienteModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Cliente não encontrado!");
        }
        visitaModel.setCliente(clienteModelOptional.get());
        log.debug(visitaDto.getLocal());
        if(visitaDto.getLocal() != null){
            log.debug("Visita com local diferente de nulo!");
            var localModelOptional = localService.findById(visitaDto.getLocal());
            visitaModel.setLocal(localModelOptional.get());
        } else {
            log.debug("Visita com local em nulo!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Cliente sem Local cadastrado!");
        }

        Set<FuncionarioModel> funcTemp = new HashSet<>();
        AtomicBoolean funcionarioTeste = new AtomicBoolean(false);
        visitaDto.getFuncionarios().forEach(v -> {
            var funcionarioModelOptional = funcionarioService.findById(v.getFuncionarioId());
            if(!funcionarioModelOptional.isPresent()){
                funcionarioTeste.set(true);
                return;
            }
            funcTemp.add(funcionarioModelOptional.get());
        });
        if(funcionarioTeste.get()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Funcionário não encontrado!");
        }
        visitaModel.setVisitaRemoto(visitaDto.isVisitaRemoto());
        visitaModel.setFuncionarios(funcTemp);
        visitaModel.setCreatedDate(LocalDateTime.now(ZoneId.of("UTC")));
        visitaModel.setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));

        CalculoHoras calculoHoras = new CalculoHoras();

        visitaModel.setVisitaTotalHoras(calculoHoras.diferencaInicioFim(visitaModel.getVisitaInicio(), visitaModel.getVisitaFinal()));

        visitaService.save(visitaModel);
        log.debug(visitaModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(visitaModel);
    }

    @PutMapping("/{visitaId}")
    public ResponseEntity<Object> editarVisita(@RequestBody VisitaDto visitaDto, @PathVariable(value = "visitaId") UUID visitaId) {
        var visitaModelOptional = visitaService.findById(visitaId);
        if(!visitaModelOptional.isPresent()) {
            log.debug("Erro: Visita com id {} não encontrada!", visitaId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Visita não encontrada!");
        }

        BeanUtils.copyProperties(visitaDto, visitaModelOptional.get());


        var clienteModel = clienteService.findById(visitaDto.getCliente());
        visitaModelOptional.get().setCliente(clienteModel.get());
        if(visitaDto.getLocal() != null){
            log.debug("Visita com local diferente de nulo!");
            var localModelOptional = localService.findById(visitaDto.getLocal());
            visitaModelOptional.get().setLocal(localModelOptional.get());
        } else {
            log.debug("Visita com local em nulo!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Cliente sem Local cadastrado!");
        }

        Set<FuncionarioModel> funcTemp = new HashSet<>();
        visitaDto.getFuncionarios().forEach(f -> {
            var funcionarioModelOptional = funcionarioService.findById(f.getFuncionarioId());
            if(funcionarioModelOptional.isPresent()){
                funcTemp.add(funcionarioModelOptional.get());
            }
        });
        visitaModelOptional.get().setFuncionarios(funcTemp);

        visitaModelOptional.get().setVisitaRemoto(visitaDto.isVisitaRemoto());

        visitaModelOptional.get().setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));

        CalculoHoras calculoHoras = new CalculoHoras();
        visitaModelOptional.get().setVisitaTotalHoras(calculoHoras.diferencaInicioFim(visitaModelOptional.get().getVisitaInicio(), visitaModelOptional.get().getVisitaFinal()));

        visitaService.save(visitaModelOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(visitaModelOptional.get());
    }

    @PostMapping("/filtro")
    public ResponseEntity<Object> getVisitasFiltradas(@RequestBody FiltroVisitaDto filtroVisitaDto,
                                                      @PageableDefault(page = 0, size = 50, sort = "visita_inicio", direction = Sort.Direction.ASC) Pageable pageable){

        boolean hasCliente = filtroVisitaDto.getCliente() != null;
        boolean hasFuncionario = filtroVisitaDto.getFuncionario() != null;
        boolean hasInicio = filtroVisitaDto.getVisitaInicio() != null;
        boolean hasFinal = filtroVisitaDto.getVisitaFinal() != null;

        log.debug("Teste recebimento....");
        log.debug(pageable);
        log.debug(filtroVisitaDto);

        if (hasCliente && hasFuncionario && hasInicio && hasFinal) {
            log.debug("Entrou filtro cliente, funcionario e periodo");
            return ResponseEntity.status(HttpStatus.OK).body(visitaService.filtrarVisitaClienteFuncionarioEPeriodo(filtroVisitaDto, pageable));
        }
        if(hasCliente && hasInicio && hasFinal){
            log.debug("Entrou filtro cliente e periodo");
            return ResponseEntity.status(HttpStatus.OK).body(visitaService.filtrarVisitaClienteEPeriodo(filtroVisitaDto, pageable));
        }
        if(hasFuncionario && hasInicio && hasFinal){
            log.debug("Entrou filtro cliente e periodo");
            return ResponseEntity.status(HttpStatus.OK).body(visitaService.filtrarVisitaFuncionarioEPeriodo(filtroVisitaDto, pageable));
        }
        if (hasInicio && hasFinal) {
            log.debug("Entrou filtro periodo");
            return ResponseEntity.status(HttpStatus.OK).body(visitaService.filtrarVisitaPeriodo(filtroVisitaDto, pageable));
        }


        return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro com o filtro, preencha mais valores e tente novamente.");
    }

    // TODO: Mapeamento de endpoint para o fechamento. (Verificar)
    @GetMapping("/clientelocal/{clienteLocalId}")
    public ResponseEntity<Object> getVisitasPorClienteLocalEPeriodo(@PathVariable (value = "clienteLocalId") UUID clienteLocalId, @RequestBody PeriodoDto periodoDto,
                                                                    @PageableDefault(page = 0, size = 100, sort = "visita_inicio", direction = Sort.Direction.ASC) Pageable pageable) {

        // Validação do UUID do local recebido como parametro.
        var clienteLocalModel = localService.findById(clienteLocalId);
        if(!clienteLocalModel.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Local do Cliente selecionado não encontrado!");
        }

        Page<VisitaModel> visitaModelPage = visitaService.listarVisitasPorClienteLocalEPeriodo(clienteLocalId, periodoDto.getPeriodoInicio(), periodoDto.getPeriodoFinal(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(visitaModelPage);
    }

    @DeleteMapping("/delete/{visitaId}")
    public ResponseEntity<Object> deleteVisita(@PathVariable (value = "visitaId") UUID visitaId) {
        log.debug("deletando visita " + visitaId);
        Optional<VisitaModel> visitaModelOptional = visitaService.findById(visitaId);
        if(!visitaModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: visita não encontrada!");
        }

        Optional<FechamentoModel> fechamentoModelOptional = fechamentoService.findFechamentoModelByVisita(visitaModelOptional.get());
        if(fechamentoModelOptional.isPresent()){
            fechamentoModelOptional.get().getVisitas().remove(visitaModelOptional.get());
            fechamentoService.save(fechamentoModelOptional.get());
            log.debug("deletando visita " + visitaId);
        }

        visitaService.delete(visitaModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Visita deletado com sucesso!");
    }

}