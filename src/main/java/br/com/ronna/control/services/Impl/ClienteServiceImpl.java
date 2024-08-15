package br.com.ronna.control.services.Impl;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.PessoaJuridicaModel;
import br.com.ronna.control.repositories.ClienteRepository;
import br.com.ronna.control.repositories.PessoaJuridicaRepository;
import br.com.ronna.control.services.ClienteService;
import br.com.ronna.control.services.PessoaJuridicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Page<ClienteModel> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public Optional<ClienteModel> findById(UUID clienteId) {
        return clienteRepository.findById(clienteId);
    }

    @Override
    public void delete(ClienteModel clienteModel) {

    }

    @Override
    public void save(ClienteModel clienteModel) {

    }

}
