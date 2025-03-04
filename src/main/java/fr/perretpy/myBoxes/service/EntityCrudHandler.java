package fr.perretpy.myBoxes.service;

@FunctionalInterface
public interface EntityCrudHandler<T> {
    void updateFields(T existingEntity, T newEntity);
}
