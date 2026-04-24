package com.javaspring.demo.dao;

import org.apache.commons.lang3.exception.ExceptionUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * DAO genérico. Esta classe não dá para ser usada diretamente. Todo DAO deve
 * extender desta classe.
 *
 * @author Thiago
 * @param <T>
 * @param <I>
 */
public abstract class GenericDAO<T, I extends Serializable> extends DAO {

    private final Class<T> classe;

    public GenericDAO(Class<T> classe) {
        this.classe = classe;
    }

    public Class<T> getClasse() {
        return classe;
    }

    /**
     * Salvar um objeto no banco, gerando um id para ele. Este método fará o
     * autoincremento do @Id.
     *
     * @param entidade objeto a ser inserido no banco
     * @return id do objeto inserido
     */
    public I incluirAutoincrementando(T entidade) {
        EntityManager conexao = getEntityManager();
        try {
            conexao.getTransaction().begin();
            autoIncrementarId(conexao, entidade);
            I id = incluir(conexao, entidade);
            conexao.getTransaction().commit();
            return id;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            conexao.getTransaction().rollback();
            return null;
        }
    }

    /**
     * Salvar um objeto no banco. Este método NÃO fará o autoincremento do @Id,
     * utilize caso seu objeto utilize chave composta.
     *
     * @param entidade objeto a ser inserido no banco
     * @return id do objeto inserido
     */
    public I incluir(T entidade) {
        EntityManager conexao = getEntityManager();
        try {
            conexao.getTransaction().begin();
            I id = incluir(conexao, entidade);
            conexao.getTransaction().commit();
            return id;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            conexao.getTransaction().rollback();
            return null;
        }
    }

