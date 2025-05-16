package modelo.manutencaoEstoque;

import Exceptions.DocInvalidoException;
import Validacoes.ValidaCPF;
import infra.DAO;
import jakarta.persistence.EntityManager;
import modelo.entities.Clientes.Cliente;
import modelo.entities.Clientes.Conta;
import singleton.EntityManagerFactorySingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Scanner;


public class GestaoCadastroCliente {

    private static final DAO<Cliente> clienteDAO;
    private static final EntityManager em;
    private static final Scanner scan = new Scanner(System.in);
    private static final Logger logger = LogManager.getLogger(GestaoCadastroCliente.class);
    private static final FazerPedido entrarNaLojaParaPedido;

    static {
        em = new EntityManagerFactorySingleton().abrirConexao().createEntityManager();
        clienteDAO = new DAO<>(em, Cliente.class);
        entrarNaLojaParaPedido = new FazerPedido();
    }

    public GestaoCadastroCliente(){

    }

    public static void cadastrarCliente(){
        System.out.print("NOME : ");
        String nome = scan.nextLine();
        System.out.print("EMAIL : ");
        String email = scan.next();
        System.out.print("CPF : ");
        String cpf = scan.next();
        System.out.print("TELEFONE : ");
        String telefone = scan.next();

        try {
            Cliente dadosClienteParaCadastro = new Cliente(nome, email, cpf, telefone);
            Conta contaClienteVinculada = new Conta();

            dadosClienteParaCadastro.setConta(contaClienteVinculada);
            clienteDAO.incluirAtomico(dadosClienteParaCadastro);

            logger.info("CLIENTE CADASTRADO");
            entrarNaLojaParaPedido.fazerPedido();
        } catch (Exception e) {
            logger.error("ERRO AO CADASTRAR CLIENTE");
            System.out.println("ERRO -> " + e.getMessage());

        }
    }

    public static void excluirCliente() throws DocInvalidoException {
        System.out.print("Informe o CPF: ");
        String cpf = ValidaCPF.validar(scan.next());
        try{
            System.out.print("AO EXCLUIR OS DADOS DOS PEDIDOS SERAO EXCLUIDOS TAMBEM: [S/N] ");
            String op = scan.next().trim();
            if (op.equalsIgnoreCase("s")) {

                Optional<Cliente> clienteRemocao = clienteDAO.obterPorCampo("CPF", cpf)
                        .filter(a -> a.getCPF().equals(cpf))
                            .findFirst();


                if(clienteRemocao.isPresent()){
                    em.getTransaction().begin();
                    em.remove(clienteRemocao.get());
                    em.getTransaction().commit();
                    logger.info("EXCLUSAO FINALIZADA");

                }
                else{
                    logger.warn("NAO HA REGISTRO PARA EXCLUIR");
                }
            }

        }catch (Exception e) {
            logger.error("NAO FOI POSSIVEL EXCLUIR {}", e.getMessage());
        }
    }
}
