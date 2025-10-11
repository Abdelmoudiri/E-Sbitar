package org.hospital.sbitari.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    void deleteById(ID id);
}
