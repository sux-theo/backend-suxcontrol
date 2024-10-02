package br.com.ronna.control.models;

import br.com.ronna.control.enums.EmpresaStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "TB_EMPRESAS")
public class EmpresaModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID empresaId;

    @Column(nullable = false)
    private String empresaNomeFantasia;

    @Column(nullable = false, unique = true)
    private String empresaCNPJ;

    @Column(nullable = false, unique = true)
    private String empresaInscricaoEstadual;

    @Column(nullable = false)
    private String empresaEndereco;

    @Column(nullable = false)
    private String empresaEmail;

    @Column(nullable = false)
    private String empresaTelefone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmpresaStatus empresaStatus;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dataAtualizacao;


    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<ClienteModel> clientes;

}
