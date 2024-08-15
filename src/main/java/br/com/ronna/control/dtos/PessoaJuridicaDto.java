package br.com.ronna.control.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PessoaJuridicaDto {

    private String clienteNome;

    private String clienteTelefone;

    private String clienteEmail;

    private String clienteEndereco;

    private String clienteNumero;

    private String clienteBairro;

    private String clienteCidade;

    private String clienteUF;

    private String clienteCEP;

    private String clienteRazaoSocial;

    private String clienteCNPJ;

    private String clienteInscricaoEstadual;

    private UUID empresa;

    private String isFechamentoSeparado;

}
