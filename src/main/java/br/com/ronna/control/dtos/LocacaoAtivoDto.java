package br.com.ronna.control.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LocacaoAtivoDto {

    private UUID ativoId;
    private UUID contratoLocacaoId;
    private double valorMensal;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataDevolucao;
    private String observacao;
}
