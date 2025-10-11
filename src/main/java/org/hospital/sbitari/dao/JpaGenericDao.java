package org.hospital.sbitari.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class JpaGenericDao<T, ID> implements GenericDao<T, ID> {

    // initialize lazily to avoid throwing during classloading (which caused NoClassDefFoundError)
    private static volatile EntityManagerFactory emf;

    private final Class<T> entityClass;

    public JpaGenericDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    private static synchronized void ensureEmf() {
        if (emf != null) return;
        try {
            emf = Persistence.createEntityManagerFactory("teleexpertisePU");
        } catch (Throwable t) {
            // log to console and throw a clear runtime exception
            System.err.println("[JpaGenericDao] failed to create EntityManagerFactory for 'teleexpertisePU': " + t);
            throw new IllegalStateException("Unable to initialize JPA EntityManagerFactory for persistence unit 'teleexpertisePU'", t);
        }
    }

    protected EntityManager em() {
        ensureEmf();
        return emf.createEntityManager();
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = em();
        try {
            T e = em.find(entityClass, id);
            return Optional.ofNullable(e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = em();
        try {
            String q = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(q, entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public T save(T entity) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.getTransaction().commit();
            return merged;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public T update(T entity) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.getTransaction().commit();
            return merged;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(T entity) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            T merged = em.merge(entity);
            em.remove(merged);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(ID id) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            T e = em.find(entityClass, id);
            if (e != null) em.remove(e);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
