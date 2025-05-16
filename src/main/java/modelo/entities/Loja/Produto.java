package modelo.entities.Loja;


import Enums.Categorias;
import Validacoes.ValidaData;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private Categorias categoria;

    @ManyToOne
    @JoinColumn()
    private Fornecedor fornecedor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn()
    private Estoque estoque;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", message = "O preco minimo deve ser {value}")
    private Double precoVenda;

    @Column(nullable = false)
    @Min(value = 0, message = "Este campo nao deve ser negativo, {value}")
    private Long quantidadeEstoque;

    private String dataVencimento;

    @Column(nullable = false, unique = true)
    private Double codBarra;

    public Produto(String nome, Categorias categoria, Double precoVenda, Long quantidadeEstoque, String dataVencimento, Fornecedor fornecedor) {
        this.nome = nome;
        this.categoria = categoria;
        this.precoVenda = precoVenda;
        this.quantidadeEstoque = quantidadeEstoque;
        this.dataVencimento = dataVencimento.equals("N/A") ? "N/A" : ValidaData.validar(dataVencimento);
        this.codBarra = geraCodBarra();
        this.fornecedor = fornecedor;
    }

    public Produto(String nome, Categorias categoria, Double precoVenda, Long quantidadeEstoque, Fornecedor fornecedor) {
        this.nome = nome;
        this.categoria = categoria;
        this.precoVenda = precoVenda;
        this.quantidadeEstoque = quantidadeEstoque;
        this.codBarra = geraCodBarra();
        this.fornecedor = fornecedor;
    }


    private Double geraCodBarra(){
        Random rand = new Random();
        return rand.nextDouble(0, Double.MAX_VALUE);
    }

    public String dadosProduto(){
        return String.format("ID: %d - Nome: %s - Preco: %.2f - Nome fornecedor: %s - Qtd.Estoque: %d",
                getId(), getNome(), getPrecoVenda() , getFornecedor().getNome(), getQuantidadeEstoque());
    }

    @Override
    public String toString() {
        return dadosProduto();
    }
}
