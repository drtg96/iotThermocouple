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
            String insert = "INSERT INTO config_tbl (id, heatTemp, coolTemp, description) VALUES "
                + config.toString();
            System.out.println("insert CONFIG: " + config.toString());
            statement.execute(insert);
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());                                                                                                                
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
                        resultSet.getString("description"));
            }
            return config;
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
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
                        resultSet.getString("description"));
                configs.add(config);
            }
            return configs;
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
        }
        return null;
    }

    // thermaldb::config_tbl
    // READ (GET / QUERY)
    public static final Configuration getConfiguration(String description)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String query = "SELECT * FROM config_tbl where description = '" + description + "'";     
            ResultSet resultSet = statement.executeQuery(query);
            Configuration config = null;                    
            while (resultSet.next())                 
            {   
                config = new Configuration(resultSet.getLong("id"),
                        resultSet.getDouble("heatTemp"),
                        resultSet.getDouble("coolTemp"),
                        resultSet.getString("description"));
            }
            return config;
        } 
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
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
                + " heatTemp = '" + config.getHeatTemperature()
                + "', coolTemp = '" + config.getCoolTemperature()
                + "', description = '" + config.getDescription()
                + "' WHERE id = '" + config.getID() + "'";
            System.out.println("Update config: " + update);
            statement.execute(update);
        } 
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
            return "Update config_tbl failed\n";
        }
        return "Update config_tbl succeeded\n";
    }

    // thermaldb::config_tbl
    // DELETE (REMOVE/DELETE)
    public static final String deleteConfiguration(Long id)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String delete = "DELETE FROM config_tbl WHERE id='" + id.toString() + "';";
            System.out.println("Delete: " + delete);
            statement.execute(delete);
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
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
            String insert = "INSERT INTO status_tbl (state, dateTime) VALUES "
                + status.toString();
            System.out.println("Insert STATUS: " + insert);
            statement.execute(insert);
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
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
                status = new Status(resultSet.getBoolean("state"),
                        resultSet.getTimestamp("dateTime"));
            }
            return status;
        } 
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
        }
        return null;
    }

    // thermaldb::meas_tbl
    // CREATE (ADD/INSERT)
    public static final String insertMeasurement(Measurement measurement)
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String insert = "INSERT INTO meas_tbl (temp, dateTime) VALUES " 
                + measurement.toString();
            System.out.println("Insert MEAS: " + insert);
            statement.execute(insert);
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%\n", e.getSQLState(), e.getMessage());
            return "Insert meas_tbl failed\n";
        }
        return "Insert meas_tbl succeeded\n";
    }

    // thermaldb::meas_tbl
    // READ (GET/QUERY)
    public static final Measurement getMeasurement()
    {
        try (Connection connection = makeConnection();
                Statement statement = connection.createStatement();)
        {
            String query = "SELECT * FROM meas_tbl";
            ResultSet resultSet = statement.executeQuery(query);
            Measurement measurement = null;
            while(resultSet.next())
            {
                measurement = new Measurement(resultSet.getDouble("temp"));
            }
            return measurement;
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
        }
        return null;
    }

    // Helper function for making the conenction
    private static final Connection makeConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

