package br.com.ronna.control.models;

import br.com.ronna.control.enums.ContratoLocacaoStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data

public class ContratoLocacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID contratoLocacaoId;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteModel cliente;

    @OneToMany(mappedBy = "contratoLocacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LocacaoAtivoModel> ativosLocados;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @Enumerated(EnumType.STRING)
    private ContratoLocacaoStatus status;
}
