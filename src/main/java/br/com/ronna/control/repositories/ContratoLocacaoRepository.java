package br.com.ronna.control.repositories;

import br.com.ronna.control.models.AtivoModel;
import br.com.ronna.control.models.ContratoLocacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ContratoLocacaoRepository extends JpaRepository<ContratoLocacaoModel, UUID>, JpaSpecificationExecutor<ContratoLocacaoModel> {

    @Query("SELECT DISTINCT c FROM ContratoLocacaoModel c " +
            "JOIN FETCH c.ativosLocados a " +
            "WHERE a.status = br.com.ronna.control.enums.LocacaoAtivoStatus.ALUGADO")
    List<ContratoLocacaoModel> findAllWithAtivosAlugados();
}
