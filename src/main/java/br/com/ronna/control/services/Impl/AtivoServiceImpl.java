package br.com.ronna.control.services.Impl;

import br.com.ronna.control.models.AtivoModel;
import br.com.ronna.control.repositories.AtivoRepository;
import br.com.ronna.control.services.AtivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtivoServiceImpl implements AtivoService {

    @Autowired
    AtivoRepository ativoRepository;


    @Override
    public List<AtivoModel> findAll() {
        return ativoRepository.findAll();
    }

    @Override
    public Optional<AtivoModel> findById(UUID ativoId) {
        return ativoRepository.findById(ativoId);
    }

    @Override
    public void delete(AtivoModel ativoModel) {
        ativoRepository.delete(ativoModel);
    }

    @Override
    public void save(AtivoModel ativoModel) {
        ativoRepository.save(ativoModel);
    }

    @Override
    public Page<AtivoModel> findAll(Pageable pageable) {
        return ativoRepository.findAll(pageable);
    }

    @Override
    public boolean ativoHasContrato(UUID ativoId) {
        return ativoRepository.ativoHasContrato(ativoId);
    }

    @Override
    public boolean ativoInContrato(UUID ativoId, UUID contratoId) {
        return ativoRepository.ativoInContrato(ativoId, contratoId);
    }

    @Override
    public List<AtivoModel> findAtivosNaoAlugados() {
        return ativoRepository.findAtivosNaoAlugados();
    }

}
