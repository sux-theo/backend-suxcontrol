package br.com.ronna.control.controllers;

import br.com.ronna.control.dtos.ContratoLocacaoDto;
import br.com.ronna.control.dtos.LocacaoAtivoDto;
import br.com.ronna.control.enums.ContratoLocacaoStatus;
import br.com.ronna.control.enums.LocacaoAtivoStatus;
import br.com.ronna.control.models.*;
import br.com.ronna.control.services.*;
import lombok.extern.log4j.Log4j2;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/contrato-locacao")
public class ContratoLocacaoController {

    @Autowired
    private ContratoLocacaoService contratoLocacaoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AtivoService ativoService;

    @Autowired
    private LocacaoAtivoService locacaoAtivoService;


    private boolean verificaAtivoAlugado(AtivoModel ativoModel) {
        Optional<LocacaoAtivoModel> locacaoAtivoModelOptional = locacaoAtivoService.findByAtivo(ativoModel, LocacaoAtivoStatus.ALUGADO);
        if (locacaoAtivoModelOptional.isPresent()) {
            LocacaoAtivoModel locacaoAtivoModel = locacaoAtivoModelOptional.get();
            return locacaoAtivoModel.getStatus() == LocacaoAtivoStatus.ALUGADO;
        }
        return false;
    }


    @PostMapping("/novo")
    public ResponseEntity<Object> create(@RequestBody ContratoLocacaoDto contratolocacaoDto) {
        Optional<ClienteModel> clienteModelOptional = clienteService.findById(contratolocacaoDto.getClienteId());
        if (!clienteModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado!");
        }
        var contrato = new ContratoLocacaoModel();
        contrato.setDescricao(contratolocacaoDto.getDescricao());
        contrato.setCliente(clienteModelOptional.get());
        contrato.setDataCriacao(LocalDateTime.now());
        contrato.setStatus(ContratoLocacaoStatus.ATIVO);

        //Criação de contrato de locação sem os ativos.
        contratoLocacaoService.save(contrato);

        // Após a criação de um contrato, cria a locação por ativos selecionados na
        //              lista recebida para podermos criar a lista de locações com o id do contrato
        List<LocacaoAtivoModel> locacaoAtivoModels = new ArrayList<>();
        contratolocacaoDto.getAtivosLocados().forEach(a -> {
            Optional<AtivoModel> ativoModelOptional = ativoService.findById(a.getAtivoId());
            if (!ativoModelOptional.isPresent()) {
                throw new RuntimeException("Ativo não encontrado!");
            }
            if(verificaAtivoAlugado(ativoModelOptional.get())) {
                throw new RuntimeException("Ativo não disponível para locação! - PAT: " + ativoModelOptional.get().getAtivoPatrimonio());
            }

            LocacaoAtivoModel locacaoAtivoModel = new LocacaoAtivoModel();
            locacaoAtivoModel.setAtivo(ativoModelOptional.get());
            locacaoAtivoModel.setContratoLocacao(contrato);
            locacaoAtivoModel.setDataEnvio(a.getDataEnvio());
            locacaoAtivoModel.setValorMensal(a.getValorMensal());
            locacaoAtivoModel.setObservacao(a.getObservacao());
            locacaoAtivoModel.setStatus(LocacaoAtivoStatus.ALUGADO);

            locacaoAtivoService.save(locacaoAtivoModel);
            locacaoAtivoModels.add(locacaoAtivoModel);
        });
        contrato.setAtivosLocados(locacaoAtivoModels);

       // Atualiza o contrato com a lista de locações
        contrato.setDataAtualizacao(LocalDateTime.now());
        contratoLocacaoService.save(contrato);

        return ResponseEntity.status(HttpStatus.CREATED).body(contrato);
    }

