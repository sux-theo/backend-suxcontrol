package br.com.ronna.control.repositories;

import br.com.ronna.control.enums.LocacaoAtivoStatus;
import br.com.ronna.control.models.AtivoModel;
import br.com.ronna.control.models.LocacaoAtivoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocacaoAtivoRepository extends JpaRepository<LocacaoAtivoModel, UUID> {
    /**
     * Find a LocacaoAtivoModel by its AtivoModel.
     *
     * @param ativoModel the AtivoModel to search for
     * @return the LocacaoAtivoModel associated with the given AtivoModel
     */
    Optional<LocacaoAtivoModel> findByAtivoAndStatus(AtivoModel ativoModel, LocacaoAtivoStatus status);
}
