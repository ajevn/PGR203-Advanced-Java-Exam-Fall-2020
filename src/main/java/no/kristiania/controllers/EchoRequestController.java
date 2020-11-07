package no.kristiania.controllers;

import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.HttpResponse;
import no.kristiania.httpserver.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class EchoRequestController implements HttpController{
    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String requestLine = request.getStartLine();
        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');

        //Handles echo requests - Example: localhost:8080/echo?body=hello
        String statusCode = "200";
        String body = "Hello <strong>World</strong>!";
        if (questionPos != -1) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                body = queryString.getParameter("body");
            }
        }
        HttpResponse response = new HttpResponse(statusCode, body);
        response.setContentType("text/plain");
        response.write(clientSocket);
    }
}
