package fr.perretpy.myBoxes.service;

@FunctionalInterface
public interface EntityUpdateHandler<T> {
    void updateFields(T existingEntity, T newEntity);
}