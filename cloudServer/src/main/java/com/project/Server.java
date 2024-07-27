package com.project;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import java.io.IOException;

public class Server extends NanoHTTPD
{
    public Server() throws IOException
    {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static void main(String[] args)
    {
        try
        {
            new Server();
        }
        catch (IOException e)
        {
            System.err.println("Couldn't start server:\n" + e);
        }
    }

    @Override
    public Response serve(IHTTPSession session)
    {
        if (session.getMethod() == Method.GET)
        {
            return CurlUtils.performGet(session);
        }
        else if (session.getMethod() == Method.POST)
        {
            return CurlUtils.performPost(session);
        }
        else if (session.getMethod() == Method.PUT) 
        {
            return CurlUtils.performPost(session);
        }
        else if (session.getMethod() == Method.DELETE) 
        {
            return CurlUtils.performDelete(session);
        }
        return CurlUtils.failedAttempt("Unknown request type!");
    }
}

