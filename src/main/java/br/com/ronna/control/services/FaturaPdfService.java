package br.com.ronna.control.services;

import br.com.ronna.control.dtos.FechamentoNovoDto;
import br.com.ronna.control.dtos.FechamentoResponseDto;
import br.com.ronna.control.models.FechamentoModel;

import java.io.IOException;

public interface FaturaPdfService {

    public byte[] gerarFaturaPdf(FechamentoResponseDto fechamentoNovoDto) throws IOException;

}
