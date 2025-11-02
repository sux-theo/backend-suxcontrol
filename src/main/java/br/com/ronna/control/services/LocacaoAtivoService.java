package br.com.ronna.control.services;

import br.com.ronna.control.enums.LocacaoAtivoStatus;
import br.com.ronna.control.models.AtivoModel;
import br.com.ronna.control.models.LocacaoAtivoModel;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface LocacaoAtivoService {
    void save(LocacaoAtivoModel locacaoAtivoModel);

    Optional<LocacaoAtivoModel> findByAtivo(AtivoModel ativoModel, LocacaoAtivoStatus status);

    Optional<LocacaoAtivoModel> findById(UUID locacaoId);
}
