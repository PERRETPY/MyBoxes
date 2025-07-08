package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidParameterException;
import fr.perretpy.myBoxes.exception.ResourceNotFoundException;
import fr.perretpy.myBoxes.model.GenericModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class GenericService<T extends GenericModel<T, ID>, ID> {

    protected final JpaRepository<T, ID> repository;

    public Page<T> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<T> get(ID id) {
        if (id == null) {
            throw new InvalidParameterException("ID null");
        }
        return this.repository.findById(id);
    }

    private T creater(T newObject) throws InvalidParameterException {
        if(newObject == null) {
            throw new InvalidParameterException("create object cannot be null");
        }
        newObject.setId(null);
        return newObject;
    }

    public T create(T newEntity) throws InvalidParameterException {
        return this.repository.save(creater(newEntity));
    }

    protected T createWithCreater(T newEntity, EntityCreateHandler<T> creater) {
        newEntity = creater(newEntity);
        creater.createFields(newEntity);
        return repository.save(newEntity);
    }

    public T update(ID id, T newEntity) throws InvalidParameterException{
        if(newEntity == null) {
            throw new InvalidParameterException("update object cannot be null");
        }
        if(id == null) {
            throw new InvalidParameterException("id cannot be null");
        }
        Optional<T> optionalEntity = repository.findById(id);
        T existedEntity;
        if(optionalEntity.isPresent()) {
            existedEntity = optionalEntity.get();
        } else {
            throw new InvalidParameterException("entity id doesn't exist");
        }
        newEntity.setId(existedEntity.getId());

        return repository.save(newEntity);
       }

    protected T updateWithUpdater(ID id, T newEntity, EntityUpdateHandler<T> updater) {
        return repository.findById(id)
                .map(existingEntity -> {
                    updater.updateFields(existingEntity, newEntity);
                    return repository.save(existingEntity);
                })
                .orElseThrow(() -> new InvalidParameterException("Entity with ID " + id + " doesn't exist"));
    }

    public void delete(ID id) {
        T object = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found"));
        repository.delete(object);
    }

    public abstract Page<T> search(String query, Pageable pageable);
}

