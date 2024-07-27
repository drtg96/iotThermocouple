package com.project.database;

import java.sql.Timestamp;
import java.time.Instant;

public class Measurement implements Stored
{
    private Long id;
    private Double temperature;
    private Timestamp dateTime;

    public Measurement(Long id, Double temperature)
    {
        this.id = id;
        this.temperature = temperature;
        this.dateTime = Timestamp.from(Instant.now());
    }

    public Long getID()
    {
        return id;
    }

    public void setID(Long id)
    {
        this.id = id;
    }

    public Double getTemperature()
    {
        return temperature;
    }

    public void setTemperature(Double temperature)
    {
        this.temperature = temperature;
    }

    public Timestamp getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime)
    {
        this.dateTime = dateTime;
    }

    public String toString()
    {
        return "('" + temperature.toString() + "', '" + dateTime.toString() + "')";
    }
}

