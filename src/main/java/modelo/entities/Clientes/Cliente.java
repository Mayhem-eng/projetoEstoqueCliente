package modelo.entities.Clientes;

import Exceptions.DocInvalidoException;
import Exceptions.IlegalFormatoEmailException;
import Validacoes.ValidaCPF;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelo.entities.Loja.Pedido;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 11, unique = true)
    private String CPF;

    @Column(nullable = false)
    private String telefone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_conta", unique = true)
    private Conta conta;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos;

    public Cliente(String nome, String email, String CPF, String telefone) throws DocInvalidoException {
        this.nome = nome;
        setEmail(email);
        this.CPF = ValidaCPF.validar(CPF);
        this.telefone = telefone;
    }

    private void setEmail(String email){
        if(email != null && (email.endsWith("@gmail.com") || email.endsWith("@bradesco.com.br"))){
            this.email = email;
        }
        else{
            throw new IlegalFormatoEmailException(email);
        }
    }
    public String dadosCliente(){
        return String.format("Nome: %s - Email: %s - CPF: %s - Telefone: %s",
                getNome(), getEmail(), getCPF(), getTelefone());
    }

    @Override
    public String toString() {
        return dadosCliente();
    }
}
