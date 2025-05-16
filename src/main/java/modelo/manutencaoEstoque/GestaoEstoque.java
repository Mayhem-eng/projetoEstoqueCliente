package modelo.manutencaoEstoque;

import Enums.Categorias;
import Exceptions.FalhaNaManutencaoException;
import Validacoes.ValidaCategoria;
import Validacoes.ValidaData;
import infra.DAO;
import jakarta.persistence.EntityManager;
import modelo.entities.Loja.Estoque;
import modelo.entities.Loja.Fornecedor;
import modelo.entities.Loja.Produto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import singleton.EntityManagerFactorySingleton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class GestaoEstoque {

    private final DAO<Produto> produtoDAO;
    private final DAO<Fornecedor> fornecedorDAO;
    private final Scanner scan = new Scanner(System.in);
    private Produto produtoParaAlteracao;
    private final EntityManager em;
    private static final Logger logger = LogManager.getLogger(GestaoEstoque.class);

    private final String user;
    private final String senha;


    public GestaoEstoque(String user, String senha) throws FalhaNaManutencaoException {
        this.user = user;
        this.senha = senha;
        try{
            this.em = new EntityManagerFactorySingleton().abrirConexao().createEntityManager();
             produtoDAO = new DAO<>(em, Produto.class);
             fornecedorDAO = new DAO<>(em, Fornecedor.class);
             logger.info("CONEXAO ABERTA");
        }catch (Exception e){
            logger.error("FALHA NA CONEXAO {}", e.getMessage());
            throw new FalhaNaManutencaoException(e.toString());
        }
    }


    public void alterarQuantidade() {
        listaProdutosEmEstoque();

        try {

            em.getTransaction().begin();

            System.out.print("ID DO PRODUTO : ");
            Long idCampoComNovaRemessa = scan.nextLong();

            System.out.print("NOVO : ");
            Long novaRemessa = scan.nextLong();

            produtoParaAlteracao = em.find(Produto.class, idCampoComNovaRemessa);
            produtoParaAlteracao.setQuantidadeEstoque(produtoParaAlteracao.getQuantidadeEstoque() + novaRemessa);

            logger.info("NOVA REMESSA CONFIGURADA");
        } catch (Exception e) {
            logger.error("FALHA NA ALTERACAO DA REMESSA {}", e.getMessage());
        }finally {
            em.getTransaction().commit();
        }

    }

    public void alterarNome(){
        listaProdutosEmEstoque();

        try{
            em.getTransaction().begin();

            System.out.print("ID DO PRODUTO : ");
            long IDProdutoParaAlterarNome = scan.nextLong();
            scan.nextLine();

            System.out.print("NOVO NOME : ");
            String novoNome = scan.nextLine();

            produtoParaAlteracao = em.find(Produto.class, IDProdutoParaAlterarNome);
            produtoParaAlteracao.setNome(novoNome);

            logger.info("NOVO NOME CONFIGURADO");

        }catch (Exception e){
            logger.error("FALHA NA ALTERACAO DO NOME {}", e.getMessage());
        }
        finally {
            em.getTransaction().commit();
        }
    }


    public void alterarPreco() {
        listaProdutosEmEstoque();

        try {

            em.getTransaction().begin();

            System.out.print("ID DO PRODUTO : ");
            long IDProdutoParaAlterarPreco = scan.nextLong();

            System.out.print("NOVO PRECO R$");
            Double novoPreco = scan.nextDouble();

            produtoParaAlteracao = em.find(Produto.class, IDProdutoParaAlterarPreco);
            produtoParaAlteracao.setPrecoVenda(novoPreco);

            logger.info("NOVO PRECO CONFIGURADO");

        } catch (Exception e) {
            logger.error("FALHA NA ALTERACAO DO PRECO {}", e.getMessage());
        }
        finally {
            em.getTransaction().commit();
        }
    }

    public void excluirFornecedor(){
       listaFornecedoresAtuais();

        try{
            em.getTransaction().begin();
            System.out.print("ID DO FORNECEDOR : ");
            Long IDFornecedorParaAlterarPreco = scan.nextLong();

            System.out.print("A EXCLUSAO SIGNIFICA A EXCLUSAO DE SEUS PRODUTOS CADASTRADOS NESSE FORNECEDOR [S/N] : ");
            String opcaoExclusaoFornecedor = scan.next().trim();

            if(opcaoExclusaoFornecedor.equalsIgnoreCase("s")){
                em.remove(em.find(Fornecedor.class, IDFornecedorParaAlterarPreco));
                em.getTransaction().commit();
                logger.info("FORNECEDOR EXCLUIDO");
            }
        }catch (Exception e){
            logger.error("FALHA AO EXCLUIR FORNECEDOR -> {}", e.getMessage());
        }
    }


    public void excluirProduto(){
        listaProdutosEmEstoque();

        try{

            System.out.print("ID DO PRODUTO : ");
            Long IDProdutoParaExclusao = scan.nextLong();

            System.out.print("CONFIRMA EXCLUSAO TOTAL [S/N]: ");
            String opcaoExclusaoProduto = scan.next().trim();

            if(opcaoExclusaoProduto.equalsIgnoreCase("s")){
                produtoDAO.excluirAtomico(em.find(Produto.class, IDProdutoParaExclusao));
                logger.info("PRODUTO EXCLUIDO");

            }
        }catch (Exception e){
            logger.error("FALHA AO EXCLUIR PRODUTO -> {}", e.getMessage());
        }
    }

    private Long cadastrarFornecedor() throws FalhaNaManutencaoException {
        Fornecedor novoFornecedor;
        System.out.println("SESSAO CADASTRO FORNECEDOR");
        System.out.print("RAZAO SOCIAL : ");
        String nomeFornecedor = scan.nextLine();
        System.out.print("CNPJ : ");
        String CnpjFornecedor = scan.nextLine();
        System.out.print("ENDERECO : ");
        String enderecoFornecedor = scan.nextLine();
        System.out.print("TELEFONE : ");
        String telefoneFornecedor = scan.nextLine();

        try {
            novoFornecedor = new Fornecedor(nomeFornecedor, CnpjFornecedor, enderecoFornecedor, telefoneFornecedor);

            fornecedorDAO.incluirAtomico(novoFornecedor);
            logger.info("FORNECEDOR CADASTRADO");

        } catch (Exception e) {
            logger.error("FALHA AO CADASTRAR FORNECEDOR -> {}", e.getMessage());
            throw new FalhaNaManutencaoException("ERRO -> " + e.getMessage());
        }

        return novoFornecedor.getId();
    }

    public void cadastrarProduto(){
        Long atualIDFornecedor = 0L;

        System.out.print("NOME : ");
        String nomeProduto = scan.nextLine();
        listaDeCategoriasDisponiveis();
        Categorias categoriaProduto;
        do {
            System.out.print("CATEGORIA:  ");
            categoriaProduto = ValidaCategoria.validar(scan.next());
        } while (categoriaProduto == null);
        scan.nextLine();
        System.out.print("PRECO : ");
        Double precoProduto = scan.nextDouble();
        System.out.print("QUANTIDADE REMESSA : ");
        Long qntdRemessaProduto = scan.nextLong();
        System.out.print("DATA DE VENCIMENTO(SE APLICAVEL) : ");
        String VencimentoProduto;
        if(categoriaProduto.name().equals("OUTROS")){
            VencimentoProduto = "N/A";
            System.out.println();
        }
        else{
            VencimentoProduto = ValidaData.validar(scan.next());
        }


        try {
            System.out.print("NOVO FORNECEDOR [1] - SELECIONAR EXISTENTE [2] : ");
            int opcaoDeFornecedor = scan.nextInt();
            scan.nextLine();


            if(opcaoDeFornecedor == 1){
                atualIDFornecedor = cadastrarFornecedor();
            }
            else if(opcaoDeFornecedor == 2){
                if(fornecedorDAO.obterTudo(new Fornecedor()).isEmpty()){
                    logger.warn("NAO HÁ FORNECEDORES CADASTRADOS");
                    atualIDFornecedor = cadastrarFornecedor();
                }
                else{
                    listaFornecedoresAtuais();
                    System.out.println("ID FORNECEDOR : ");
                    atualIDFornecedor = scan.nextLong();
                }
            }
            else{
                logger.error("OPCAO INVALIDA");
            }

            em.getTransaction().begin();


            Fornecedor fornecedor = em.find(Fornecedor.class, atualIDFornecedor);
            Produto produto = new Produto(nomeProduto, categoriaProduto, precoProduto, qntdRemessaProduto, VencimentoProduto, fornecedor);
            Estoque estoque = new Estoque(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            produto.setEstoque(estoque);
            estoque.adicionarProduto(produto);
            em.persist(produto);
            em.persist(estoque);
            em.getTransaction().commit();
            logger.info("PRODUTO ADICIONADO AO ESTOQUE");

        } catch (Exception e) {
            logger.error("FALHA AO CADASTRAR PRODUTO -> {}", e.getMessage());
        }

    }

    private void listaProdutosEmEstoque(){
        Produto produtosEstoque = new Produto();

        System.out.println("ATUAIS PRODUTOS NO ESTOQUE");
        produtoDAO.obterTudo(produtosEstoque)
                .stream()
                    .map(Produto::dadosProduto)
                        .forEach(System.out::println);
    }

    private void listaFornecedoresAtuais(){
        Fornecedor acessarListaFornecedores = new Fornecedor();

        System.out.println("ATUAIS FORNECEDORES DA LOJA :");
        fornecedorDAO.obterTudo(acessarListaFornecedores)
                .stream()
                    .map(Fornecedor::dadosForncedor)
                        .forEach(System.out::println);

    }

    private void listaDeCategoriasDisponiveis(){
        System.out.println("CATEGORIAS DISPONIVEIS > ");
        for(Categorias iteraCategorias : Categorias.values()){
            System.out.print(iteraCategorias.name() + " ");
        }
        System.out.println();
    }

}
