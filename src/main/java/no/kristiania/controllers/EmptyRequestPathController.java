package no.kristiania.controllers;

import no.kristiania.httpserver.HttpMessage;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class EmptyRequestPathController implements HttpController{
    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String requestLine = request.getStartLine();
        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');

    }
}
