package br.com.ronna.control.services.Impl;

import br.com.ronna.control.enums.LocacaoAtivoStatus;
import br.com.ronna.control.models.AtivoModel;
import br.com.ronna.control.models.LocacaoAtivoModel;
import br.com.ronna.control.repositories.LocacaoAtivoRepository;
import br.com.ronna.control.services.LocacaoAtivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocacaoAtivoServiceImpl implements LocacaoAtivoService {
    @Autowired
    LocacaoAtivoRepository locacaoAtivoRepository;

    @Override
    public void save(LocacaoAtivoModel locacaoAtivoModel) {
        locacaoAtivoRepository.save(locacaoAtivoModel);
    }

    @Override
    public Optional<LocacaoAtivoModel> findByAtivo(AtivoModel ativoModel, LocacaoAtivoStatus status) {
        return locacaoAtivoRepository.findByAtivoAndStatus(ativoModel, status);
    }

    @Override
    public Optional<LocacaoAtivoModel> findById(UUID locacaoId) {
        return locacaoAtivoRepository.findById(locacaoId);
    }

}
