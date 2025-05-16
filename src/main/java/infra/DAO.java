package infra;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Stream;

public class DAO<E>{

    private Class<E> classe;
    private EntityManager em;
    private final Logger logger = LogManager.getLogger(DAO.class);

    public DAO() {
        super();
    }

    public DAO(EntityManager em, Class<E> classe) {
        this.classe = classe;
        this.em = em;
    }

    public DAO<E> abrirT(){
        em.getTransaction().begin();
        return this;
    }
    public void fecharT(){
        em.getTransaction().commit();
    }

    public void rollbackT(){
        em.getTransaction().rollback();
    }

    public DAO<E> incluir(E entidade){
        em.persist(entidade);
        return this;
    }

    public void incluirAtomico(E entidade){
        abrirT().incluir(entidade).fecharT();
    }


    public List<E> obterTudo(E entidade){
        String JPQL = "SELECT u FROM "+entidade.getClass().getSimpleName()+" u";
        TypedQuery<E> query = em.createQuery(JPQL, classe);
        return query.getResultList();
    }

    public Stream<E> obterPorCampo(String campo, Object valor){
        String JPQL = "SELECT u FROM "+ classe.getName()+" u WHERE "+campo+" = :setaValor";
        TypedQuery<E> query = em.createQuery(JPQL, classe).setParameter("setaValor", valor);
        return query.getResultStream();
    }


    private DAO<E> excluir(E entidade){
        em.remove(entidade);
        return this;
    }


    public void excluirAtomico(E entidade){
        abrirT().excluir(entidade).fecharT();
    }



    public void excluirPorCampo(String campo, Object valor){
        em.getTransaction().begin();
        String JPQL = "DELETE FROM "+classe.getName()+ " u WHERE "+campo+" = :valor";
        Query query = em.createQuery(JPQL).setParameter("valor", valor);
        try {
            query.executeUpdate();
        } catch (Exception e) {
            if(em.getTransaction().isActive()){
                logger.error("NAO FOI POSSIVEL EXCLUIR, {}", e.getMessage());
                rollbackT();
            }
        }finally {
            em.getTransaction().commit();
        }

    }

    public void atualizar(Long id, String campo, Object novoValor){
        String JPQL = "UPDATE "+classe.getName()+"u SET "+campo+" = :setaNovoValor WHERE id = :setaID";
        Query query = em.createQuery(JPQL).setParameter("setaNovoValor", novoValor).setParameter("setaID", id);
        try {
            abrirT();
            int linhasAlteradas = query.executeUpdate();
            System.out.println("Linhas alteradas: "+ linhasAlteradas);
            fecharT();
        } catch (Exception e) {
            if(em.getTransaction().isActive()){
                logger.error("NAO FOI POSSIVEL ATUALIZAR");
                rollbackT();
            }
        }finally {
            fecharT();
        }

    }

    public void excluirTudo(Long id){
        abrirT();
        String JPQL = "DELETE FROM "+classe.getName()+" WHERE id = :id";
        Query query = em.createQuery(JPQL).setParameter("id", id);
        query.executeUpdate();
        fecharT();

    }

}
