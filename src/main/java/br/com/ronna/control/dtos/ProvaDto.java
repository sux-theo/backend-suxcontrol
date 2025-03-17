package br.com.ronna.control.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProvaDto {
    private LocalDateTime fechamentoInicio;
    private LocalDateTime fechamentoFinal;
    private String clienteId;
}
