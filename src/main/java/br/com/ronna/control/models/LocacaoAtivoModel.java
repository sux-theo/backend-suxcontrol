package br.com.ronna.control.models;

import br.com.ronna.control.enums.LocacaoAtivoStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class LocacaoAtivoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID locacaoAtivoId;

    @ManyToOne
    @JoinColumn(name = "ativo_id")
    private AtivoModel ativo;

    @ManyToOne
    @JoinColumn(name = "contrato_locacao_id")
    @JsonBackReference
    private ContratoLocacaoModel contratoLocacao;


    private LocalDateTime dataEnvio;

    private LocalDateTime dataDevolucao;

    private double valorMensal;

    private String observacao;

    @Enumerated(EnumType.STRING)
    private LocacaoAtivoStatus status;
}