    /**
     * Salvar um objeto no banco de forma transacional. Utilize este método caso
     * precise fazer várias alterações no banco na mesma transação. Será
     * necessário pegar a sessão e iniciar a transação manualmente, passar a
     * sessão para este método, depois commitar ou fazer rollback, e fechar a
     * sessão.
     *
     * @param conexao conexao do EntityManager para usar mesma sessão
     * @param entidade objeto a ser inserido no banco
     * @return id do objeto inserido
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public I incluir(EntityManager conexao, T entidade) throws IllegalArgumentException, IllegalAccessException {
        conexao.persist(entidade);
        conexao.flush();
        conexao.refresh(entidade);

        I idEntidade = null;
        Field fieldId = getIdField(entidade);
        if (fieldId != null) {
            idEntidade = (I) fieldId.get(entidade);
        }
        return idEntidade;
    }

    /**
     * Busca e preenche o próximo id para o objeto a ser salvo. Este método
     * utiliza a anotação @Id do objeto para saber qual é o atributo referente
     * ao id. Caso o id seja uma chave composta, esse método não funcionará.
     *
     * @param conexao conexao do EntityManager para usar mesma sessão
     * @param entidade objeto a ser inserido no banco
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public void autoIncrementarId(EntityManager conexao, T entidade) throws IllegalArgumentException, IllegalAccessException {
        Field fieldId = getIdField(entidade);
        if (fieldId != null) {
            Query q = conexao.createQuery("select coalesce(max(" + fieldId.getName() + "),0) + 1 as id from " + entidade.getClass().getName());
            I idAutoincremento = (I) q.getSingleResult();
            fieldId.set(entidade, idAutoincremento);
        }
    }

    /**
     * Retorna o atributo do objeto que tem a anotação @id. Utilizado para pegar
     * o id do objeto e realizar o autoincremento ou o seu valor.
     *
     * @param entidade objeto a ser buscado o atributo id
     * @return field que tem o @id anotado na classe modelo
     */
    private Field getIdField(T entidade) {
        Field fieldId = null;
        for (Field field : entidade.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Id.class) != null) {
                field.setAccessible(true);
                fieldId = field;
                break;
            }
        }
        return fieldId;
    }

    /**
     * Exclui um objeto do banco.
     *
     * @param entidade objeto a ser excluído
     * @return true caso sucesso, false caso ocorra algum erro
     */
    public boolean excluir(T entidade) {
        EntityManager conexao = getEntityManager();
        try {
            conexao.getTransaction().begin();
            excluir(conexao, entidade);
            conexao.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            conexao.getTransaction().rollback();
        }
        return false;
    }

    /**
     * Exclui um objeto do banco de forma transacional. Utilize este método caso
     * precise fazer várias alterações no banco na na mesma transação. Será
     * necessário pegar a sessão e iniciar a transação manualmente, passar a
     * sessão para este método, depois commitar ou fazer rollback, e fechar a
     * sessão.
     *
     * @param conexao conexao do EntityManager para usar mesma sessão
     * @param entidade objeto a ser excluído
     */
    public void excluir(EntityManager conexao, T entidade) {
        conexao.remove(conexao.contains(entidade) ? entidade : conexao.merge(entidade));
        conexao.flush();
    }

    /**
     * Exclui um objeto do banco passando um id. Utilize este método caso você
     * não tenho o objeto que veio do banco de dados, ou seja, saiba apenas a
     * classe e tenha o id.
     *
     * @param codigo id do objeto a ser excluído
     * @return true caso sucesso, false caso ocorra algum erro
     */
    public boolean excluir(I codigo) {
        EntityManager conexao = getEntityManager();
        try {
            conexao.getTransaction().begin();
            excluir(conexao, codigo);
            conexao.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            conexao.getTransaction().rollback();
        }
        return false;
    }

    /**
     * Exclui um objeto do banco de forma transacional, passando um id. Utilize
     * este método caso você não tenho o objeto que veio do banco de dados, ou
     * seja, saiba apenas a classe e tenha o id. Utilize este método caso
     * precise fazer várias alterações no banco na na mesma transação. Será
     * necessário pegar a sessão e iniciar a transação manualmente, passar a
     * sessão para este método, depois commitar ou fazer rollback, e fechar a
     * sessão.
     *
     * @param conexao conexao do EntityManager para usar mesma sessão
     * @param codigo id do objeto a ser excluído
     */
    public void excluir(EntityManager conexao, I codigo) {
        T genericClass = (T) conexao.find(classe, codigo);
        if (genericClass != null) {
            conexao.remove(conexao.contains(genericClass) ? genericClass : conexao.merge(genericClass));
        }
    }

    /**
     * Atualiza um objeto no banco.
     *
     * @param entidade objeto a ser atualizado
     * @return true caso sucesso, false caso ocorra algum erro
     */
    public boolean atualizar(T entidade) {
        EntityManager conexao = getEntityManager();
        try {
            conexao.getTransaction().begin();
            atualizar(conexao, entidade);
            conexao.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            conexao.getTransaction().rollback();
        }
        return false;
    }

    /**
     * Atualiza um objeto no banco de forma transacional. Utilize este método
     * caso precise fazer várias alterações no banco na na mesma transação. Será
     * necessário pegar a sessão e iniciar a transação manualmente, passar a
     * sessão para este método, depois commitar ou fazer rollback, e fechar a
     * sessão.
     *
     * @param conexao conexao do EntityManager para usar mesma sessão
     * @param entidade objeto a ser atualizado
     */

    public void atualizar(EntityManager conexao, T entidade) {
        conexao.merge(entidade);
        conexao.flush();
        conexao.refresh(entidade);
    }

    /**
     * Buscar um objeto no banco de dados.
     *
     * @param codigo id do objeto a ser encontrado
     * @return o objeto encontrado, deverá ser feito um cast para a sua classe
     * desejada
     */
    public T buscar(I codigo) {
        EntityManager conexao = getEntityManager();
        T object = conexao.find(classe, codigo);
        return object;
    }

    /**
     * Lista todos os objetos existentes no banco da classe desesejada.
     *
     * @return lista de objetos, deverá ser feito um cast de todos os objetos
     * para a sua classe desejada
     */
    public List<T> listar() {
        EntityManager conexao = getEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = conexao.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(classe);
            Root<T> root = criteriaQuery.from(classe);
            criteriaQuery.select(root);

            return conexao.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Lista os objetos da classe Curso do banco de dados baseado em um filtro e
     * ordem.
     *
     * @param filtro condições que irá no where, em hql
     * @param ordem condições que irá no order by, em hql
     * @return lista de objetos, deverá ser feito um cast de todos os objetos
     * para a sua classe desejada
     */
    public List<T> listar(String filtro, String ordem) {
        EntityManager conexao = getEntityManager();
        try {
            Query query = conexao.createQuery("from " + classe.getSimpleName() + " t where " + filtro + " " + ordem);

            //printQuery(query);
            List<T> objs = query.getResultList();
            return objs;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Lista os objetos da classe Curso do banaaco de dados baseado em um filtro e
     * ordem de forma paginada.
     *
     * @param filtro condições que irá no where, em hql
     * @param ordem condições que irá no order by, em hql
     * @param page é o offset, ou seja, inicio dos dados a serem buscados
     * @param pageSize é o limit, ou seja, quantidade de dados a serem buscados
     * @return lista de objetos, deverá ser feito um cast de todos os objetos
     * para a sua classe desejada
     */
    public List<T> listarPaginado(String filtro, String ordem, int page, int pageSize) {
        EntityManager conexao = getEntityManager();
        try {
            Query query = conexao.createQuery("from " + classe.getSimpleName() + " t where " + filtro + " " + ordem);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);

            //printQuery(query);
            List<T> objs = query.getResultList();
            return objs;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Quantidade de dados encontrados na tabela. DB2 COUNT aggregate function:
     * The result is a large integer. The result cannot be null. A large integer
     * is binary integer with a precision of 31 bits. The range is -2147483648
     * to +2147483647.
     *
     * @param filtro condições que irá no where, em hql
     * @return quantidade de dados encontrados
     */
    public int contar(String filtro) {
        EntityManager conexao = getEntityManager();
        try {
            Query query = conexao.createQuery("select count(*) from " + classe.getSimpleName() + " t where " + filtro);

            int count = ((Long) query.getSingleResult()).intValue();
            return count;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }

    /**
     * Executa um HQL.
     *
     * @param hql sql em hql a ser executado
     * @return lista de objetos
     */
    public List<Object[]> executarSelectHql(String hql) {
        EntityManager conexao = getEntityManager();
        try {
            Query query = conexao.createQuery(hql);

            //printQuery(query);
            List<Object[]> objs = query.getResultList();
            return objs;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Executa um select com SQL puro.
     *
     * @param sql sql puro a ser executado
     * @return lista de objetos
     */
    public List<Object[]> executarSelectSql(String sql) {
        EntityManager conexao = getEntityManager();
        try {
            Query query = conexao.createNativeQuery(sql);

            //printQuery(query);
            List<Object[]> objs = query.getResultList();
            return objs;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public int executarUpdateSql(String sql) {
        EntityManager conexao = getEntityManager();
        try {
            conexao.getTransaction().begin();
            int result = executarUpdateSql(conexao, sql);
            conexao.getTransaction().commit();
            return result;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            conexao.getTransaction().rollback();
            return 0;
        }
    }

    public int executarUpdateSql(EntityManager conexao, String sql) {
        try {
            int result = conexao.createNativeQuery(sql).executeUpdate();
            return result;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }

}

