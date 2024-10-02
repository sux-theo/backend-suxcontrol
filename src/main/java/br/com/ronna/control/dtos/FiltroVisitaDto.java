package br.com.ronna.control.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FiltroVisitaDto {

    private LocalDateTime visitaInicio;

    private LocalDateTime visitaFinal;

    private UUID cliente;

    private UUID funcionario;
}
