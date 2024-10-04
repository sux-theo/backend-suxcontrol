package br.com.ronna.control.dtos;

import br.com.ronna.control.enums.FechamentoStatus;
import br.com.ronna.control.models.VisitaModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class FechamentoResponseDto {

    private UUID fechamentoId;
    private LocalDateTime fechamentoInicio;
    private LocalDateTime fechamentoFinal;
    private Double fechamentoValorServicos;
    private Double fechamentoValorProdutos;
    private FechamentoStatus fechamentoStatus;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Set<VisitaModel> visitas;
}
