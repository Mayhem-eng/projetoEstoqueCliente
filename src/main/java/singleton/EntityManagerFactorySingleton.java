package singleton;


import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityManagerFactorySingleton {

    private static EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT = "projetoFinal2";
    private static final Logger logger = LogManager.getLogger(EntityManagerFactorySingleton.class);

    public EntityManagerFactory abrirConexao(){
        if(emf == null){
            synchronized (EntityManagerFactorySingleton.class){
                try{
                    emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
                    logger.info("Conexao aberta com sucesso");
                }catch (Exception e){
                    logger.error("Erro ao abrir conexao: {}", e.getMessage());
                    throw new EntityNotFoundException("NAO FOI POSSIVEL ABRIR A CONEXAO");
                }
            }
        }
        return emf;
    }

    public void fecharConexao(){
        if(emf.isOpen() && emf != null){
            emf.close();
            logger.info("Conexao fechada com sucesso");
        }
    }

}