    @PutMapping("/adicionar/{contratoLocacaoId}")
    public ResponseEntity<Object> update(@PathVariable UUID contratoLocacaoId, @RequestBody ContratoLocacaoDto contratoLocacaoDto) {
        Optional<ContratoLocacaoModel> contratoLocacaoModelOptional = contratoLocacaoService.findById(contratoLocacaoId);
        if (!contratoLocacaoModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado!");
        }
        Optional<ClienteModel> clienteModelOptional = clienteService.findById(contratoLocacaoDto.getClienteId());
        if (!clienteModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado!");
        }

        var contrato = contratoLocacaoModelOptional.get();
        contrato.setDescricao(contratoLocacaoDto.getDescricao());
        contrato.setCliente(clienteModelOptional.get());

        //Verifica se ativo está alugado
        Set<LocacaoAtivoModel> ativosLocados = new HashSet<>();
        for (LocacaoAtivoDto locacaoAtivoDto : contratoLocacaoDto.getAtivosLocados()) {
            Optional<AtivoModel> ativoModelOptional = ativoService.findById(locacaoAtivoDto.getAtivoId());
            if (!ativoModelOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ativo não encontrado!");
            }
            if(verificaAtivoAlugado(ativoModelOptional.get())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ativo não disponível para locação! - PAT: " + ativoModelOptional.get().getAtivoPatrimonio());
            }
            // Cria a locação do ativo
            LocacaoAtivoModel newLocacaoAtivoModel = new LocacaoAtivoModel();
            newLocacaoAtivoModel.setAtivo(ativoModelOptional.get());
            newLocacaoAtivoModel.setContratoLocacao(contrato);
            newLocacaoAtivoModel.setDataEnvio(locacaoAtivoDto.getDataEnvio());
            newLocacaoAtivoModel.setValorMensal(locacaoAtivoDto.getValorMensal());
            newLocacaoAtivoModel.setObservacao(locacaoAtivoDto.getObservacao());
            newLocacaoAtivoModel.setStatus(LocacaoAtivoStatus.ALUGADO);
            locacaoAtivoService.save(newLocacaoAtivoModel);

            // Adiciona a locação à lista de ativos locados
            ativosLocados.add(newLocacaoAtivoModel);
        }

        // Atualiza o contrato com a lista de locações
        contrato.getAtivosLocados().addAll(ativosLocados);
        contrato.setDataAtualizacao(LocalDateTime.now());
        contratoLocacaoService.save(contrato);

        return ResponseEntity.status(HttpStatus.OK).body("Atualizado com sucesso!");
    }

    @PutMapping("/remover-ativos/{contratoLocacaoId}")
    public ResponseEntity<Object> removerAtivosDoContrato(
            @PathVariable UUID contratoLocacaoId,
            @RequestBody List<UUID> locacaoAtivoIds) {

        Optional<ContratoLocacaoModel> contratoOptional = contratoLocacaoService.findById(contratoLocacaoId);
        if (!contratoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado!");
        }

        ContratoLocacaoModel contrato = contratoOptional.get();
        List<String> mensagens = new ArrayList<>();

        for (UUID locacaoId : locacaoAtivoIds) {
            Optional<LocacaoAtivoModel> locacaoOptional = locacaoAtivoService.findById(locacaoId);
            if (!locacaoOptional.isPresent()) {
                mensagens.add("Locação " + locacaoId + " não encontrada.");
                continue;
            }

            LocacaoAtivoModel locacao = locacaoOptional.get();

            if (!locacao.getContratoLocacao().getContratoLocacaoId().equals(contratoLocacaoId)) {
                mensagens.add("Locação " + locacaoId + " não pertence ao contrato.");
                continue;
            }

            // Atualiza status e data de devolução
            locacao.setStatus(LocacaoAtivoStatus.DEVOLVIDO);
            locacao.setDataDevolucao(LocalDateTime.now());
            locacaoAtivoService.save(locacao);

            mensagens.add("Locação " + locacaoId + " devolvida com sucesso.");
        }

        contrato.setDataAtualizacao(LocalDateTime.now());
        contratoLocacaoService.save(contrato);

        return ResponseEntity.status(HttpStatus.OK).body(mensagens);
    }


    @GetMapping()
    public ResponseEntity<Object> findAll() {
        List<ContratoLocacaoModel> contratos = contratoLocacaoService.findAll();
        if (contratos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum contrato encontrado!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(contratos);
    }

    @GetMapping("/alugados")
    public ResponseEntity<Object> findAllContratosLocacaoAtiva() {
        // Busca todos os contratos de locação e retorna apenas os ativos que estão no cliente
        List<ContratoLocacaoModel> contratos = contratoLocacaoService.findAtivosLocados();
        if (contratos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum contrato encontrado!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(contratos);
    }

    @PutMapping("/desativar/{contratoLocacaoId}")
    public ResponseEntity<Object> desativarContrato(@PathVariable UUID contratoLocacaoId) {
        Optional<ContratoLocacaoModel> contratoOptional = contratoLocacaoService.findById(contratoLocacaoId);
        if (!contratoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato não encontrado!");
        }
        ContratoLocacaoModel contrato = contratoOptional.get();
        // Devolve todos os ativos locados
        for (LocacaoAtivoModel locacao : contrato.getAtivosLocados()) {
            locacao.setStatus(LocacaoAtivoStatus.DEVOLVIDO);
            locacao.setDataDevolucao(LocalDateTime.now());
            locacaoAtivoService.save(locacao);
        }

        // Atualiza o status do contrato
        contrato.setStatus(ContratoLocacaoStatus.DESATIVO);
        contrato.setDataAtualizacao(LocalDateTime.now());

        contratoLocacaoService.save(contrato);
        return ResponseEntity.status(HttpStatus.OK).body("Contrato desativado com sucesso!");
    }

}
