package com.slamcode.goalcalendar.data.model.backup;

import com.slamcode.goalcalendar.data.Identifiable;

import java.util.UUID;

public class BackupSourceModel implements Identifiable<UUID> {

    private UUID id;

    private String name;

    private String description;

    @Override
    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
