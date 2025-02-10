package br.com.ronna.control.controllers;

import br.com.ronna.control.dtos.*;
import br.com.ronna.control.enums.FechamentoStatus;
import br.com.ronna.control.models.*;
import br.com.ronna.control.services.*;
import br.com.ronna.control.utils.CalculoHoras;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.var;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/fechamento")
public class FechamentoController {

    @Autowired
    private FechamentoService fechamentoService;

    @Autowired
    private LocalService localService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VisitaService visitaService;

    @Autowired
    private ContratoService contratoService;


    //Controller com os métodos de CRUD para a entidade FechamentoModel

    @GetMapping
    public ResponseEntity<Page<FechamentoModel>> findAll(@PageableDefault(page = 0, size = 100,
            sort = "fechamento_inicio", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Listando todos os fechamentos...");
        log.info("Pageable: {}", pageable.toString());
        FiltroFechamentoDto filtroFechamentoDto = new FiltroFechamentoDto();
        filtroFechamentoDto.setInicio(LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(3).withMinute(0).withSecond(0).withNano(0));
        filtroFechamentoDto.setFim(LocalDateTime.now().withDayOfMonth(1).withHour(2).withMinute(59).withSecond(0).withNano(0));

        log.info("Filtro: {}", filtroFechamentoDto.toString());
        return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.filtrarPorInicioFim(filtroFechamentoDto, pageable));
    }

