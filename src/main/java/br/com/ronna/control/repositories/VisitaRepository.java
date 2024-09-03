package br.com.ronna.control.repositories;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.FuncionarioModel;
import br.com.ronna.control.models.LocalModel;
import br.com.ronna.control.models.VisitaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface VisitaRepository extends JpaRepository<VisitaModel, UUID>, JpaSpecificationExecutor<VisitaModel> {

    @Query(value = "select * from tb_visitas where local_id= :localId and visita_inicio >= :periodoInicio AND visita_final <= :periodoFinal", nativeQuery = true)
    Page<VisitaModel> listarVisitasPorClienteLocalEPeriodo(UUID localId, LocalDateTime periodoInicio, LocalDateTime periodoFinal, Pageable pageable);

    @Query(value = "select * from tb_visitas where local_id= :localId and visita_inicio >= :periodoInicio AND visita_final <= :periodoFinal", nativeQuery = true)
    Set<VisitaModel> listarVisitasPorClienteLocalEPeriodoFechamento(UUID localId, LocalDateTime periodoInicio, LocalDateTime periodoFinal);

    Page<VisitaModel> findVisitaModelByClienteAndVisitaInicioAfterAndVisitaFinalBefore(ClienteModel clienteModel, LocalDateTime periodoInicio, LocalDateTime periodoFinal, Pageable pageable);
    Set<VisitaModel> findVisitaModelByClienteAndVisitaInicioAfterAndVisitaFinalBefore(ClienteModel clienteModel, LocalDateTime periodoInicio, LocalDateTime periodoFinal);


    Page<VisitaModel> findVisitaModelsByFuncionariosContaining(FuncionarioModel funcionarioModel, Pageable pageable);

    Page<VisitaModel> findVisitaModelsByFuncionariosContainingAndVisitaFinalBeforeAndVisitaInicioAfter(FuncionarioModel funcionarioModel,
                                                                                                       LocalDateTime visitaFinal,LocalDateTime visitaInicio,
                                                                                                       Pageable pageable);

    @Query(value = "SELECT v.* FROM tb_visitas v " +
            "JOIN tb_visitas_funcionarios vf ON v.visita_id = vf.visita_model_visita_id " +
            "JOIN tb_funcionarios f ON vf.funcionarios_funcionario_id = f.funcionario_id " +
            "WHERE v.cliente_id = :clienteId " +
            "AND f.funcionario_id = :funcionarioId " +
            "AND v.visita_inicio BETWEEN :inicio AND :fim",
            nativeQuery = true)
    Page<VisitaModel> filtrarVisitaClienteFuncionarioEPeriodo(@Param("clienteId") UUID clienteId,
                                                              @Param("funcionarioId") UUID funcionarioId,
                                                              @Param("inicio") LocalDateTime inicio,
                                                              @Param("fim") LocalDateTime fim, Pageable pageable);

    @Query(value = "SELECT * FROM tb_visitas v " +
            "WHERE v.cliente_id = :clienteId " +
            "AND v.visita_inicio BETWEEN :inicio AND :fim",
            countQuery = "SELECT count(*) FROM tb_visitas v " +
                    "WHERE v.cliente_id = :clienteId " +
                    "AND v.visita_inicio BETWEEN :inicio AND :fim",
            nativeQuery = true)
    Page<VisitaModel> filtrarVisitaClienteEPeriodo(@Param("clienteId") UUID clienteId,
                                                   @Param("inicio") LocalDateTime inicio,
                                                   @Param("fim") LocalDateTime fim,
                                                   Pageable pageable);


    @Query(value = "SELECT * FROM tb_visitas WHERE visita_inicio BETWEEN :inicio AND :fim", nativeQuery = true)
    Page<VisitaModel> filtrarVisitaPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);



    @Query(value = "SELECT v.* FROM tb_visitas v " +
            "JOIN tb_visitas_funcionarios vf ON v.visita_id = vf.visita_model_visita_id " +
            "WHERE vf.funcionarios_funcionario_id = :funcionarioId " +
            "AND v.visita_inicio >= :visitaInicio " +
            "AND v.visita_final <= :visitaFinal",
            countQuery = "SELECT count(*) FROM tb_visitas v " +
                    "JOIN tb_visitas_funcionarios vf ON v.visita_id = vf.visita_model_visita_id " +
                    "WHERE vf.funcionarios_funcionario_id = :funcionarioId " +
                    "AND v.visita_inicio >= :visitaInicio " +
                    "AND v.visita_final <= :visitaFinal",
            nativeQuery = true)
    Page<VisitaModel> filtrarVisitaFuncionarioEPeriodo(UUID funcionarioId, LocalDateTime visitaInicio, LocalDateTime visitaFinal, Pageable pageable);
}
