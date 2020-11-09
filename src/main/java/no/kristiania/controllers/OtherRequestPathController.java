package no.kristiania.controllers;

import no.kristiania.httpserver.HttpErrorMessage;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.HttpResponse;
import no.kristiania.httpserver.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class OtherRequestPathController implements HttpController{
    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String requestLine = request.getStartLine();
        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');
        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        //Handles echo requests - Example: localhost:8080/echo?body=hello
        String statusCode = "200";
        String body = "";
        if (questionPos != -1) {
             body += "<h2>Echo:</h2><h4> ";
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                body += queryString.getParameter("body");
            }
        } else {
            HttpErrorMessage errorMessage = new HttpErrorMessage(requestPath, 404, "Not Found");
            body += errorMessage.getErrorMessage();
        }
        body += "</h4>";
        HttpResponse response = new HttpResponse(statusCode, body);
        response.setContentType("text/html");
        response.write(clientSocket);
    }
}
