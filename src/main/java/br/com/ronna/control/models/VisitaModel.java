package br.com.ronna.control.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "TB_VISITAS")
public class VisitaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID visitaId;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime visitaInicio;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime visitaFinal;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String visitaDescricao;

    @ManyToMany
    private Set<FuncionarioModel> funcionarios;

    @ManyToOne
    @JoinColumn(name = "clienteId")
    private ClienteModel cliente;

    @ManyToOne
    @JoinColumn(name = "localId")
    private LocalModel local;

    private Double visitaValorProdutos;

    private Double visitaTotalAbono;

    @Column(nullable = false)
    private boolean visitaRemoto;

    private Double visitaTotalHoras;

    @ManyToOne
    @JsonIgnore
    private FechamentoModel fechamento;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "TB_VISITA_PRODUTOS", joinColumns = @JoinColumn(name = "visitaId"))
    private Set<ProdutoModel> produtos;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitaModel that = (VisitaModel) o;
        return Objects.equals(visitaId, that.visitaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitaId);
    }
}
