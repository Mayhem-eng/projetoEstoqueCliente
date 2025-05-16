package modelo.manutencaoEstoque;

import Enums.FormaDePagamento;
import Exceptions.DocInvalidoException;
import Validacoes.ValidaCPF;
import infra.DAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import modelo.entities.Clientes.Cliente;
import modelo.entities.Clientes.Conta;
import modelo.entities.Loja.Estoque;
import modelo.entities.Loja.Pedido;
import modelo.entities.Loja.Produto;
import modelo.entities.Loja.RegistroPagamento;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import singleton.EntityManagerFactorySingleton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FazerPedido {

    private final EntityManager em;
    private DAO<Pedido> daoPedido;
    private DAO<Cliente> daoCliente;
    private DAO<Estoque> daoEstoque;
    private DAO<Conta> daoConta;
    private DAO<RegistroPagamento> daoRegistroPagamento;
    private boolean pedidosEstaoVazios;

    private Pedido pedidoDoCliente;
    private  Cliente clienteVinculadoAoPedido;
    private Produto produtoSeleciadoPeloCliente;
    private RegistroPagamento registroPagamentosDoPedigoDoCliente;
    private final Scanner scan = new Scanner(System.in);
    private final Logger logger = LogManager.getLogger(FazerPedido.class);

    private Long idDoPedidoDoCliente = 0L;
    private Long idDoClienteSincronizado = 0L;
    private Long idDoProduto;
    private String nomeProdutoNoCarrinho;
    private Double precoDoProdutoNoCarrinho;

    public FazerPedido(){
        this.em = new EntityManagerFactorySingleton().abrirConexao().createEntityManager();
        this.daoPedido = new DAO<>(em, Pedido.class);
        this.daoCliente = new DAO<>(em, Cliente.class);
        this.daoEstoque = new DAO<>(em, Estoque.class);
        this.daoConta = new DAO<>(em, Conta.class);
        this.daoRegistroPagamento = new DAO<>(em, RegistroPagamento.class);

    }

   public void fazerPedido() throws DocInvalidoException {
        List<Produto> carrinhoDeProdutos = new ArrayList<>();
        pedidoDoCliente = new Pedido(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        listaDeProdutosEmEstoque();

        sincronizaClienteComPedido();

       try {
           while(true){
               System.out.print("ID PRODUTO : ");
               Long idProdutoSelecionado = scan.nextLong();

               produtoSeleciadoPeloCliente = em.find(Produto.class, idProdutoSelecionado);
               if(produtoSeleciadoPeloCliente != null){
                   carrinhoDeProdutos.add(em.find(Produto.class, idProdutoSelecionado));
               }
               else{
                   logger.warn("NAO EXISTE UM PRODUTO COM ID {} NO ESTOQUE ->", idProdutoSelecionado);
               }

               try {
                   pedidoDoCliente.setTotalAPagar(em.find(Produto.class, idProdutoSelecionado).getPrecoVenda() + pedidoDoCliente.getTotalAPagar());
                   em.persist(pedidoDoCliente);

                   pedidoDoCliente.setProdutos(carrinhoDeProdutos);
                   daoPedido.incluirAtomico(pedidoDoCliente);

                   logger.info("PRODUTO ADICIONADO - PRE TOTAL R${}", pedidoDoCliente.getTotalAPagar());

               } catch (Exception e) {
                   logger.error("ERRO AO INCLUIR TOTAL OU PRODUTO {}", e.getMessage());
               }

               System.out.print("NOVO PRODUTO [S/N] : ");
               String novoProduto = scan.next().trim();
               if(!novoProduto.equalsIgnoreCase("s")){
                   break;
               }
           }


       } catch (Exception e) {
           logger.error("ERRO NA INSERCAO {}", e.getMessage());
       }

   }

   public void carrinhoDeProdutos() throws DocInvalidoException {


        String JPQL = "SELECT c.id, ped.id, p.id, p.nome, p.precoVenda FROM Pedido ped JOIN ped.cliente c JOIN ped.produtos p";
        List<Object[]> listaDeProdutosNoCarrinho = em.createQuery(JPQL).getResultList();
        if(listaDeProdutosNoCarrinho.isEmpty()){
            logger.warn("NAO HÁ PRODUTOS NO CARRINHO");
        }
        else{

            System.out.println("PRODUTOS NO CARRINHO");
            for(Object[] resultadoBusca : listaDeProdutosNoCarrinho){
                idDoClienteSincronizado = (Long)resultadoBusca[0];
                idDoPedidoDoCliente = (Long)resultadoBusca[1];
                idDoProduto = (Long)resultadoBusca[2];
                nomeProdutoNoCarrinho = (String) resultadoBusca[3];
                precoDoProdutoNoCarrinho = (Double) resultadoBusca[4];

                System.out.println("ID PRODUTO : " + idDoProduto + " NOME : " + nomeProdutoNoCarrinho + " PRECO : " + precoDoProdutoNoCarrinho);

            }

            pedidoDoCliente = em.find(Pedido.class, idDoPedidoDoCliente);
            clienteVinculadoAoPedido = em.find(Cliente.class, idDoClienteSincronizado);

            while (true){
                pedidosEstaoVazios = em.createQuery(JPQL).getResultList().isEmpty();

                System.out.print("ALTERAR PRODUTO[S/N] : ");
                String opcaoParaAlterarProdutoNoCarrinho = scan.next().trim();

                if(opcaoParaAlterarProdutoNoCarrinho.equalsIgnoreCase("s") && !pedidosEstaoVazios){
                    System.out.print("ID PRODUTO : ");
                    Long produtoSelecionadoParaAlteracao = scan.nextLong();

                    pedidoDoCliente.setTotalAPagar(pedidoDoCliente.getTotalAPagar() - removerProdutoCarrinho(produtoSelecionadoParaAlteracao)); // ---->> AQUI ESTA SETANOO PRECO DO PRODUTO PARA MENOS

                    try {
                        daoPedido.incluirAtomico(pedidoDoCliente);
                        logger.info("CARRINHO ATUALIZADO");
                    } catch (Exception e) {
                        logger.error("FALHA AO ATUALIZAR CARRINHO -> {}", e.getMessage());
                    }

                }
                else{
                    break;
                }

            }

            if(!pedidosEstaoVazios){
                System.out.print("FINALIZAR PAGAMENTO [S/N] : ");
                if(scan.next().trim().equalsIgnoreCase("s")){
                    executarPagamento(pedidoDoCliente, clienteVinculadoAoPedido);
                }
                else{
                    carrinhoDeProdutos();  // ----> Se n finalizar pagamento irei fazr uma chamada ciclica e voltar ao carrinho
                }
            }
            else{
                logger.warn("CARRINHO VAZIO");
            }
        }


   }

    private void executarPagamento(Pedido pedidoFinal,Cliente clienteFinal) throws DocInvalidoException {
        RealizaPagamento realizarPagamentoCliente = new RealizaPagamento(clienteFinal, em);

        System.out.print("FORMA DE PAGAMENTO DEBITO [D] - CREDITO [C] : ");
        String escolhaFormaDePagamento = scan.next().trim();

        switch (escolhaFormaDePagamento.toUpperCase()){
            case "D" -> realizarPagamentoCliente.pagamento(pedidoFinal.getTotalAPagar(), FormaDePagamento.DEBITO);
            case "C" -> realizarPagamentoCliente.pagamento(pedidoFinal.getTotalAPagar(), FormaDePagamento.CREDITO);
        }

        emiteRegistroDoPagamento();


        daoPedido.excluirTudo(idDoPedidoDoCliente);

    }


    private double removerProdutoCarrinho(Long IdProdutoSelecionadoParaRemocao){
        Long idPedidoDoCliente = pedidoDoCliente.getId();
        pedidoDoCliente = em.find(Pedido.class, idPedidoDoCliente);

        produtoSeleciadoPeloCliente = em.find(Produto.class, IdProdutoSelecionadoParaRemocao);

        try {
            em.getTransaction().begin();
            pedidoDoCliente.getProdutos().remove(produtoSeleciadoPeloCliente);
            em.merge(pedidoDoCliente);
            pedidoDoCliente.setTotalAPagar(pedidoDoCliente.getTotalAPagar() - em.find(Produto.class, IdProdutoSelecionadoParaRemocao).getPrecoVenda());

            em.getTransaction().commit();
            logger.info("PRODUTO REMOVIDO");

        } catch (Exception e) {
            logger.error("NAO FOI POSSIVEL REMOVER O PRODUTO {}", e.getMessage());
        }

        return pedidoDoCliente.getTotalAPagar();
    }

    public void limparCarrinho() throws DocInvalidoException {

        System.out.print("INFORME O CPF PARA CONFIRMAR : ");
        String cpfParaConfirmarExclusao = ValidaCPF.validar(scan.next());

        String JPQL = "SELECT ped.id FROM Pedido ped JOIN ped.cliente c WHERE c.CPF = :cpf";
        Query query = em.createQuery(JPQL).setParameter("cpf", cpfParaConfirmarExclusao);
        Optional resultado = query.getResultStream().findFirst();

        if(!daoPedido.obterTudo(new Pedido()).isEmpty() && resultado.isPresent()){
            idDoPedidoDoCliente = (Long)resultado.get();

            daoPedido.excluirTudo(idDoPedidoDoCliente);

            logger.warn("CARRINHO LIMPO");
        }
        else{
            logger.warn("CARRINHO JA ESTA VAZIO OU CPF NAO CADASTRADO");
        }
    }


    private void listaDeProdutosEmEstoque(){

        Estoque validaProdutosNoEstoque = new Estoque();
        if(daoEstoque.obterTudo(validaProdutosNoEstoque).isEmpty()){
            logger.warn("NAO HÁ PRODUTOS, CONTATE O ADMINISTRADOR");
        }
        else{
            System.out.println("PRODUTOS DISPONIVEIS NO ESTOQUE ->");
            daoEstoque.obterTudo(validaProdutosNoEstoque)
                    .stream()
                    .map(Estoque::getProdutos)
                    .forEach(System.out::println);
        }
    }

    private void sincronizaClienteComPedido() throws DocInvalidoException {

        System.out.print("CPF CLIENTE : ");
        String cpfDoCliente = ValidaCPF.validar(scan.next());

        Optional<Cliente> buscaCpfClienteNoCadastro =
                daoCliente.obterPorCampo("CPF", cpfDoCliente)
                        .findFirst();


        if(buscaCpfClienteNoCadastro.isPresent()){
            clienteVinculadoAoPedido = buscaCpfClienteNoCadastro.get();
            pedidoDoCliente.setCliente(clienteVinculadoAoPedido);
        }
        else{
            logger.warn("ESSE CLIENTE NAO CONSTA NO CADASTRO DE CLIENTES DA LOJA");
            logger.warn("VOCE SERA REDIRECIONADO PARA A SESSAO DE CADASTRO DO CLIENTE");
            scan.nextLine();
            GestaoCadastroCliente.cadastrarCliente();
        }
    }

    private void emiteRegistroDoPagamento(){
        registroPagamentosDoPedigoDoCliente = new RegistroPagamento();
        try{

            registroPagamentosDoPedigoDoCliente.adicionarPedido(em.find(Pedido.class, idDoPedidoDoCliente));
            daoRegistroPagamento.incluirAtomico(registroPagamentosDoPedigoDoCliente);

        }catch (Exception e){
            logger.error("FALHA AO SALVAR DADOS DO PAGAMENTO -> {}", e.getMessage());
        }
        System.out.println("DADOS DO PAGAMENTO");
        registroPagamentosDoPedigoDoCliente.dadosDoPagamento(idDoPedidoDoCliente);

    }
}
