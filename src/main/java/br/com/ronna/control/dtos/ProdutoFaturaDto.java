package br.com.ronna.control.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProdutoFaturaDto {

    private String nome;
    private Double preco;
    private int quantidade;
    private LocalDateTime visitaInicio;
    private LocalDateTime visitaFinal;
}
