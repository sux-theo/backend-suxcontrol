package br.com.ronna.control.dtos;

import br.com.ronna.control.models.ClienteModel;
import br.com.ronna.control.models.FuncionarioModel;
import br.com.ronna.control.models.LocalModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class VisitaDto {

    private LocalDateTime visitaInicio;

    private LocalDateTime visitaFinal;

    private String visitaDescricao;

    private boolean visitaRemoto;

    private Double visitaValorProdutos;

    private Double visitaTotalAbono;

    private Set<FuncionarioModel> funcionarios;

    private UUID cliente;

    private UUID local;

}
