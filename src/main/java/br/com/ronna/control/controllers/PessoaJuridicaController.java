package br.com.ronna.control.controllers;

import br.com.ronna.control.dtos.PessoaJuridicaDto;
import br.com.ronna.control.enums.ClienteStatus;
import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.PessoaJuridicaModel;
import br.com.ronna.control.services.EmpresaService;
import br.com.ronna.control.services.PessoaJuridicaService;
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
@RequestMapping("/cliente/pj")
public class PessoaJuridicaController {

    @Autowired
    private PessoaJuridicaService pessoaJuridicaService;

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<Page<PessoaJuridicaModel>> buscarTodasClientesPJ(@PageableDefault(page = 0, size = 10, sort = "clienteNome",
            direction = Sort.Direction.ASC)Pageable pageable){
        log.debug("Listando todos clientes PJ...");

        Page<PessoaJuridicaModel> pessoaJuridicaModelPage = pessoaJuridicaService.findAll(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(pessoaJuridicaModelPage);
    }

    @GetMapping("empresa/{empresaId}")
    public ResponseEntity<Object> buscarTodosClientesPJPorEmpresa(@PathVariable(value = "empresaId") UUID empresaId,
                                                                                     @PageableDefault(page = 0, size = 10,
                                                                                             sort = "clienteNome",
                                                                                             direction = Sort.Direction.ASC) Pageable pageable){
        log.debug("Listando todos clientes PJ da empresa {}...", empresaId);
        //List<PessoaJuridicaModel> listaClientePJ = pessoaJuridicaService.findAllByEmpresaId(empresaId);

        var empresaModelOptional = empresaService.findById(empresaId);
        if(!empresaModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Empresa selecionada não encontrada!");
        }

        Page<PessoaJuridicaModel> pessoaJuridicaModelPage = pessoaJuridicaService.findAllByEmpresaId(empresaModelOptional.get(), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(pessoaJuridicaModelPage);
    }

    @GetMapping("{clienteId}")
    public ResponseEntity<Object> buscaEmpresa(@PathVariable(value = "clienteId")UUID clienteId){
        log.debug("Buscando cliente com ID: {}", clienteId);
        Optional<PessoaJuridicaModel> empresaModelOptional = pessoaJuridicaService.findById(clienteId);

        if(!empresaModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado!");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(empresaModelOptional.get());
        }
    }

    @PostMapping("/novo")
    public ResponseEntity<Object> criarClientePJ(@RequestBody PessoaJuridicaDto pessoaJuridicaDto) {
        log.debug("Criação de cliente...");

        if(pessoaJuridicaService.existsByClienteCNPJ(pessoaJuridicaDto.getClienteCNPJ())){
            log.warn("CNPJ {} já cadastrado no sistema", pessoaJuridicaDto.getClienteCNPJ());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: CNPJ já cadastrado");
        }
        if(pessoaJuridicaService.existsByClienteInscricaoEstadual(pessoaJuridicaDto.getClienteInscricaoEstadual())){
            log.warn("Inscrição Estadual {} já cadastrada no sistema", pessoaJuridicaDto.getClienteInscricaoEstadual());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Inscrição Estadual já cadastrada");
        }

        var clientePJModel = new PessoaJuridicaModel();
        BeanUtils.copyProperties(pessoaJuridicaDto, clientePJModel);

        if(pessoaJuridicaDto.getEmpresa() != null) {
            var empresaModel =  empresaService.findById( pessoaJuridicaDto.getEmpresa() );
            if(empresaModel.isPresent()) {
                clientePJModel.setEmpresa(empresaModel.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Empresa informada não foi encontrada! empresaId: " +
                        pessoaJuridicaDto.getEmpresa());
            }
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Cliente deve estar vinculado a uma empresa!");
        }


        clientePJModel.setFechamentoSeparado(Boolean.valueOf(pessoaJuridicaDto.getIsFechamentoSeparado()));
        clientePJModel.setClienteStatus(ClienteStatus.ATIVO);
        clientePJModel.setClienteDataCriacao(LocalDateTime.now(ZoneId.of("UTC")));
        clientePJModel.setClienteDataAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));
        pessoaJuridicaService.save(clientePJModel);
        log.debug("POST criarCliente clientePJModel salvo {}", clientePJModel.toString());
        log.info("Cliente criada com sucesso clienteId {}", clientePJModel.getClienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePJModel);
    }

    @DeleteMapping("{clienteId}")
    public ResponseEntity<Object> deleteCliente(@PathVariable(value = "clienteId") UUID clienteId) {
        log.debug("Deletar Cliente PJ");
        Optional<PessoaJuridicaModel> pessoaJuridicaModelOptional = pessoaJuridicaService.findById(clienteId);
        if(!pessoaJuridicaModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        pessoaJuridicaModelOptional.get().setClienteStatus(ClienteStatus.DESATIVO);
        pessoaJuridicaService.save(pessoaJuridicaModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(pessoaJuridicaModelOptional.get());
    }

    @PutMapping("{clienteId}")
    public ResponseEntity<Object> editarEmpresa(@PathVariable( value = "clienteId") UUID clienteId, @RequestBody PessoaJuridicaDto pessoaJuridicaDto) {
        log.debug("Edição de cliente...");
        Optional<PessoaJuridicaModel> pessoaJuridicaModelOptional = pessoaJuridicaService.findById(clienteId);
        if(!pessoaJuridicaModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        } else {
            var pessoaJuridicaModel = pessoaJuridicaModelOptional.get();
            BeanUtils.copyProperties(pessoaJuridicaDto, pessoaJuridicaModel);
            pessoaJuridicaModel.setClienteDataAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));

            if(pessoaJuridicaDto.getEmpresa() != null) {
                var empresaModelOptional = empresaService.findById(pessoaJuridicaDto.getEmpresa());
                if(empresaModelOptional.isPresent()) {
                    pessoaJuridicaModel.setEmpresa(empresaModelOptional.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Empresa informada não encontrada!");
                }
            } else {
                pessoaJuridicaModel.setEmpresa(pessoaJuridicaModel.getEmpresa());
            }

            pessoaJuridicaModel.setFechamentoSeparado(Boolean.valueOf(pessoaJuridicaDto.getIsFechamentoSeparado()));
            pessoaJuridicaModel.setClienteStatus(ClienteStatus.ATIVO);
            pessoaJuridicaService.save(pessoaJuridicaModel);

            log.debug("PUT editarCliente pessoaJuridicaModel salvo {}", pessoaJuridicaModel.toString());
            log.info("Cliente editada com sucesso clienteId {}", pessoaJuridicaModel.getClienteId());


            return ResponseEntity.status(HttpStatus.OK).body(pessoaJuridicaModel);
        }
    }
}
