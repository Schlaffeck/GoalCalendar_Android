package com.slamcode.goalcalendar.data.inmemory;

import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.data.Identifiable;
import com.slamcode.goalcalendar.data.Repository;
import com.slamcode.goalcalendar.data.model.CategoryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by moriasla on 23.12.2016.
 */

public class InMemoryRepositoryBase<ModelType extends Identifiable<IdentityType>,
        IdentityType extends Comparable<IdentityType>>
        implements Repository<ModelType, IdentityType> {

    private List<ModelType> inMemoryEntityList;

    public InMemoryRepositoryBase()
    {
        this.inMemoryEntityList = new ArrayList<>();
    }

    public InMemoryRepositoryBase(List<ModelType> inMemoryEntityList)
    {
        this.inMemoryEntityList = inMemoryEntityList;
    }

    protected List<ModelType> getInMemoryEntityList()
    {
        return this.inMemoryEntityList;
    }

    protected void setInMemoryEntityList(List<ModelType> entityList)
    {
        this.inMemoryEntityList = entityList;
    }

    @Override
    public ModelType findById(IdentityType comparable) {
        for (ModelType cm : this.inMemoryEntityList) {
            if (cm.getId().equals(comparable))
            {
                return cm;
            }
        }
        return null;
    }

    @Override
    public ModelType findFirst(Predicate<ModelType> predicate) {
        for (ModelType cm : this.inMemoryEntityList) {
            if (predicate.apply(cm))
            {
                return cm;
            }
        }
        return null;
    }

    @Override
    public List<ModelType> findAll() {
        return this.inMemoryEntityList;
    }

    @Override
    public void remove(ModelType categoryModel)
    {
        this.inMemoryEntityList.remove(categoryModel);
    }

    @Override
    public void add(ModelType categoryModel)
    {
        this.inMemoryEntityList.add(categoryModel);
    }

    @Override
    public List<ModelType> findMany(Predicate<ModelType> predicate) {

        List<ModelType> resultList = new ArrayList<ModelType>();

        for (ModelType cm : this.inMemoryEntityList) {
            if (predicate.apply(cm))
            {
                resultList.add(cm);
            }
        }
        return resultList;
    }
}
