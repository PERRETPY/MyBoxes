package fr.perretpy.myBoxes.service;

@FunctionalInterface
public interface EntityUpdater<T> {
    void updateFields(T existingEntity, T newEntity);
}
