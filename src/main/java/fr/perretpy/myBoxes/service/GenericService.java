package fr.perretpy.myBoxes.service;

import fr.perretpy.myBoxes.exception.InvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@RequiredArgsConstructor
public abstract class GenericService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    public T update(ID id, T newEntity, EntityUpdater<T> updater) {
        return repository.findById(id)
                .map(existingEntity -> {
                    updater.updateFields(existingEntity, newEntity);
                    return repository.save(existingEntity);
                })
                .orElseThrow(() -> new InvalidParameterException("Entity with ID " + id + " doesn't exist"));
    }
}

