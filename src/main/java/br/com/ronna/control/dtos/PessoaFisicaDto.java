package br.com.ronna.control.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PessoaFisicaDto {

    private String clienteNome;

    private String clienteTelefone;

    private String clienteEmail;

    private String clienteEndereco;

    private String clienteNumero;

    private String clienteBairro;

    private String clienteCidade;

    private String clienteUF;

    private String clienteCEP;

    private String clienteCPF;

    private UUID empresa;

    private boolean isFechamentoSeparado;

}
