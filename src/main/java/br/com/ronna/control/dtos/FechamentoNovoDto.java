package br.com.ronna.control.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class FechamentoNovoDto {
    
    private LocalDateTime fechamentoInicio;
    
    private LocalDateTime fechamentoFinal;

    private Set<UUID> clientesSelecionados;

}