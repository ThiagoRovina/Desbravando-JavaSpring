package com.javaspring.demo.dao;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.sql.Connection;
import java.sql.SQLException;


public class DAO {

    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;

    public static EntityManager getEntityManager() {
        try {
            if (entityManagerFactory == null) {
                System.out.println("Iniciando nova EntityManagerFactory!");
                entityManagerFactory = Persistence.createEntityManagerFactory(null);
            }
            if (entityManager == null) {
                System.out.println("Criando nova EntityManager!");
                entityManager = entityManagerFactory.createEntityManager();
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao obter o EntityManager: " + ExceptionUtils.getStackTrace(e));
        }
        return entityManager;
    }

    public static String getConnectionName() {

        String dbURL = "";

        EntityManager entityManager = DAO.getEntityManager();
        Session sessao = entityManager.unwrap(Session.class);

        ReturningWork<String> getConName;
        getConName = new ReturningWork<String>() {

            @Override
            public String execute(Connection cnctn) throws SQLException {
                return cnctn.getMetaData().getURL().toString();
            }

        };

        dbURL = sessao.doReturningWork(getConName);

        return dbURL;
    }


    public static void shutdown() {
        System.out.println("Encerrando EntityManager para fechar conexões!");
        entityManager.close();
        entityManagerFactory.close();
    }

}
