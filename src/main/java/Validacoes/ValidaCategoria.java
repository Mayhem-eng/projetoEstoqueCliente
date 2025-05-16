package Validacoes;

import Enums.Categorias;
import Exceptions.InvalidoEnumException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.Arrays;

import java.util.Optional;



public class ValidaCategoria {
    private static final Logger logger = LogManager.getLogger(ValidaCategoria.class);

    public static Categorias validar(String categoria){

        Optional<Categorias> listaDasCategorias = Arrays.stream(Categorias.values())
                .filter(cat -> categoria.equalsIgnoreCase(cat.name()))
                    .findFirst();

        if(listaDasCategorias.isPresent()){
            return listaDasCategorias.get();
        }
        else{
            logger.error("ENUM INVALIDO");
            return null;
        }

    }
}
