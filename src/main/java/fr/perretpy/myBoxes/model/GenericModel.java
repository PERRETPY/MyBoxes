package fr.perretpy.myBoxes.model;

public interface GenericModel<T, ID> {
    ID getId();
    void setId(ID id);
}
