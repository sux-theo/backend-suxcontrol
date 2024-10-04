package br.com.ronna.control.repositories;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.FechamentoModel;
import br.com.ronna.control.models.VisitaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FechamentoRepository extends JpaRepository<FechamentoModel, UUID>, JpaSpecificationExecutor<FechamentoModel> {

    Page<FechamentoModel> findByCliente(ClienteModel cliente, Pageable pageable);

    Page<FechamentoModel> findAll(Pageable pageable);

    @Query("SELECT f FROM FechamentoModel f LEFT JOIN FETCH f.visitas WHERE f.fechamentoId = :fechamentoId")
    Optional<FechamentoModel> findByIdWithVisitas(UUID fechamentoId);

    @Query(value = "select * from tb_fechamentos where cliente_id= :clienteId and fechamento_inicio >= :fechamentoInicio AND fechamento_final <= :fechamentoFinal", nativeQuery = true)
    Optional<FechamentoModel> findFechamentoModelByClienteIdEPeriodo(UUID clienteId, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal);

    @Query(value = "select * from tb_fechamentos where local_id= :localId and fechamento_inicio >= :fechamentoInicio AND fechamento_final <= :fechamentoFinal", nativeQuery = true)
    Optional<FechamentoModel> findFechamentoModelByLocalIdEPeriodo(UUID localId, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal);

    @Query(value = "select * from tb_fechamentos where cliente_id= :clienteId and (fechamento_inicio >= :fechamentoInicio AND fechamento_inicio <= :fechamentoFinal)", nativeQuery = true)
    Page<FechamentoModel> filtrarPorClienteInicioFim(UUID clienteId, LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal, Pageable pageable);

    @Query(value = "select * from tb_fechamentos where cliente_id= :clienteId and (fechamento_inicio = :fechamentoInicio)", nativeQuery = true)
    Page<FechamentoModel> filtrarPorClienteInicio(UUID clienteId, LocalDateTime fechamentoInicio, Pageable pageable);

    @Query(value = "select * from tb_fechamentos where (fechamento_inicio >= :fechamentoInicio AND fechamento_inicio <= :fechamentoFinal)", nativeQuery = true)
    Page<FechamentoModel> filtrarPorInicioFim(LocalDateTime fechamentoInicio, LocalDateTime fechamentoFinal, Pageable pageable);

    @Query(value = "select * from tb_fechamentos where (fechamento_inicio = :fechamentoInicio)", nativeQuery = true)
    Page<FechamentoModel> filtrarPorInicio(LocalDateTime fechamentoInicio, Pageable pageable);

    @Query(value = "select * from tb_fechamentos where (fechamento_final = :fechamentoFinal)", nativeQuery = true)
    Page<FechamentoModel> filtrarPorFim(LocalDateTime fechamentoFinal, Pageable pageable);

    @Query(value = "select * from tb_fechamentos where (cliente_id = :clienteId)", nativeQuery = true)
    Page<FechamentoModel> filtrarPorCliente(UUID clienteId, Pageable pageable);

    // @Query(value = "select * from tb_fechamentos_visitas where visitas_visita_id = :visitaId", nativeQuery = true)
    Optional<FechamentoModel> findFechamentoModelByVisitas(VisitaModel visitaModel);
}