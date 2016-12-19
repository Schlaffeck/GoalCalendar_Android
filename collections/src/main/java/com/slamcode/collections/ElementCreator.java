package com.slamcode.collections;

import java.util.List;

/**
 * Created by moriasla on 19.12.2016.
 */

public interface ElementCreator<T> {

    T Create(int index, List<T> currentList);
}
