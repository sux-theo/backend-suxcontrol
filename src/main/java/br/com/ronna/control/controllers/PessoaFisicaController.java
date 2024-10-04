package br.com.ronna.control.controllers;

import br.com.ronna.control.dtos.PessoaFisicaDto;
import br.com.ronna.control.dtos.PessoaJuridicaDto;
import br.com.ronna.control.enums.ClienteStatus;
import br.com.ronna.control.models.EmpresaModel;
import br.com.ronna.control.models.PessoaFisicaModel;
import br.com.ronna.control.models.PessoaJuridicaModel;
import br.com.ronna.control.services.EmpresaService;
import br.com.ronna.control.services.PessoaFisicaService;
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
@RequestMapping("/cliente/pf")
public class PessoaFisicaController {

    @Autowired
    private PessoaFisicaService pessoaFisicaService;

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<Page<PessoaFisicaModel>> buscarTodasClientesPJ(@PageableDefault(page = 0, size = 10,
            sort = "clienteNome", direction = Sort.Direction.ASC)Pageable pageable){
        log.debug("Listando todos clientes PF...");
        //List<PessoaFisicaModel> listaClientePF = pessoaFisicaService.findAll();

        Page<PessoaFisicaModel> pessoaFisicaModelPage = pessoaFisicaService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(pessoaFisicaModelPage);
    }

    @GetMapping("empresa/{empresaId}")
    public ResponseEntity<List<PessoaFisicaModel>> buscarTodosClientesPFPorEmpresa(@PathVariable(value = "empresaId") UUID empresaId){
        log.debug("Listando todos clientes PF da empresa {}...", empresaId);
        List<PessoaFisicaModel> listaClientePF = pessoaFisicaService.findAllByEmpresaId(empresaId);

        return ResponseEntity.status(HttpStatus.OK).body(listaClientePF);
    }

    @GetMapping("{clienteId}")
    public ResponseEntity<Object> buscaCliente(@PathVariable(value = "clienteId")UUID clienteId){
        log.debug("Buscando cliente com ID: {}", clienteId);
        Optional<PessoaFisicaModel> pessoaFisicaModelOptional = pessoaFisicaService.findById(clienteId);

        if(!pessoaFisicaModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado!");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(pessoaFisicaModelOptional.get());
        }
    }

    @PostMapping("/novo")
    public ResponseEntity<Object> criarClientePJ(@RequestBody PessoaFisicaDto pessoaFisicaDto) {
        log.debug("Criação de cliente...");

        if(pessoaFisicaService.existsByClienteCPF(pessoaFisicaDto.getClienteCPF())){
            log.warn("CPF {} já cadastrado no sistema", pessoaFisicaDto.getClienteCPF());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: CPF já cadastrado");
        }

        var clientePFModel = new PessoaFisicaModel();
        BeanUtils.copyProperties(pessoaFisicaDto, clientePFModel);

        if(pessoaFisicaDto.getEmpresa() != null){
            var empresaModel = empresaService.findById( pessoaFisicaDto.getEmpresa() );
            if(empresaModel.isPresent()) {
                clientePFModel.setEmpresa(empresaModel.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Empresa informada não foi encontrada! empresaId: " +
                        pessoaFisicaDto.getEmpresa());
            }
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Cliente deve estar vinculado a uma empresa!");
        }


        clientePFModel.setClienteStatus(ClienteStatus.ATIVO);
        clientePFModel.setClienteDataCriacao(LocalDateTime.now(ZoneId.of("UTC")));
        clientePFModel.setClienteDataAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));
        pessoaFisicaService.save(clientePFModel);
        log.debug("POST criarCliente clientePFModel salvo {}", clientePFModel.toString());
        log.info("Cliente criada com sucesso clienteId {}", clientePFModel.getClienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePFModel);
    }

    @DeleteMapping("{clienteId}")
    public ResponseEntity<Object> deletarCliente(@PathVariable(value = "clienteId") UUID clienteId) {
        log.debug("Deletar cliente PF...");
        Optional<PessoaFisicaModel> pessoaFisicaModelOptional = pessoaFisicaService.findById(clienteId);
        if(!pessoaFisicaModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        pessoaFisicaModelOptional.get().setClienteStatus(ClienteStatus.DESATIVO);
        pessoaFisicaService.save(pessoaFisicaModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(pessoaFisicaModelOptional.get());
    }

    @PutMapping("{clienteId}")
    public ResponseEntity<Object> editarCliente(@PathVariable( value = "clienteId") UUID clienteId, @RequestBody PessoaFisicaDto pessoaFisicaDto) {
        log.debug("Edição de cliente...");
        Optional<PessoaFisicaModel> pessoaFisicaModelOptional = pessoaFisicaService.findById(clienteId);
        if(!pessoaFisicaModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        } else {
            var pessoaFisicaModel = pessoaFisicaModelOptional.get();

            BeanUtils.copyProperties(pessoaFisicaDto, pessoaFisicaModel);
            pessoaFisicaModel.setClienteDataAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));

            if(pessoaFisicaDto.getEmpresa() != null) {
                var empresaModelOptional = empresaService.findById(pessoaFisicaDto.getEmpresa());
                if(empresaModelOptional.isPresent()) {
                    pessoaFisicaModel.setEmpresa(empresaModelOptional.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Empresa informada não encontrada!");
                }
            } else {
                pessoaFisicaModel.setEmpresa(pessoaFisicaModel.getEmpresa());
            }
            pessoaFisicaModel.setClienteStatus(ClienteStatus.ATIVO);

            pessoaFisicaService.save(pessoaFisicaModel);

            log.debug("PUT editarCliente pessoaFisicaModel salvo {}", pessoaFisicaModel.toString());
            log.info("Cliente editada com sucesso clienteId {}", pessoaFisicaModel.getClienteId());


            return ResponseEntity.status(HttpStatus.OK).body(pessoaFisicaModel);
        }
    }
}
