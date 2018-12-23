package com.slamcode.collections;

import java.util.*;

import javax.xml.bind.Element;

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

    @SafeVarargs
    public static <T> List<T> createList(T... elements)
    {
        List<T> result = new ArrayList<T>();
        Collections.addAll(result, elements);

        return result;
    }

    public static <T> List<T> createList(Iterable<T> elements)
    {
        List<T> result = new ArrayList<T>();
        for(T elem : elements)
        {
            result.add(elem);
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

    public static <ParentType, SelectedElementType> List<SelectedElementType> select(Collection<ParentType> baseCollection,
                                                                                     ElementSelector<ParentType, SelectedElementType> elementSelector)
    {
        List<SelectedElementType> result = new ArrayList<>();

        for (ParentType baseObject : baseCollection) {
            result.add(elementSelector.select(baseObject));
        }

        return result;
    }

    public static <ParentType> int sum(Iterable<ParentType> iterable, ElementSelector<ParentType, Integer> summableSelector) {
        int result = 0;

        for (ParentType item : iterable) {
            Integer selected = summableSelector.select(item);

                result += selected;
        }

        return result;
    }

    public static <ParentType> ParentType singleOrDefault(Iterable<ParentType> iterable, ElementSelector<ParentType, Boolean> elementPredicate)
    {
        ParentType result = null;
        for(ParentType item : iterable)
        {
            if(elementPredicate.select(item))
            {
                if(result != null)
                    return null;

                result = item;
            }
        }

        return result;
    }

    public static <ParentType, Comparable extends java.lang.Comparable<Comparable>> ParentType firstMax(Iterable<ParentType> iterable, ElementSelector<ParentType, Comparable> comparableSelector)
    {
        ParentType result = null;
        Comparable comparableResult = null;
        for(ParentType item : iterable)
        {
            Comparable itemComparable =  comparableSelector.select(item);
            if(itemComparable != null && (comparableResult == null || comparableResult.compareTo(itemComparable) > 0))
            {
                result = item;
                comparableResult = itemComparable;
            }
        }

        return result;
    }

    public static <ParentType, Comparable extends java.lang.Comparable<Comparable>> ParentType firstMin(Iterable<ParentType> iterable, ElementSelector<ParentType, Comparable> comparableSelector)
    {
        ParentType result = null;
        Comparable comparableResult = null;
        for(ParentType item : iterable)
        {
            Comparable itemComparable =  comparableSelector.select(item);
            if(itemComparable != null && (comparableResult == null || comparableResult.compareTo(itemComparable) < 0))
            {
                result = item;
                comparableResult = itemComparable;
            }
        }

        return result;
    }

    public static <Element> Iterable<Element> filter(Iterable<Element> iterable, Predicate<Element> predicate)
    {
        List<Element> resultList = new ArrayList<Element>();

        for (Element cm : iterable) {
            if (predicate.apply(cm))
            {
                resultList.add(cm);
            }
        }
        return resultList;
    }
}
