package modelo.entities.Loja;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Produto> produtos;

    @Column(nullable = false)
    private String dataEntrada;

    public Estoque() {
        super();
    }

    public Estoque(String dataEntrada) {
        this.produtos = new ArrayList<>();
        this.dataEntrada = dataEntrada;
    }

    public void adicionarProduto(Produto produto){
        this.produtos.add(produto);
    }
}
