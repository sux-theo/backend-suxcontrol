package br.com.ronna.control.services.Impl;

import br.com.ronna.control.enums.ContratoLocacaoStatus;
import br.com.ronna.control.enums.LocacaoAtivoStatus;
import br.com.ronna.control.models.ContratoLocacaoModel;
import br.com.ronna.control.models.LocacaoAtivoModel;
import br.com.ronna.control.repositories.ContratoLocacaoRepository;
import br.com.ronna.control.services.ContratoLocacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContratoLocacaoServiceImpl implements ContratoLocacaoService {

    @Autowired
    private ContratoLocacaoRepository contratoLocacaoRepository;

    @Override
    public void save(ContratoLocacaoModel contrato) {
        contratoLocacaoRepository.save(contrato);
    }

    @Override
    public Optional<ContratoLocacaoModel> findById(UUID id) {
        return contratoLocacaoRepository.findById(id);
    }

    @Override
    public List<ContratoLocacaoModel> findAll() {
        return contratoLocacaoRepository.findAll();
    }

    @Override
    public List<ContratoLocacaoModel> findAtivosLocados() {
        return contratoLocacaoRepository.findAllWithAtivosAlugados();
    }
}
