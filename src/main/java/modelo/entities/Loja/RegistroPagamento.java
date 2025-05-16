package modelo.entities.Loja;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class RegistroPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "pagamento_pedido",
            joinColumns = @JoinColumn(name = "registro_pagamento_id"),
            inverseJoinColumns = @JoinColumn(name = "pedido_id"))
    private Set<Pedido> pedidos;

    public RegistroPagamento() {
        this.pedidos = new HashSet<>();
    }

    public void adicionarPedido(Pedido pedido){
        pedidos.add(pedido);

        pedido.getPagamentos().add(this);
    }

    public void dadosDoPagamento(Long id){
        getPedidos().stream()
                .filter(cod -> cod.getId().equals(id))
                    .map(dados -> "CLIENTE: " + dados.getCliente().getNome() + " DATA PEDIDO: " + dados.getDataPedido() + " TOTAL PAGO: " + dados.getTotalAPagar())
                        .findFirst()
                            .ifPresent(System.out::println);
    }
}
