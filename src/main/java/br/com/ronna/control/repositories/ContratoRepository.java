package br.com.ronna.control.repositories;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.ContratoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContratoRepository extends JpaRepository<ContratoModel, UUID>, JpaSpecificationExecutor<ContratoModel> {


    @Query(value = "select tb_contratos.contrato_id, tb_contratos.contrato_descricao, tb_contratos.contrato_valor_remoto, tb_contratos.contrato_valor_visita, tb_contratos.cliente_cliente_id, " +
             " tb_ativos.ativo_id, tb_ativos.ativo_descricao, tb_ativos.ativo_valor_locacao from tb_contratos" +
            " inner join tb_ativos on tb_ativos.contrato_id = tb_contratos.contrato_id", nativeQuery = true)
    List<ContratoModel> findAllWithAtivos();

    @Query(value = "select * from tb_contratos where cliente_cliente_id = :clienteId", nativeQuery = true)
    Boolean ClienteHasContrato(@Param("clienteId") UUID clientId);

    Boolean existsContratoModelByCliente(@Param("clienteModel") ClienteModel clienteModel);

    @Query(value = "select case when exists (select 1 from tb_contratos where cliente_cliente_id = :clienteId and contrato_id = :contratoId) " +
                   "then 'true' else 'false' end", nativeQuery = true)
    boolean findByContratoIdAndCliente(@Param("contratoId") UUID contratoId, @Param("clienteId") UUID clienteId);

    Optional<ContratoModel> findContratoModelByCliente(ClienteModel cliente);
}
