package com.project.database;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * CREATE DATABASE thermaldb;
 * USE thermaldb;
 * CREATE TABLE meas_tbl
 * (
 *    temp FLOAT(25)  NOT NULL,
 *    dateTime DATETIME NOT NULL
 * );
 */

public class Measurement implements Stored
{
    private Double temp;
    private Timestamp dateTime;

    public Measurement(Double temp)
    {
        this.temp = temp;
        this.dateTime = Timestamp.from(Instant.now());
    }

    public Double getTemperature()
    {
        return temp;
    }

    public void setTemperature(Double temp)
    {
        this.temp = temp;
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
        return "('" + temp.toString() + "', '" + dateTime.toString() + "')";
    }
}
