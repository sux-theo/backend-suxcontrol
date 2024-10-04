package br.com.ronna.control.controllers;

import br.com.ronna.control.dtos.PessoaFisicaDto;
import br.com.ronna.control.dtos.PessoaJuridicaDto;
import br.com.ronna.control.enums.ClienteStatus;
import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.EmpresaModel;
import br.com.ronna.control.models.PessoaFisicaModel;
import br.com.ronna.control.models.PessoaJuridicaModel;
import br.com.ronna.control.services.ClienteService;
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
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<Page<ClienteModel>> buscarTodosClientes(@PageableDefault(page = 0, size = 10,
            sort = "clienteNome", direction = Sort.Direction.ASC)Pageable pageable){
        log.debug("Listando todos clientes...");
        //List<PessoaFisicaModel> listaClientePF = pessoaFisicaService.findAll();

        Page<ClienteModel> clienteModelPage = clienteService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(clienteModelPage);
    }

}
