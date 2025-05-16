package Validacoes;

import Exceptions.DocInvalidoException;

public class ValidaCPF{

    public static String validar(String cpf) throws DocInvalidoException {
        String digitos = "";
        cpf = cpf.trim().replace("-", "").replace(".", "");
        if(cpf.length() == 11 && cpf.chars().noneMatch(Character::isLetter)){

            int resultPrimDigito = 0, resultSegDigito = 0;

            //Valida 1° digito
            String cpfTemporario = cpf.substring(0, 9);
            int cont = 0;
            for(int x = 10; x >= 2; x--){
                resultPrimDigito += Integer.parseInt(String.valueOf(cpfTemporario.charAt(cont))) * x;
                cont++;
            }

            //Valida 2° digito
            cpfTemporario = cpf.substring(0, 10);
            cont = 0;
            for (int x = 11; x >=2 ; x--){
                resultSegDigito += Integer.parseInt(String.valueOf(cpfTemporario.charAt(cont))) * x;
                cont++;
            }

            int restoPor11 = (resultPrimDigito * 10) % 11;
            resultPrimDigito = restoPor11 > 10 ? 0 : restoPor11;

            restoPor11 = (resultSegDigito * 10) % 11;
            resultSegDigito = restoPor11 > 10 ? 0 : restoPor11;

            digitos = resultPrimDigito + "" + resultSegDigito;
        }
        else{
            throw new DocInvalidoException(cpf);
        }
        return cpf.substring(9, 11).equalsIgnoreCase(digitos) ? cpf : null;
    }

}
