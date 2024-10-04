package br.com.ronna.control.controllers;

import br.com.ronna.control.dtos.LocalDto;
import br.com.ronna.control.enums.LocalStatus;
import br.com.ronna.control.models.LocalModel;
import br.com.ronna.control.services.ClienteService;
import br.com.ronna.control.services.LocalService;
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
import java.util.UUID;

@RestController
@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/local")
public class LocalController {

    @Autowired
    LocalService localService;

    @Autowired
    ClienteService clienteService;

    @GetMapping("/{clienteId}")
    public ResponseEntity<Object> listaLocaisPorCliente(@PathVariable (value = "clienteId") UUID clienteId,
                                                        @PageableDefault(page = 0, size = 100,
                                                                sort = "localNome", direction = Sort.Direction.ASC)Pageable pageable) {
        var clienteModelOptional = clienteService.findById(clienteId);
        if(!clienteModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Cliente não encontrado!");
        }

        Page<LocalModel> localModelPage = localService.findAllByClienteClienteId(clienteId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(localModelPage);
    }

    @PostMapping("/{clienteId}")
    public ResponseEntity<Object> criarLocal(@PathVariable (value = "clienteId") UUID clienteId, @RequestBody LocalDto localDto) {
        var clienteModelOptional = clienteService.findById(clienteId);
        if(!clienteModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Cliente não encontrado!");
        }

        var localModel = new LocalModel();
        BeanUtils.copyProperties(localDto, localModel);

        localModel.setCliente(clienteModelOptional.get());
        localModel.setLocalStatus(LocalStatus.ATIVO);

        localModel.setCreatedDate(LocalDateTime.now(ZoneId.of("UTC")));
        localModel.setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));

        localService.save(localModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(localModel);
    }

    @DeleteMapping("/{clienteId}/{localId}")
    public ResponseEntity<Object> deleteLocal(@PathVariable(value = "clienteId") UUID clienteId, @PathVariable(value = "localId") UUID localId) {
        var clienteModelOptional = clienteService.findById(clienteId);
        if(!clienteModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Cliente não encontrado!");
        }
        var localModelOptional = localService.findById(localId);
        if(!localModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Local não encontrado!");
        }
        localModelOptional.get().setLocalStatus(LocalStatus.DESATIVO);
        localService.save(localModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Local deletado com sucesso!");
    }

    @RequestMapping(value = "/{clienteId}/{localId}", method = RequestMethod.PUT)
    public ResponseEntity<Object> editarLocal(@PathVariable (value = "clienteId") UUID clienteId, @PathVariable(value = "localId") UUID localId,
                                              @RequestBody LocalDto localDto) {

        log.error("clienteId: {}", clienteId);
        log.error("localId: {}", localId);

        var clienteModelOptional = clienteService.findById(clienteId);
        if(!clienteModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Cliente não encontrado!");
        }

        var localModelOptional = localService.findById(localId);
        if(!localModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Local não encontrado!");
        }

        if(!localModelOptional.get().getCliente().getClienteId().equals(clienteModelOptional.get().getClienteId())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Local não pertence ao cliente selecionado!");
        }

        BeanUtils.copyProperties(localDto, localModelOptional.get());
        localModelOptional.get().setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
        localModelOptional.get().setLocalStatus(LocalStatus.ATIVO);
        localService.save(localModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(localModelOptional.get());
    }

}