package br.com.ronna.control.dtos;

import br.com.ronna.control.models.FuncionarioModel;
import br.com.ronna.control.models.ProdutoModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class VisitaFaturaDto {

    private LocalDateTime visitaInicio;

    private LocalDateTime visitaFinal;

    private String visitaDescricao;

    private boolean visitaRemoto;

    private Double visitaValorProdutos;

    private Double visitaTotalAbono;

    private String funcionarios;

    private String cliente;

    private String localCliente;

    private Set<ProdutoFaturaDto> produtos;

}
