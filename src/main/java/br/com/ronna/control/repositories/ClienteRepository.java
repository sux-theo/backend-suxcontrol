package br.com.ronna.control.repositories;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.PessoaJuridicaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<ClienteModel, UUID>, JpaSpecificationExecutor<ClienteModel> {


}
