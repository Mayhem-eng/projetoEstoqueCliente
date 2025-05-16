package modelo.manutencaoEstoque;

import Enums.FormaDePagamento;
import Exceptions.DadosInvalidosException;
import Exceptions.DocInvalidoException;
import Exceptions.InvalidoEnumException;
import Exceptions.SaldoInsuficienteException;
import Validacoes.ServicosConta;
import infra.DAO;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Getter;
import modelo.entities.Clientes.Cliente;
import modelo.entities.Clientes.Conta;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
public class RealizaPagamento{

    private final EntityManager em;
    private final DAO<Conta> contaDAO;
    private final DAO<Cliente> clienteDAO;
    private final Logger logger = LogManager.getLogger(RealizaPagamento.class);
    private final Conta conta;
    private boolean acessoConta;
    ServicosConta servicosConta = new ServicosConta();

    RealizaPagamento(Cliente cliente, EntityManager em) throws DocInvalidoException {
        this.em = em;
        this.clienteDAO = new DAO<>(em, Cliente.class);
        this.contaDAO = new DAO<>(em, Conta.class);
        conta = em.find(Conta.class, cliente.getConta().getId());
        this.acessoConta = false;



        String cpf = servicosConta.informarCPF();
        if(cliente.getCPF().equals(cpf)){
            acessoConta = true;
            logger.info("ACESSO LIBERADO");
        }
    }

    @Transactional
    protected void sacar(double valor) throws DocInvalidoException {
            if(valor > 0){
                if(conta.getSaldo() - valor > 0){
                    conta.setSaldo(conta.getSaldo() - valor);

                }
                else{
                    double diferenca = valor - conta.getSaldo();
                    if(conta.getChequeEspecial() - diferenca >= 0){
                        conta.setChequeEspecial(conta.getChequeEspecial() - diferenca);
                        conta.setSaldo(0);

                    }
                    else{
                        conta.setChequeEspecial(0);
                        System.out.println("Faca um deposito");
                        depositar(servicosConta.manutencao());
                    }

                }
                em.merge(conta);
            }
            else{
                throw new DadosInvalidosException("DEVE UM NUMERO E VALOR POSITIVO");
            }
    }

    @Transactional
    protected void depositar(double valor) throws DocInvalidoException {
            if(acessoConta){
                if(valor > 0){
                    if(conta.getSaldo() == 0 && conta.getChequeEspecial() < 200){
                        double diferencaChequeEspecial = conta.getChequeEspecial() + valor;
                        if(diferencaChequeEspecial > 200){
                            conta.setChequeEspecial(200);
                            conta.setSaldo(diferencaChequeEspecial - 200);
                        }
                        else{
                            conta.setChequeEspecial(diferencaChequeEspecial);
                        }

                    }
                    else{
                        conta.setSaldo(conta.getSaldo() + valor);
                    }

                }

                else{
                    throw new DadosInvalidosException("DEVE UM NUMERO E VALOR POSITIVO");
                }
            }

            else{
                logger.warn("DEVE ACESSAR A CONTA PRIMEIRO");
            }
    }

    public void pagamento(Double preco, FormaDePagamento formaDePagamento) throws DocInvalidoException {
            if(acessoConta){
                em.getTransaction().begin();
                if(preco > 0){
                    if(formaDePagamento.equals(FormaDePagamento.DEBITO)){
                        sacar(preco);
                        logger.info("PAGAMENTO NO VALOR DE R${} FEITO VIA DEBITO > {}",preco, conta.getSaldo());
                    }
                    else if(formaDePagamento.equals(FormaDePagamento.CREDITO)){
                        conta.setCartaoDeCredito(conta.getCartaoDeCredito() - preco);
                        if(conta.getCartaoDeCredito() < 0){
                            conta.setCartaoDeCredito(0);
                            throw new SaldoInsuficienteException("LIMITE EXCEDITO");
                        }
                        else{
                            logger.info("PAGAMENTO NO VALOR DE R${} FEITO VIA CREDITO > {}",preco, conta.getCartaoDeCredito());

                        }
                    }
                    else{
                        throw new InvalidoEnumException(formaDePagamento.toString());
                    }
                
                    em.merge(conta);
                    em.getTransaction().commit();
                }

                else{
                    throw new DadosInvalidosException("DEVE SER UM NUMERO E VALOR POSITIVO");
                }
            }
            else{
                logger.warn("DEVE ACESSAR A CONTA PRIMEIRO");
            }
    }



}
