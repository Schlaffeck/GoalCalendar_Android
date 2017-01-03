package com.slamcode.collections;

/**
 * Created by moriasla on 03.01.2017.
 */

public interface ElementSelector<ParentType, SelectedElementType> {

    SelectedElementType select(ParentType parent);
}
