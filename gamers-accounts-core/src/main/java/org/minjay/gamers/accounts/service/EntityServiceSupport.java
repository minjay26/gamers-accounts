package org.minjay.gamers.accounts.service;

import org.minjay.gamers.accounts.data.domain.AbstractEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Abstract Class for generic CRUD operations on a service for a specific type.
 */
@Transactional
public abstract class EntityServiceSupport<T extends AbstractEntity<ID>, ID extends Serializable, TRepository extends CrudRepository<T, ID>>
        implements EntityService<T, ID> {

    private final TRepository repository;

    private Class<T> entityClass;

    /**
     * Gets the entity class.
     *
     * @return the entity class
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * Create a new EntityServiceSupport instance.
     *
     * @param repository
     */
    @SuppressWarnings("unchecked")
    protected EntityServiceSupport(TRepository repository) {
        this.repository = repository;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Create a new EntityServiceSupport instance.
     *
     * @param repository
     * @param entityClass
     */
    protected EntityServiceSupport(TRepository repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
    }

    /**
     * Gets the specific repository.
     *
     * @return the specific repository
     */
    protected TRepository getRepository() {
        return this.repository;
    }

    public <S extends T> S save(S entity) {
        return repository.save(entity);
    }

    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    public T findById(ID id) {
        return findById(id, false);
    }

    private T findById(ID id, boolean required) {
        T entity = repository.findById(id).orElse(null);
        if (required && entity == null) {
            throw new EntityNotFoundException(String.format("No %s entity with id %s exists!",
                    entityClass, id));
        }
        return entity;
    }

    @Transactional(readOnly = true)
    public T getById(ID id) {
        return findById(id, true);
    }

    @Transactional(readOnly = true)
    public boolean exists(ID id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public Iterable<T> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Iterable<T> getAll(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    public T delete(ID id) {
        T entity = getById(id);
        repository.delete(entity);
        return entity;
    }

    public void delete(T entity) {
        repository.delete(entity);
    }

    public void delete(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
