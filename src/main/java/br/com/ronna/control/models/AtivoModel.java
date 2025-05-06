package br.com.ronna.control.models;

import br.com.ronna.control.enums.AtivoStatus;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "TB_ATIVOS")
public class AtivoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID ativoId;

    @Column(nullable = false)
    private String ativoDescricao;

    @Column(nullable = false)
    private double ativoValorCompra;

    @Column(nullable = false)
    private double ativoValorLocacao;

    @Column(nullable = false)
    private String ativoPatrimonio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AtivoStatus ativoStatus;



    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime ativoDataCriacao;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime ativoDataAtualizacao;

}
