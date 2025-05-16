package Validacoes;

import Exceptions.DocInvalidoException;

import java.util.Scanner;

public class ServicosConta {
    private final Scanner scan = new Scanner(System.in);

    public String informarCPF() throws DocInvalidoException {
        System.out.print("CONFIRME O CPF: ");
        return ValidaCPF.validar(scan.next());
    }

    public double manutencao(){
        System.out.print("DEPOSITO: ");
        return scan.nextDouble();
    }
}
