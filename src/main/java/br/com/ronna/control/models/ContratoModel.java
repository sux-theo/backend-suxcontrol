package br.com.ronna.control.models;

import br.com.ronna.control.enums.ContratoStatus;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "TB_CONTRATOS")
public class ContratoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID contratoId;

    @Column(nullable = false)
    private String contratoDescricao;

    @Column(nullable = false)
    private double contratoValorVisita;

    @Column(nullable = false)
    private double contratoValorRemoto;

    @OneToOne(optional = false)
    private ClienteModel cliente;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContratoStatus contratoStatus;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime contratoDataCriacao;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime contratoDataAtualizacao;

}
