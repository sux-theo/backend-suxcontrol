package br.com.ronna.control.dtos;

import br.com.ronna.control.models.ClienteModel;
import lombok.Data;

import java.util.UUID;

@Data
public class ClienteVisitasDto {

    private ClienteModel cliente;
    private long totalVisitas;


    // Construtor que aceita UUID e long
    public ClienteVisitasDto(ClienteModel cliente, long totalVisitas) {
        this.cliente = cliente;
        this.totalVisitas = totalVisitas;
    }
}
