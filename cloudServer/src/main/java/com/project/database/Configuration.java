package com.project.database;

/**
 * CREATE DATABASE thermaldb;
 * USE thermaldb;
 * CREATE TABLE config_tbl
 * (
 *    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   heatTemp FLOAT(25)  NOT NULL,
 *    coolTemp FLOAT(25) NOT NULL,
 *    description VARCHAR(150),
 *    PRIMARY  KEY (id)
 * );
 */
public class Configuration implements Stored
{
    private Long id;
    private Double heatTemp;
    private Double coolTemp;
    private String desc;

    public Configuration(Long id, Double heatTemp, Double coolTemp, String desc)
    {
        this.id = id;
        this.heatTemp = heatTemp;
        this.coolTemp = coolTemp;
        this.desc = desc;
    }

    public Long getID() 
    {
        return id;
    }

    public void setID(Long id) 
    {
        this.id = id;
    }

    public Double getHeatTemperature()
    {
        return heatTemp;
    }

    public void setHeatTemperature(Double heatTemp)
    {
        this.heatTemp = heatTemp;
    }

    public Double getCoolTemperature()
    {
        return coolTemp;
    }

    public void setCoolTemperature(Double coolTemp)
    {
        this.coolTemp = coolTemp;
    }

    public String getDescription() 
    {
        return desc;
    }

    public void setDescription(String desc)
    {
        this.desc = desc;
    }

    public String toString()
    {
        return "('" + heatTemp.toString() + "', '" + coolTemp.toString() + "', '" + desc + "')";
    }
}

