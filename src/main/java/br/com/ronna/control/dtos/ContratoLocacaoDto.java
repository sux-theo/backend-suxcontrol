package br.com.ronna.control.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ContratoLocacaoDto {

    private String descricao;
    private UUID clienteId;
    private List<LocacaoAtivoDto> ativosLocados;
}
