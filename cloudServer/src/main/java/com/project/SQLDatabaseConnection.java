package com.project;

import com.project.database.Measurement;
import com.project.database.Status;
import com.project.database.Configuration;
import java.sql.*; // Statement, ResultSet, DriverManager, Connection etc.
import java.util.ArrayList;
import java.util.List;

/*
 * Implements CRUD model functionality for each of the database tables
 */
public class SQLDatabaseConnection
{
    // Connection string constants
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/thermaldb";
    private static final String USER = "root";
    private static final String PASSWORD = "2Super0Simple2Password4";

    // thermaldb::config_tbl
    // CREATE (ADD/INSERT)
    public static final String insertConfiguration(Configuration config)
    {               
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
//            String insert = "INSERT INTO config_tbl (heatTemp, coolTemp, desc) VALUES ('" +  + "')";
            String insert = "INSERT INTO config_tbl VALUES " + config.toString();
            statement.execute(insert);
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());                                                                                                                
            return "Insert failed\n";
        }           
        return "Insert succeeded\n";
     }   

    // thermaldb::config_tbl
    // READ (GET/QUERY)
    public static final Configuration getConfiguration(Long id)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String query = "SELECT * FROM config_tbl WHERE id=" + id.toString();
            ResultSet resultSet = statement.executeQuery(query);
            Configuration config = null;
            while (resultSet.next())
            {
                config = new Configuration(resultSet.getLong("id"),
                        resultSet.getDouble("heatTemp"),
                        resultSet.getDouble("coolTemp"),
                        resultSet.getString("desc"));
            }
            return config;
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return null;
    }

    // thermaldb::config_tbl
    // READ ALL (GET ALL / QUERY *)
    public static final List<Configuration> getAllConfigurations()
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            List<Configuration> configs = new ArrayList<>();
            String query = "SELECT * FROM config_tbl";
            
            ResultSet resultSet = statement.executeQuery(query);
            Configuration config;                    
            while (resultSet.next())                 
            {   
                config = new Configuration(resultSet.getLong("id"),
                        resultSet.getDouble("heatTemp"),
                        resultSet.getDouble("coolTemp"),
                        resultSet.getString("desc"));
                configs.add(config);
            }
            return configs;
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return null;
    }

    // thermaldb::config_tbl
    // READ (GET / QUERY)
    public static final Configuration getConfiguration(String desc)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String query = "SELECT * FROM config_tbl where desc = '" + desc + "'";     
            ResultSet resultSet = statement.executeQuery(query);
            Configuration config = null;                    
            while (resultSet.next())                 
            {   
                config = new Configuration(resultSet.getLong("id"),
                        resultSet.getDouble("heatTemp"),
                        resultSet.getDouble("coolTemp"),
                        resultSet.getString("desc"));
            }
            return config;
        } 
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return null;
    }

    // thermaldb::config_tbl
    // UPDATE (EDIT)
    public static final String updateConfiguration(Configuration config)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String update = "UPDATE config_tbl SET "
                + " heatTemp = " + config.getHeatTemperature()
                + ", coolTemp = " + config.getCoolTemperature()
                + ", desc = " + config.getDescription()
                + " WHERE id = " + config.getID();
            statement.execute(update);
        } 
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return "Update config_tbl failed\n";
        }
        return "Update config_tbl succeeded\n";
    }

    // thermaldb::config_tbl
    // DELETE (REMOVE/DELETE)
    public static final String deleteConfiguration(String id)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String delete = "DELETE FROM config_tbl WHERE id=" + id;
            statement.execute(delete);
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return "Delete failed\n";
        }
        return "Delete succeeded\n";
    }

    // thermaldb::status_tbl
    // CREATE (ADD / INSERT)
    public static final String insertStatus(Status status)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String insert = "INSERT INTO status_tbl VALUES " + status.toString();
            statement.execute(insert);
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return "Insert status_tbl failed\n";
        }
        return "Insert status_tbl succeeded\n";
    }

    // thermaldb::status_tbl
    // READ (GET / QUERY)
    public static final Status getStatus()
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String query = "SELECT * FROM status_tbl";
            ResultSet resultSet = statement.executeQuery(query);
            Status status = null;
            while (resultSet.next())
            {
                status = new Status(resultSet.getBoolean("state"), resultSet.getTimestamp("date"));
            }
            return status;
        } 
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return null;
    }

    // thermaldb::status_tbl
    // UPDATE (EDIT)
    public static final String updateStatus(Status status)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String update = "UPDATE status_tbl SET "
               + " state = " + status.getState()
               + ", date = " + status.getDateTime();
            statement.execute(update);
        }        
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return "Update status_tbl failed\n";
        }
        return "Update status_tbl succeeded\n";
    }
   
    // thermaldb::meas_tbl
    // CREATE (ADD/INSERT)
    public static final String insertMeasurement(Measurement measurement)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String insert = "INSERT INTO meas_tbl VALUES " + measurement.toString();
            statement.execute(insert);
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return "Insert meas_tbl failed\n";
        }
        return "Insert meas_tbl succeeded\n";
    }

    // thermaldb::meas_tbl
    // READ (GET/QUERY)
    public static final List<Measurement> getAllMeasurements()
    {
        List<Measurement> measurements = new ArrayList<>();
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String query = "SELECT * FROM meas_tbl";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next())
            {
                Measurement measurement = new Measurement(
                        resultSet.getLong("id"),
                        resultSet.getDouble("temp"));
                measurements.add(measurement);
            }
            return measurements;
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return null;
    }

    // Helper function for making the conenction
    private static final Connection makeConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

