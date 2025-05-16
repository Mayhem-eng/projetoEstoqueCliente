package modelo.entities.Loja;


import Exceptions.DocInvalidoException;
import Validacoes.ValidaCNPJ;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL)
    private List<Produto> produto;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false, unique = true)
    private String CNPJ;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String telefone;

    public Fornecedor(){

    }

    public Fornecedor(String nome, String CNPJ, String endereco, String telefone) throws DocInvalidoException {
        this.nome = nome;
        this.CNPJ = ValidaCNPJ.validar(CNPJ);
        this.endereco = endereco;
        this.telefone = telefone;
    }

    public String dadosForncedor(){
        return String.format("Id: %d - Nome: %s - CNPJ: %s - Endereco: %s - Telefone: %s",
                getId(), getNome(), getCNPJ(), getEndereco(), getTelefone());
    }
}
