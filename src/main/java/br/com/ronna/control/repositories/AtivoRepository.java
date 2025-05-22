package br.com.ronna.control.repositories;

import br.com.ronna.control.models.AtivoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AtivoRepository extends JpaRepository<AtivoModel, UUID>, JpaSpecificationExecutor<AtivoModel> {
    @Query(value = "select case when exists (select 1 from tb_contratos_lista_ativos where lista_ativos_ativo_id = :ativoId) then 'true' else 'false' end", nativeQuery = true)
    Boolean ativoHasContrato(@Param("ativoId") UUID ativoId);

    @Query(value = "select case when exists (select 1 from tb_contratos_lista_ativos where lista_ativos_ativo_id = :ativoId and contrato_model_contrato_id = :contratoId) " +
                   "then 'true' else 'false' end", nativeQuery = true)
    Boolean ativoInContrato(@Param("ativoId") UUID ativoId, @Param("contratoId") UUID contratoId);


    @Query("SELECT a FROM AtivoModel a " +
            "WHERE a.ativoId NOT IN (" +
            "   SELECT la.ativo.ativoId " +
            "   FROM LocacaoAtivoModel la " +
            "   WHERE la.status = 'ALUGADO'" +
            ")")
    List<AtivoModel> findAtivosNaoAlugados();

}
