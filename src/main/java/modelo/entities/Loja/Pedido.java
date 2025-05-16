package modelo.entities.Loja;

import Validacoes.ValidaData;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelo.entities.Clientes.Cliente;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne()
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;


    @ManyToMany()
    @JoinTable(name = "pedido_produto",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id"))
    private List<Produto> produtos;

    @ManyToMany(mappedBy = "pedidos")
    private Set<RegistroPagamento> pagamentos;

    private String dataPedido;

    private String status;

    @Setter(AccessLevel.NONE)
    private Double totalAPagar;


    public Pedido(String dataSolicitacao){
        produtos = new ArrayList<>();
        pagamentos = new HashSet<>();
        this.dataPedido = ValidaData.validar(dataSolicitacao);
        this.totalAPagar = 0.0;
    }

    public void setTotalAPagar(Double totalAPagar) {
        this.totalAPagar = totalAPagar;
        if(getTotalAPagar() - totalAPagar < 0){
            this.totalAPagar = 0.0;
        }
    }
}
