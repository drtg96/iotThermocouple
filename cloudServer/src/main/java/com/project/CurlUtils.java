package com.project;

import com.google.gson.Gson;
import com.project.database.*;
import java.sql.*;
import java.lang.System;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;
import static com.project.SQLDatabaseConnection.insertMeasurement;
import static com.project.SQLDatabaseConnection.insertStatus;
import static com.project.SQLDatabaseConnection.updateConfiguration;

public final class CurlUtils
{
    public static NanoHTTPD.Response performGet(NanoHTTPD.IHTTPSession session) 
    {
        String jsonResponse = null;
        String tblRoute = getRoute(session.getUri());
        String input = cleanValue(session.getUri());
        Gson gson = new Gson();

        if (tblRoute != null)
        {
            switch(tblRoute)
            {
                case CONFIGURATION_TBL:
                    if (input != null && !input.equals(""))
                    {
                        Long id = Long.parseLong(input);
                        Configuration config = SQLDatabaseConnection.getConfiguration(id);
                        if (config == null)
                        {
                            return failedAttempt("Config value was null!");
                        }
                        jsonResponse = gson.toJson(config);
                    }
                    else
                    {
                        List<Configuration> configs = SQLDatabaseConnection.getAllConfigurations();
                        if (configs.isEmpty())
                        {
                            return failedAttempt("Get query returned empty!");
                        }
                        jsonResponse = gson.toJson(configs);
                    }
                    break;
           
                case STATUS_TBL:
                    Status status = SQLDatabaseConnection.getStatus();
                    if (status == null) 
                    {
                        return failedAttempt("Status value was null!");
                    }
                    jsonResponse = gson.toJson(status);
                    break;
               
                case MEASUREMENT_TBL:
                    List<Measurement> measurements = SQLDatabaseConnection.getAllMeasurements();
                    if (measurements.isEmpty())
                    {
                        return failedAttempt("Get query returned empty!");
                    }
                    jsonResponse = gson.toJson(measurements);
                    break;
               
                default:
                    return failedAttempt("Bad route in URL!");
            }
            return newFixedLengthResponse(jsonResponse);
        }
        return failedAttempt("No table!");
    }

    public static NanoHTTPD.Response performPost(NanoHTTPD.IHTTPSession session)
    {
        try
        {
            String response = null;
            session.parseBody(new HashMap<>());
            Stored stored = parseTableData(session.getUri().replace("/", ""), session.getQueryParameterString());

            if (stored == null)
            {
                return failedAttempt("DB read failed!");
            }

            if (stored instanceof Configuration)
            {
                response = SQLDatabaseConnection.updateConfiguration((Configuration) stored);
            }
            else if (stored instanceof Status)
            {
                response = SQLDatabaseConnection.insertStatus((Status) stored);
            }
            else if (stored instanceof Measurement) 
            {
                response = handleTemperatureChange((Measurement) stored) + "/n";
                response += SQLDatabaseConnection.insertMeasurement((Measurement) stored);
            }

            return newFixedLengthResponse(response);
        }
        catch (IOException | NanoHTTPD.ResponseException e)
        {
            return failedAttempt("Post unsuccessful!");
        }
    }

    public static NanoHTTPD.Response performDelete(NanoHTTPD.IHTTPSession session) 
    {
        String response = null;
        String table = session.getUri().replace("/", "");
        String index = cleanValue(session.getUri());
        switch (table)
        {
            case CONFIGURATION_TBL:
            case MEASUREMENT_TBL:
               response = SQLDatabaseConnection.deleteConfiguration(index);
               return newFixedLengthResponse(response); 
            default:
               return failedAttempt("Delete unsuccessful");
        }
    }

    // Helper functions
    public static NanoHTTPD.Response failedAttempt(String msg)
    {
        return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, MIME_PLAINTEXT, msg);
    }

    private static Stored parseTableData(String table, String row)
    {
        String[] entries = row.split(",");
        switch (table)
        {
            case CONFIGURATION_TBL:
                Long config_id = Long.parseLong(entries[0]);
                Double heatTemp = Double.parseDouble(entries[1]);
                Double coolTemp = Double.parseDouble(entries[2]);
                String desc = entries[3];
                return new Configuration(config_id, heatTemp, coolTemp, desc);

            case MEASUREMENT_TBL:
                Long meas_id = Long.parseLong(entries[0]);
                Double temp = Double.parseDouble(entries[1]);
                return new Measurement(meas_id, temp);
            default:
                return null;
        }        
    }

    private static String parseDescription()
    {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 19)
        {
            return "NIGHT";
        }
        else if (hour >= 12)
        {
            return "MIDDAY";
        }
        return "MORNING";
    }

    private static Configuration getConfigurationFromDescription()
    {
        return SQLDatabaseConnection.getConfiguration(parseDescription());
    }

    private static String handleTemperatureChange(Measurement measurement)
    {
        Configuration config = getConfigurationFromDescription();
        double temp = measurement.getTemperature();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        
        if (temp > config.getCoolTemperature())
        {
            // Turn heater off
            return SQLDatabaseConnection.updateStatus(new Status(false, ts));
        }
        else if (temp < config.getHeatTemperature())
        {
            //turn heater on
            return SQLDatabaseConnection.updateStatus(new Status(true, ts));
        }

        // no change
        return null;
    }

    private static String cleanValue(String input)
    {
        return input.replaceAll("[^0-9]", "");
    }

    private static String getRoute(String input)
    {
        if (input.contains(MEASUREMENT_TBL))
        {
            return MEASUREMENT_TBL;
        }
        else if (input.contains(STATUS_TBL))
        {
            return STATUS_TBL;
        }
        else if (input.contains(CONFIGURATION_TBL))
        {
            return CONFIGURATION_TBL;
        }
        return null;
    }

    // Class Constants
    private static final String STATUS_TBL = "status_tbl";
    private static final String CONFIGURATION_TBL = "config_tbl";
    private static final String MEASUREMENT_TBL = "meas_tbl";
}

