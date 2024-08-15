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

}
