package br.com.ronna.control.repositories;

import br.com.ronna.control.models.FuncionarioModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface FuncionarioRepository extends JpaRepository<FuncionarioModel, UUID>, JpaSpecificationExecutor<FuncionarioModel> {

    boolean existsByFuncionarioCPF(String funcionarioCPF);

    @Query(value = "SELECT f FROM FuncionarioModel f WHERE f.funcionarioStatus = 'ATIVO'")
    Page<FuncionarioModel> findAllByFuncionarioStatus(Pageable pageable);
}
