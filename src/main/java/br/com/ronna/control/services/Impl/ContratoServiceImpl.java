package br.com.ronna.control.services.Impl;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.ContratoModel;
import br.com.ronna.control.repositories.ContratoRepository;
import br.com.ronna.control.services.ContratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContratoServiceImpl implements ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Override
    public List<ContratoModel> findAll() {
        return contratoRepository.findAll();
    }

    @Override
    public Optional<ContratoModel> findById(UUID contratoId) {
        return contratoRepository.findById(contratoId);
    }

    @Override
    public void delete(ContratoModel contratoModel) {
        contratoRepository.delete(contratoModel);
    }

    @Override
    public void save(ContratoModel contratoModel) {
        contratoRepository.save(contratoModel);
    }

    @Override
    public Page<ContratoModel> findAll(Pageable pageable) {
        return contratoRepository.findAll(pageable);
    }

    @Override
    public Boolean existsContratoModelByCliente(ClienteModel cliente) {
        return contratoRepository.existsContratoModelByCliente(cliente);
    }

    @Override
    public boolean findByIdAndCliente(UUID contratoId, UUID clienteId) {
        return contratoRepository.findByContratoIdAndCliente(contratoId, clienteId);
    }

    @Override
    public Optional<ContratoModel> findContratoModelByCliente(ClienteModel clienteModel) {
        return contratoRepository.findContratoModelByCliente(clienteModel);
    }

}
