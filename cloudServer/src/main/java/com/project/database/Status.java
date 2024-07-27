package com.project.database;

import java.sql.Timestamp;

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
        return "('" + state.toString() + "', '" + dateTime.toString() + "')"; 
    }
}

