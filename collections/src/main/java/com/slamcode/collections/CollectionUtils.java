package com.slamcode.collections;

import java.util.*;

public class CollectionUtils {

    public static <T> List<T> createList(int size, ElementCreator<T> creator)
    {
        List<T> result = new ArrayList<T>();
        for(int i = 0; i < size; i++)
        {
            T element = creator.Create(i, result);
            result.add(element);
        }

        return result;
    }

    public static <T> List<T> createList(T... elements)
    {
        List<T> result = new ArrayList<T>();
        for(T element : elements)
        {
            result.add(element);
        }

        return result;
    }

    public static <T> List<T> emptyList()
    {
        return new ArrayList<T>();
    }

    public static <ParentType,CollectionElementType> List<CollectionElementType> merge(Collection<ParentType> baseCollection,
                                                                                       ElementSelector<ParentType, Collection<CollectionElementType>> collectionSelector)
    {
        List<CollectionElementType> result = new ArrayList<>();

        for (ParentType baseObject : baseCollection) {
            result.addAll(collectionSelector.select(baseObject));
        }

        return result;
    }
}
