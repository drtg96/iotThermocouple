package com.project.database;

import java.sql.Timestamp;

/**
 * CREATE DATABASE thermaldb;
 * USE thermaldb;
 * CREATE TABLE status_tbl
 * (
 *    state BOOLEAN NOT NULL,
 *    dateTime DATETIME NOT NULL,
 * );
 */

public class Status implements Stored
{
    private Boolean state;
    private Timestamp dateTime;

    public Status(Boolean state, Timestamp dateTime)
    {
        this.state = state;
        this.dateTime = dateTime; 
    }
    
    public Boolean getState()
    {
        return state;
    }

    public void setState(Boolean state)
    {
        this.state = state;
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
        return "(" + state.toString() + ", '" + dateTime.toString() + "')"; 
    }
}

