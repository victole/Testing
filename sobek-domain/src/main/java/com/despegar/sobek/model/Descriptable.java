package com.despegar.sobek.model;

import java.util.Set;

public interface Descriptable<D extends AbstractDescriptionI18N> {

    public Set<D> getDescriptions();

    public void setDescriptions(Set<D> descriptions);
}
