package modelo.entities.Clientes;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "conta")
    private Cliente cliente;

    @DecimalMin(value = "0.0", message = "O preco minimo deve ser {value}")
    private double saldo;

    @DecimalMin(value = "0.0", message = "O preco minimo deve ser {value}")
    private double chequeEspecial;

    @DecimalMin(value = "0.0", message = "O preco minimo deve ser {value}")
    private double cartaoDeCredito;

    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", clienteID=" + cliente.getId() +
                ", cliente_nome=" + cliente.getNome() +
                ", saldo=" + saldo +
                ", chequeEspecial=" + chequeEspecial +
                ", cartaoDeCredito=" + cartaoDeCredito +
                '}';
    }
}


