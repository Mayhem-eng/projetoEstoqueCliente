package org.com;

import Exceptions.DocInvalidoException;
import Exceptions.FalhaNaManutencaoException;
import modelo.manutencaoEstoque.FazerPedido;
import modelo.manutencaoEstoque.GestaoCadastroCliente;
import modelo.manutencaoEstoque.GestaoEstoque;

import java.util.Scanner;
// ARRUMAR DATA DE VENCIMENTO SE DEVE SER SETADA
public class Main {
    static Scanner scan = new Scanner(System.in);
    public static void portalAdminstrador(String usuario, String senha) throws FalhaNaManutencaoException, DocInvalidoException {
        GestaoEstoque gestaoEstoque = new GestaoEstoque(usuario, senha);
        System.out.println("ADICIONAR PRODUTO AO ESTOQUE[1]");
        System.out.println("EDITAR PRODUTOS EM ESTOQUE[2]");
        System.out.println("EXCLUIR PRODUTO DO ESTOQUE[3]");
        System.out.println("EXCLUIR FORNECEDOR[4]");
        System.out.println("EXCLUIR CLIENTE[5]");
        System.out.print("OPCAO : ");
        int opcaoPortalADM = scan.nextInt();
        switch (opcaoPortalADM){
            case 1 -> gestaoEstoque.cadastrarProduto();
            case 2 -> {
                System.out.println("ALTERAR QUANTIDADE[1]");
                System.out.println("ALTERAR NOME[2]");
                System.out.println("ALTERAR PRECO[3]");
                System.out.print("OPCAO : ");
                int opcaoParaAlteracao = scan.nextInt();
                switch (opcaoParaAlteracao){
                    case 1 -> gestaoEstoque.alterarQuantidade();
                    case 2 -> gestaoEstoque.alterarNome();
                    case 3 -> gestaoEstoque.alterarPreco();
                    default -> throw new IllegalArgumentException("OPCAO INDISPONIVEL");
                }
            }
            case 3 -> gestaoEstoque.excluirProduto();
            case 4 -> gestaoEstoque.excluirFornecedor();
            case 5 -> GestaoCadastroCliente.excluirCliente();
            default -> throw new IllegalArgumentException("OPCAO INVALIDA");
        }
    }
    public static void portalCliente() throws DocInvalidoException {
        FazerPedido fazerPedidoLoja = new FazerPedido();

        System.out.println("CADASTRAR CLIENTE NA LOJA[1]");
        System.out.println("FAZER PEDIDO[2] ");
        System.out.println("VER CARRINHO[3]");
        System.out.println("LIMPAR CARRINHO[4]");
        System.out.println("FINALIZAR[5]");
        System.out.print("OPCAO : ");
        int opcaoPortalCliente = scan.nextInt();
        scan.nextLine();
        switch (opcaoPortalCliente){
            case 1 -> GestaoCadastroCliente.cadastrarCliente();
            case 2 -> fazerPedidoLoja.fazerPedido();
            case 3 -> fazerPedidoLoja.carrinhoDeProdutos();
            case 4 -> fazerPedidoLoja.limparCarrinho();
        }

    }

    public static void main(String[] args) throws FalhaNaManutencaoException, DocInvalidoException {

        System.out.print("USUARIO : ");
        String usuario = scan.next();
        System.out.print("SENHA: ");
        String senha = scan.next();

        while (true){
            System.out.println("ACESSO SISTEMA DA LOJA");
            System.out.print("ACESSAR COMO: ADMINISTRADOR [ADM] - CLIENTE [CLI] - SAIR [S] : ");
            String opcaoAcesso = scan.next().trim();
            if(opcaoAcesso.equalsIgnoreCase("adm")){
                portalAdminstrador(usuario, senha);
            }
            else if(opcaoAcesso.equalsIgnoreCase("cli")){
                portalCliente();
            }
            else{
                break;
            }
        }

    }
}