package Validacoes;

import Exceptions.DocInvalidoException;

import java.util.Arrays;
import java.util.List;

public  class ValidaCNPJ{

    private static int calculaDigito(int valor){
        return valor % 11 < 2 ? 0 : 11 - (valor % 11);
    }

    public static String validar(String cnpj) throws DocInvalidoException {
        cnpj = cnpj.trim().replace(".", "").replace("-", "").replace("/", "");
        String cnpj_aux = cnpj.substring(0, 12);

        List<Integer> seq_somas = Arrays.asList(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2);
        int result_somas = 0;

        for(int x = 1; x < seq_somas.size(); x++){
            result_somas += seq_somas.get(x) * Integer.parseInt(String.valueOf(cnpj_aux.charAt(x-1)));
        }

        cnpj_aux += calculaDigito(result_somas);

        result_somas = 0;
        for(int x = 0; x < seq_somas.size(); x++){
            result_somas += seq_somas.get(x) * Integer.parseInt(String.valueOf(cnpj_aux.charAt(x)));

        }

        cnpj_aux += calculaDigito(result_somas);

        if(cnpj_aux.equals(cnpj)){
            return cnpj;
        }
        else{
            throw new DocInvalidoException(cnpj);
        }
    }
}
