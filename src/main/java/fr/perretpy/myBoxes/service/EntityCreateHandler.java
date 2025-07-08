package fr.perretpy.myBoxes.service;

@FunctionalInterface
public interface EntityCreateHandler<T> {
    void createFields(T newEntity);
}
