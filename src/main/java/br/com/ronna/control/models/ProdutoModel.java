package br.com.ronna.control.models;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Embeddable
public class ProdutoModel {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private double preco;

    @Column(nullable = false)
    private int quantidade;

}
