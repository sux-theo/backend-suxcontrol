package br.com.ronna.control.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FiltroFechamentoDto {

    private UUID cliente;

    private LocalDateTime inicio;

    private LocalDateTime fim;
}
