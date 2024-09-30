package br.com.ronna.control.controllers;

import br.com.ronna.control.dtos.EmpresaDto;
import br.com.ronna.control.enums.EmpresaStatus;
import br.com.ronna.control.models.EmpresaModel;
import br.com.ronna.control.services.EmpresaService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/empresa")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<Page<EmpresaModel>> buscarTodasEmpresas(
            @PageableDefault(page = 0, size = 10, sort = "empresaNomeFantasia", direction = Sort.Direction.ASC)Pageable pageable){
        log.debug("Listando todas Empresas...");
        //List<EmpresaModel> listaEmpresas = empresaService.findAll();

        Page<EmpresaModel> empresaModelPage = empresaService.findAll(pageable);
        log.info("Empresa...");
        log.info(empresaModelPage.toString());
        return ResponseEntity.status(HttpStatus.OK).body(empresaModelPage);
    }

    @GetMapping("{empresaId}")
    public ResponseEntity<Object> buscaEmpresa(@PathVariable(value = "empresaId")UUID empresaId){
        log.debug("Buscando empresa com ID: {}", empresaId);
        Optional<EmpresaModel> empresaModelOptional = empresaService.findById(empresaId);

        if(!empresaModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empresa não encontrada!");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(empresaModelOptional.get());
        }
    }

    @PostMapping("/novo")
    public ResponseEntity<Object> criarEmpresa(@RequestBody EmpresaDto empresaDto) {
        log.debug("Criação de empresa...");

        if(empresaService.existsByEmpresaCNPJ(empresaDto.getEmpresaCNPJ())){
            log.warn("CNPJ {} já cadastrado no sistema", empresaDto.getEmpresaCNPJ());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: CNPJ já cadastrado");
        }
        if(empresaService.existsByEmpresaInscricaoEstadual(empresaDto.getEmpresaInscricaoEstadual())){
            log.warn("Inscrição Estadual {} já cadastrado no sistema", empresaDto.getEmpresaInscricaoEstadual());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Inscrição Estadual já cadastrado");
        }

        var empresaModel = new EmpresaModel();
        BeanUtils.copyProperties(empresaDto, empresaModel);

        empresaModel.setEmpresaStatus(EmpresaStatus.ATIVO);
        empresaModel.setDataCriacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        empresaModel.setDataAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        empresaService.save(empresaModel);
        log.debug("POST criarEmpresa empresaModel salvo {}", empresaModel.toString());
        log.info("Empresa criada com sucesso empresaId {}", empresaModel.getEmpresaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaModel);
    }

    @DeleteMapping("{empresaId}")
    public ResponseEntity<Object> deleteEmpresa(@PathVariable(value = "empresaId") UUID empresaId) {
        log.debug("Deletar empresa...");
        Optional<EmpresaModel> empresaModelOptional = empresaService.findById(empresaId);
        if(!empresaModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empresa não encontrada");
        } else {
            empresaModelOptional.get().setEmpresaStatus(EmpresaStatus.DESATIVO);
            empresaService.save(empresaModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("Empresa deletada com sucesso!");
        }
    }

    @PutMapping("{empresaId}")
    public ResponseEntity<Object> editarEmpresa(@PathVariable( value = "empresaId") UUID empresaId, @RequestBody EmpresaDto empresaDto) {
        log.debug("Edição de empresa...");
        Optional<EmpresaModel> empresaModelOptional = empresaService.findById(empresaId);
        if(!empresaModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empresa não encontrada");
        } else {
            var empresaModel = empresaModelOptional.get();
            empresaModel.setEmpresaCNPJ(empresaDto.getEmpresaCNPJ());
            empresaModel.setEmpresaInscricaoEstadual(empresaDto.getEmpresaInscricaoEstadual());
            empresaModel.setEmpresaNomeFantasia(empresaDto.getEmpresaNomeFantasia());
            empresaModel.setEmpresaEndereco(empresaDto.getEmpresaEndereco());
            empresaModel.setEmpresaEmail(empresaDto.getEmpresaEmail());
            empresaModel.setEmpresaTelefone(empresaDto.getEmpresaTelefone());
            empresaModel.setDataAtualizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
            empresaModel.setEmpresaStatus(EmpresaStatus.ATIVO);
            empresaService.save(empresaModel);
            log.info("\n'asdasd'");
            log.debug("PUT editarEmpresa empresaModel salvo {}", empresaModel.toString());
            log.info("Empresa editada com sucesso empresaId {}", empresaModel.getEmpresaId());
            return ResponseEntity.status(HttpStatus.OK).body(empresaModel);
        }
    }
}
