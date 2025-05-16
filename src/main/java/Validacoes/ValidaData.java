package Validacoes;

import Exceptions.DataAnteriorDataAtualException;
import Exceptions.IncoerenciaDataException;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

public class ValidaData {


    public static String validar(String data){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate dataVencimento = LocalDate.parse(data, formatter);

        if(dataVencimento.isBefore(LocalDate.now())){
            throw new DataAnteriorDataAtualException(data);
        }

        if(dataVencimento.getMonth().getValue() == 2){
            if(Year.now().isLeap() && dataVencimento.getDayOfMonth() > 29){
                throw new IncoerenciaDataException(data);
            }
            if(!Year.now().isLeap() && dataVencimento.getDayOfMonth() > 28){
                throw new IncoerenciaDataException(data);
            }
        }
        return dataVencimento.format(formatter);
    }

//    private LocalDate geraDataOld(){
//        Random rand = new Random();
//        int geraDia, geraMes, geraAno;
//
//        geraMes = rand.nextInt(1, 12);
//        geraAno = rand.nextInt(Year.now().getValue(), 2040);
//
//
//        if(geraMes == 1 || geraMes == 3 || geraMes == 5 || geraMes == 7 || geraMes == 8 || geraMes == 10 || geraMes == 12){
//            geraDia = rand.nextInt(1, 31);
//        }
//        else if(geraMes == 4 || geraMes == 6 || geraMes == 9 || geraMes == 11){
//            geraDia = rand.nextInt(1, 30);
//        }
//        else{
//            if(Year.now().isLeap()){
//                geraDia = rand.nextInt(1, 29);
//            }
//            else{
//                geraDia = rand.nextInt(1, 28);
//            }
//        }
//        return LocalDate.of(geraDia, geraMes, geraAno);
//    }
}