    @GetMapping("/{fechamentoId}")
    public ResponseEntity<Object> findByIdWithVisitas(@PathVariable UUID fechamentoId) {
        Optional<FechamentoModel> fechamentoModelOptional = fechamentoService.findByIdWithVisitas(fechamentoId);
        if (!fechamentoModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Fechamento não encontrado!");
        }
        FechamentoResponseDto fechamentoResponseDto = new FechamentoResponseDto();
        BeanUtils.copyProperties(fechamentoModelOptional.get(), fechamentoResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(fechamentoResponseDto);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Object> listarFechamentosCliente(@PathVariable UUID clienteId, @PageableDefault(page = 0, size = 100, sort = "fechamentoInicio",
            direction = Sort.Direction.ASC) Pageable pageable) {
        Optional<ClienteModel> clienteModelOptional = clienteService.findById(clienteId);
        if (!clienteModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Cliente selecionado não encontrado");
        }
        return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.findFechamentoModelsByCliente(clienteModelOptional.get(), pageable));
    }

    @GetMapping("/local/{clienteLocalId}")
    public ResponseEntity<Object> listarFechamentosClienteLocal(@PathVariable(value = "clienteLocalId")UUID clienteLocalId,
                                                                @PageableDefault(page = 0, size = 100,
                                                                        sort = "fechamentoInicio", direction = Sort.Direction.ASC)Pageable pageable) {
        var localModelOptional = localService.findById(clienteLocalId);
        if(!localModelOptional.isPresent()) {
            log.info("Local do cliente {} não encontrado!", clienteLocalId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Local do Cliente selecionado não encontrado!");
        }
        Page<FechamentoModel> fechamentoModelPage = fechamentoService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(fechamentoModelPage);
    }

    @GetMapping("/{clienteLocalId}/{fechamentoId}")
    public ResponseEntity<Object> buscarFechamento(@PathVariable(value = "clienteLocalId") UUID clienteLocalId,
                                                   @PathVariable(value = "fechamentoId") UUID fechamentoId) {
        var localModelOptional = localService.findById(clienteLocalId);
        if(!localModelOptional.isPresent()) {
            log.info("Local do cliente {} não encontrado!", clienteLocalId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Local do Cliente selecionado não encontrado!");
        }
        var fechamentoModelOptional = fechamentoService.findById(fechamentoId);
        if(!fechamentoModelOptional.isPresent()) {
            log.info("Fechamento {} não encontrado!", fechamentoId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Fechamento selecionado não encontrado!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(fechamentoModelOptional.get());
    }

    @PostMapping("/new")
    public ResponseEntity<Object> novoFechamento(@RequestBody FechamentoNovoDto fechamentoNovoDto) {
        log.debug(fechamentoNovoDto.toString());
        LocalDateTime fechamentoInicioUtc = fechamentoNovoDto.getFechamentoInicio().atZone(ZoneId.of("America/Sao_Paulo")).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime fechamentoFinalUtc = fechamentoNovoDto.getFechamentoFinal().atZone(ZoneId.of("America/Sao_Paulo")).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();


        // Verificar

        try {
            Set<ClienteModel> clientesFechamentosSeparados = new HashSet<>();
            Set<ClienteModel> clientesFechamentosJuntos = new HashSet<>();

            for (UUID clienteId : fechamentoNovoDto.getClientesSelecionados()) {
                Optional<ClienteModel> clienteModelOptinal = clienteService.findById(clienteId);
                if (!clienteModelOptinal.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: cliente selecionado não encontrado: " + clienteId);
                }
                if (clienteModelOptinal.get().isFechamentoSeparado()) {
                    clientesFechamentosSeparados.add(clienteModelOptinal.get());
                } else {
                    clientesFechamentosJuntos.add(clienteModelOptinal.get());
                }
            }

            //TODO: ajustar o fechamento por local
            //Criar Fechamento por Local
            for (ClienteModel clienteModel : clientesFechamentosSeparados) {
                Optional<ContratoModel> contratoModelOptional = contratoService.findContratoModelByCliente(clienteModel);
                if (!contratoModelOptional.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Sem contrato existente para o cliente: " + clienteModel.getClienteNome());
                }
                List<LocalModel> listaLocais = localService.findAllByClienteClienteId(clienteModel.getClienteId());
                for (LocalModel localModel : listaLocais) {
                    FechamentoModel fechamentoModel = new FechamentoModel();
                    fechamentoModel.setCliente(clienteModel);
                    fechamentoModel.setLocal(localModel);
                    fechamentoModel.setFechamentoInicio(fechamentoInicioUtc);
                    fechamentoModel.setFechamentoFinal(fechamentoFinalUtc);
                    fechamentoModel.setCreatedDate(LocalDateTime.now(ZoneId.of("UTC")));
                    fechamentoModel.setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
                    fechamentoModel.setFechamentoStatus(FechamentoStatus.CRIADO);
                    Set<VisitaModel> setVisitas = visitaService.setVisitasPorLocalEPeriodo(localModel.getLocalId(),
                            fechamentoNovoDto.getFechamentoInicio(), fechamentoNovoDto.getFechamentoFinal());
                    fechamentoModel.setVisitas(setVisitas);

                    // valor dos produtos e valor dos serviços
                    double totalHoras = 0.0;
                    double totalHorasRemoto = 0.0;
                    double totalProdutos = 0.0;

                    for (VisitaModel visitaModel : setVisitas) {
                        if (visitaModel.getVisitaTotalAbono() == null){
                            visitaModel.setVisitaTotalAbono(0.0);
                        }
                        if (visitaModel.getVisitaValorProdutos() == null){
                            visitaModel.setVisitaValorProdutos(0.0);
                        }
                        totalProdutos = totalProdutos + visitaModel.getVisitaValorProdutos();
                        if (visitaModel.isVisitaRemoto()) {
                            totalHorasRemoto = totalHorasRemoto + (visitaModel.getVisitaTotalHoras() - visitaModel.getVisitaTotalAbono());
                        } else {
                            totalHoras = totalHoras + (visitaModel.getVisitaTotalHoras() - visitaModel.getVisitaTotalAbono());
                        }
                    }
                    fechamentoModel.setFechamentoValorProdutos(totalProdutos);
                    fechamentoModel.setFechamentoValorServicos((totalHorasRemoto * contratoModelOptional.get().getContratoValorRemoto()) +
                            (totalHoras * contratoModelOptional.get().getContratoValorVisita()));

                    log.info("Local id: {}", localModel.getLocalId());
                    Optional<FechamentoModel> fechamentoModelOptionalExistente =
                            fechamentoService.findFechamentoModelByLocalIdEPeriodo(localModel.getLocalId(),
                                    fechamentoInicioUtc, fechamentoFinalUtc);
                    log.info("FechamentoModelOptionalExistente: {}", fechamentoModelOptionalExistente);
                    if (fechamentoModelOptionalExistente.isPresent()) {
                        log.info("FechamentoModelOptionalExistente: {}", fechamentoModelOptionalExistente.get());
                        fechamentoModel.setFechamentoId(fechamentoModelOptionalExistente.get().getFechamentoId());
                        fechamentoService.delete(fechamentoModelOptionalExistente.get());
                    }


                    fechamentoService.save(fechamentoModel);
                }
            }

            //Criar Fechamento por Cliente
            for (ClienteModel clienteModel : clientesFechamentosJuntos) {
                Optional<ContratoModel> contratoModelOptional = contratoService.findContratoModelByCliente(clienteModel);
                if (!contratoModelOptional.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Sem contrato existente para o cliente: " + clienteModel.getClienteNome());
                }

                FechamentoModel fechamentoModel = new FechamentoModel();
                fechamentoModel.setCliente(clienteModel);
                fechamentoModel.setFechamentoInicio(fechamentoInicioUtc);
                fechamentoModel.setFechamentoFinal(fechamentoFinalUtc);
                fechamentoModel.setCreatedDate(LocalDateTime.now(ZoneId.of("UTC")));
                fechamentoModel.setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
                fechamentoModel.setFechamentoStatus(FechamentoStatus.CRIADO);

                Set<VisitaModel> setVisitas = visitaService.listarVisitasPorClienteEPeriodoFechamento(clienteModel,
                        fechamentoNovoDto.getFechamentoInicio(), fechamentoNovoDto.getFechamentoFinal());
                fechamentoModel.setVisitas(setVisitas);

                log.info("FechamentoInicio (UTC): {}", fechamentoInicioUtc);
                log.info("FechamentoFinal (UTC): {}", fechamentoFinalUtc);


                // valor dos produtos e valor dos serviços
                double totalHoras = 0.0;
                double totalHorasRemoto = 0.0;
                double totalProdutos = 0.0;
                for (VisitaModel visitaModel : setVisitas) {
                    totalProdutos = totalProdutos + visitaModel.getVisitaValorProdutos();
                    if(visitaModel.getVisitaTotalAbono() == null){
                        visitaModel.setVisitaTotalAbono(0.0);
                    }
                    if(visitaModel.getVisitaValorProdutos() == null){
                        visitaModel.setVisitaValorProdutos(0.0);
                    }
                    if (visitaModel.isVisitaRemoto()) {
                        totalHorasRemoto = totalHorasRemoto + (visitaModel.getVisitaTotalHoras() - visitaModel.getVisitaTotalAbono());
                    } else {
                        totalHoras = totalHoras + (visitaModel.getVisitaTotalHoras() - visitaModel.getVisitaTotalAbono());
                    }
                }

                fechamentoModel.setFechamentoValorProdutos(totalProdutos);
                fechamentoModel.setFechamentoValorServicos((totalHorasRemoto * contratoModelOptional.get().getContratoValorRemoto()) +
                        (totalHoras * contratoModelOptional.get().getContratoValorVisita()));
                log.info("FechamentoInicio: {}", fechamentoModel.getFechamentoInicio());
                log.info("FechamentoFinal: {}", fechamentoModel.getFechamentoFinal());
                log.info("FechamentoClienteId: {}", clienteModel.getClienteId());
                Optional<FechamentoModel> fechamentoModelOptionalExistente =
                        fechamentoService.findFechamentoModelsByClienteIdAndPeriodo(clienteModel.getClienteId(),
                                fechamentoInicioUtc, fechamentoFinalUtc);
                log.info("FechamentoModelOptionalExistente: {}", fechamentoModelOptionalExistente);
                if (fechamentoModelOptionalExistente.isPresent()) {
                    log.info("FechamentoModelOptionalExistente: {}", fechamentoModelOptionalExistente.get());
                    fechamentoModel.setFechamentoId(fechamentoModelOptionalExistente.get().getFechamentoId());
                    fechamentoService.delete(fechamentoModelOptionalExistente.get());
                }
                fechamentoService.save(fechamentoModel);

                log.debug("Fechamento por cliente novo: {}", fechamentoModel.toString());

            }

            return ResponseEntity.status(HttpStatus.OK).body("Fechamentos gerados com sucesso! ");
        } catch (DataIntegrityViolationException e) {
            log.error("Erro: de integridade: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Não foi possível salvar o fechamento devido a uma violação de integridade de dados. " +
                    "Por favor, verifique se o fechamento já foi criado ou se há dados duplicados.");
        } catch (Exception e) {
            log.error("Erro ao criar fechamento: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: Ocorreu um erro ao criar o fechamento. " +
                    "Por favor, tente novamente.");
        }
    }

    @PostMapping("/filtro")
    public ResponseEntity<Object> filtrarFechamentos(@RequestBody FiltroFechamentoDto filtroFechamentoDto,
                                                     @PageableDefault(page = 0, size = 50,
                                                             sort = "fechamento_inicio", direction = Sort.Direction.ASC )Pageable pageable) {
        log.debug(filtroFechamentoDto);


        // Verifica quais atributos foram fornecidos
        boolean hasCliente = filtroFechamentoDto.getCliente() != null;
        boolean hasInicio = filtroFechamentoDto.getInicio() != null;
        boolean hasFim = filtroFechamentoDto.getFim() != null;

        // Lógica para redirecionar para o serviço correto
        if (hasCliente && hasInicio && hasFim) {
            // Chama o serviço para filtrar por cliente, início e fim
            return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.filtrarPorClienteInicioFim(filtroFechamentoDto, pageable));
        } else if (hasCliente && hasInicio) {
            // Chama o serviço para filtrar por cliente e início
            return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.filtrarPorClienteInicio(filtroFechamentoDto, pageable));
        } else if (hasCliente && hasFim) {
            // Chama o serviço para filtrar por cliente e fim
            return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.filtrarPorClienteFim(filtroFechamentoDto, pageable));
        } else if (hasInicio && hasFim) {
            // Chama o serviço para filtrar por início e fim
            return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.filtrarPorInicioFim(filtroFechamentoDto, pageable));
        } else if (hasCliente) {
            // Chama o serviço para filtrar apenas por cliente
            return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.filtrarPorCliente(filtroFechamentoDto, pageable));
        } else if (hasInicio) {
            // Chama o serviço para filtrar apenas por início
            return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.filtrarPorInicio(filtroFechamentoDto, pageable));
        } else if (hasFim) {
            // Chama o serviço para filtrar apenas por fim
            return ResponseEntity.status(HttpStatus.OK).body(fechamentoService.filtrarPorFim(filtroFechamentoDto, pageable));
        } else {
            // Caso nenhum filtro seja fornecido
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nenhum filtro fornecido.");
        }

    }

    @PostMapping("/novo")
    public ResponseEntity<Object> criarFechamento(@RequestBody FechamentoDto fechamentoDto) {
        log.info("Criando novo fechamento: {}", fechamentoDto);

        if (fechamentoDto.getCliente() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: cliente não selecionado!");
        }
        Optional<ClienteModel> clienteModelOptional = clienteService.findById(fechamentoDto.getCliente());
        if (!clienteModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: cliente selecionado não encontrado!");
        }
        //Dados do contrato.
        Optional<ContratoModel> contratoModelOptional = contratoService.findContratoModelByCliente(clienteModelOptional.get());
        if(!contratoModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Contrato não encontrado para criação desse Fechamento!");
        }
        double totalHoras = 0.0;
        double totalHorasRemoto = 0.0;
        double totalProdutos = 0.0;
        if (fechamentoDto.getClienteLocalId() != null && !fechamentoDto.getClienteLocalId().equals("")) {
            //Fechamento separado por local
            Optional<LocalModel> localModelOptional = localService.findById(fechamentoDto.getClienteLocalId());
            if(!localModelOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Local selecionado não encontrado!");
            }
            Set<VisitaModel> visitaModels = visitaService.setVisitasPorLocalEPeriodo(localModelOptional.get().getLocalId(),
                    fechamentoDto.getFechamentoInicio(), fechamentoDto.getFechamentoFinal());
            if(visitaModels.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Não existem visitas para este cliente no período selecionado!");
            }
            //Somar os valores de produtos totais e total de horas das visitas. salvar valor final pelo valor cadastrado em contrato.
            for(VisitaModel visitaModel : visitaModels){
                totalProdutos = totalProdutos + visitaModel.getVisitaValorProdutos();
                //valida acesso remoto para salvar o valor correto
                if(visitaModel.isVisitaRemoto()) {
                    totalHorasRemoto = totalHorasRemoto + visitaModel.getVisitaTotalHoras();
                } else {
                    totalHoras = totalHoras + visitaModel.getVisitaTotalHoras();
                }
            }
            var fechamentoModel = new FechamentoModel();
            fechamentoModel.setCliente(clienteModelOptional.get());
            fechamentoModel.setLocal(localModelOptional.get());
            fechamentoModel.setFechamentoInicio(fechamentoDto.getFechamentoInicio());
            fechamentoModel.setFechamentoFinal(fechamentoDto.getFechamentoFinal());
            fechamentoModel.setFechamentoValorProdutos(totalProdutos);
            fechamentoModel.setFechamentoValorServicos( (totalHorasRemoto * contratoModelOptional.get().getContratoValorRemoto()) +
                    ( totalHoras * contratoModelOptional.get().getContratoValorVisita() )  );
            fechamentoModel.setVisitas(visitaModels);
            fechamentoModel.setCreatedDate(LocalDateTime.now(ZoneId.of("UTC")));
            fechamentoModel.setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
            fechamentoModel.setFechamentoStatus(FechamentoStatus.CRIADO);
            fechamentoService.save(fechamentoModel);
            log.debug("Fechamento separado por local criado com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(fechamentoModel);
        } else {
            // Fechamento por cliente todos os locais juntos.
            Set<VisitaModel> visitaModels = visitaService.listarVisitasPorClienteEPeriodoFechamento(clienteModelOptional.get(),
                    fechamentoDto.getFechamentoInicio(), fechamentoDto.getFechamentoFinal());
            if(visitaModels.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Não existem visitas para este cliente no período selecionado!");
            }
            //Somar os valores de produtos totais e total de horas das visitas. salvar valor final pelo valor cadastrado em contrato.
            for(VisitaModel visitaModel : visitaModels){
                totalProdutos = totalProdutos + visitaModel.getVisitaValorProdutos();
                //valida acesso remoto para salvar o valor correto
                if(visitaModel.isVisitaRemoto()) {
                    totalHorasRemoto = totalHorasRemoto + visitaModel.getVisitaTotalHoras();
                } else {
                    totalHoras = totalHoras + visitaModel.getVisitaTotalHoras();
                }
            }
            var fechamentoModel = new FechamentoModel();
            fechamentoModel.setCliente(clienteModelOptional.get());
            fechamentoModel.setFechamentoInicio(fechamentoDto.getFechamentoInicio());
            fechamentoModel.setFechamentoFinal(fechamentoDto.getFechamentoFinal());
            fechamentoModel.setFechamentoValorProdutos(totalProdutos);
            fechamentoModel.setFechamentoValorServicos( (totalHorasRemoto * contratoModelOptional.get().getContratoValorRemoto()) +
                    ( totalHoras * contratoModelOptional.get().getContratoValorVisita() )  );
            fechamentoModel.setVisitas(visitaModels);
            fechamentoModel.setCreatedDate(LocalDateTime.now(ZoneId.of("UTC")));
            fechamentoModel.setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
            fechamentoModel.setFechamentoStatus(FechamentoStatus.CRIADO);
            fechamentoService.save(fechamentoModel);
            log.debug("Fechamento criado com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(fechamentoModel);
        }


    }

    @PutMapping("/{clienteLocalId}/editstatus/{fechamentoId}")
    public ResponseEntity<Object> editarStatusFechamento(@PathVariable(value = "clienteLocalId") UUID clienteLocalId,
                                                         @PathVariable(value = "fechamentoId") UUID fechamentoId,
                                                         @RequestBody FechamentoStatusDto fechamentoStatusDto) {
        var localModelOptional = localService.findById(clienteLocalId);
        if(!localModelOptional.isPresent()) {
            log.info("Local do cliente {} não encontrado!", clienteLocalId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Local do Cliente selecionado não encontrado!");
        }
        var fechamentoModelOptional = fechamentoService.findById(fechamentoId);
        if(!fechamentoModelOptional.isPresent()) {
            log.info("Fechamento {} não encontrado!", fechamentoId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Fechamento não encontrado!");
        }
        fechamentoModelOptional.get().setFechamentoStatus(fechamentoStatusDto.getFechamentoStatus());
        fechamentoModelOptional.get().setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
        fechamentoService.save(fechamentoModelOptional.get());
        log.info("Status do fechamento atualizado com sucesso para {}", fechamentoStatusDto.getFechamentoStatus());
        return ResponseEntity.status(HttpStatus.OK).body(fechamentoModelOptional.get());
    }

    @PutMapping("/editarStatus/{fechamentoId}")
    public ResponseEntity<Object> editarStatus (@PathVariable(value = "fechamentoId") UUID fechamentoId, @RequestBody FechamentoStatusDto fechamentoStatusDto) {
        var fechamentoModelOptional = fechamentoService.findById(fechamentoId);
        if(!fechamentoModelOptional.isPresent()) {
            log.info("Fechamento {} não encontrado!", fechamentoId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Fechamento não encontrado!");
        }
        fechamentoModelOptional.get().setFechamentoStatus(fechamentoStatusDto.getFechamentoStatus());
        fechamentoModelOptional.get().setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
        fechamentoService.save(fechamentoModelOptional.get());
        log.info("Status do fechamento atualizado com sucesso para {}", fechamentoStatusDto.getFechamentoStatus());
        return ResponseEntity.status(HttpStatus.OK).body(fechamentoModelOptional.get());
    }

    @PutMapping("/editar/{fechamentoId}")
    public ResponseEntity<Object> editarFechamento( @PathVariable(value = "fechamentoId") UUID fechamentoId, @RequestBody FechamentoDto fechamentoDto) {

        var fechamentoModelOptional = fechamentoService.findById(fechamentoId);
        if(!fechamentoModelOptional.isPresent()) {
            log.info("Fechamento {} não encontrado!", fechamentoId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Fechamento não encontrado!");
        }
        double valorRemoto = 0.0;
        double valorVisita = 0.0;
        double totalMinutosRemoto = 0.0;
        double totalMinutosVisita = 0.0;
        double valorProdutos = 0.0;
        double totalHoras = 0.0;
        double totalHorasRemoto = 0.0;
        double totalProdutos = 0.0;
        Set<VisitaModel> visitaModelSet = new HashSet<>();

        log.error("teste 00");
        log.error(fechamentoDto);
        log.error(fechamentoDto.getCliente());
        // Fechamento por Cliente
        if (fechamentoDto.getCliente() != null){
            Optional<ClienteModel> clienteModelOptional = clienteService.findById(fechamentoDto.getCliente());
            if(!clienteModelOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: cliente selecionado não encontrado!");
            }
            Optional<ContratoModel> contratoModelOptional = contratoService.findContratoModelByCliente(clienteModelOptional.get());
            if(!contratoModelOptional.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Cliente sem contrato cadastrado!");
            }
            valorRemoto = contratoModelOptional.get().getContratoValorRemoto();
            valorVisita = contratoModelOptional.get().getContratoValorVisita();
            Set<VisitaModel> visitaModels = visitaService.listarVisitasPorClienteEPeriodoFechamento(clienteModelOptional.get(), fechamentoDto.getFechamentoInicio(), fechamentoDto.getFechamentoFinal());
            // if(visitaModels.isEmpty()) {
            //     return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Não existem visitas para este cliente no período selecionado!");
            // }
            //Somar os valores de produtos totais e total de horas das visitas. salvar valor final pelo valor cadastrado em contrato.
            for(VisitaModel visitaModel : visitaModels){
                totalProdutos = totalProdutos + visitaModel.getVisitaValorProdutos();
                //valida acesso remoto para salvar o valor correto
                if(visitaModel.isVisitaRemoto()) {
                    totalHorasRemoto = totalHorasRemoto + visitaModel.getVisitaTotalHoras();
                } else {
                    log.debug("getVisitaTotalHoras: {} ", visitaModel.getVisitaTotalHoras());
                    totalHoras = totalHoras + visitaModel.getVisitaTotalHoras();
                    log.debug("TOTALHORAS: {} ", totalHoras);
                }
                log.error("condição 1");
                log.error(visitaModel);
                visitaModelSet.add(visitaModel);
            }
        } else {
            if (fechamentoDto.getClienteLocalId() != null) {
                Optional<LocalModel> localModelOptional = localService.findById(fechamentoDto.getClienteLocalId());
                if(!localModelOptional.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Sem local e sem cliente selecionado!");
                }
                valorRemoto = localModelOptional.get().getCliente().getContrato().getContratoValorRemoto();
                valorVisita = localModelOptional.get().getCliente().getContrato().getContratoValorVisita();
                Set<VisitaModel> visitaModels = visitaService.setVisitasPorLocalEPeriodo(localModelOptional.get().getLocalId(),
                        fechamentoDto.getFechamentoInicio(), fechamentoDto.getFechamentoFinal());
                if(visitaModels.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Não existem visitas para este cliente no período selecionado!");
                }
                //Somar os valores de produtos totais e total de horas das visitas. salvar valor final pelo valor cadastrado em contrato.
                for(VisitaModel visitaModel : visitaModels){
                    totalProdutos = totalProdutos + visitaModel.getVisitaValorProdutos();
                    //valida acesso remoto para salvar o valor correto
                    if(visitaModel.isVisitaRemoto()) {
                        totalHorasRemoto = totalHorasRemoto + visitaModel.getVisitaTotalHoras();
                    } else {
                        log.debug("getVisitaTotalHoras: {} ", visitaModel.getVisitaTotalHoras());
                        totalHoras = totalHoras + visitaModel.getVisitaTotalHoras();
                        log.debug("TOTALHORAS: {} ", totalHoras);
                    }
                    log.error("condição 2");
                    log.error(visitaModel);
                    visitaModelSet.add(visitaModel);
                }
            }

        }
        log.error(visitaModelSet);

        BeanUtils.copyProperties(fechamentoDto, fechamentoModelOptional.get());
        fechamentoModelOptional.get().setVisitas(visitaModelSet);
        fechamentoModelOptional.get().setFechamentoValorProdutos(totalProdutos);
        fechamentoModelOptional.get().setFechamentoValorServicos( (totalHorasRemoto * valorRemoto) + ( totalHoras * valorVisita )  );
        fechamentoModelOptional.get().setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
        fechamentoService.save(fechamentoModelOptional.get());
        // log.info("Fechamento atualizado com sucesso! {}", fechamentoModelOptional.get().getFechamentoId());
        return ResponseEntity.status(HttpStatus.OK).body(fechamentoModelOptional.get());
    }

    @DeleteMapping("/apagar/{fechamentoId}")
    public ResponseEntity<Object> apagarFechamento(@PathVariable(value = "fechamentoId") UUID fechamentoId){
        var fechamentoModelOptional = fechamentoService.findById(fechamentoId);
        if(!fechamentoModelOptional.isPresent()) {
            log.info("Fechamento {} não encontrado!", fechamentoId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Fechamento não encontrado!");
        }
        fechamentoService.delete(fechamentoModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Fechamento apagado com suceso!");
    }

}