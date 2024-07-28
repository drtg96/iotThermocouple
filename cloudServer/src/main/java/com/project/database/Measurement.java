package com.project.database;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * CREATE DATABASE thermaldb;
 * USE thermaldb;
 * CREATE TABLE meas_tbl
 * (
 *    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
 *    temperature FLOAT(25)  NOT NULL,
 *    dateTime DATETIME NOT NULL,
 *    PRIMARY  KEY (id)
 * );
 */

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

