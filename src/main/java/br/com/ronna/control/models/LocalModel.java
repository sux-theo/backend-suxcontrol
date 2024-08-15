package br.com.ronna.control.models;

import br.com.ronna.control.enums.LocalStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "TB_LOCAL")
public class LocalModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "varbinary(36)")
    private UUID localId;

    @Column(nullable = false)
    private String localNome;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name="clienteId")
    @JsonIgnore
    private ClienteModel cliente;

    @OneToMany(mappedBy = "local")
    @JsonIgnore
    private Set<FechamentoModel> fechamentos;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LocalStatus localStatus;
}
