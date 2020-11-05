package no.kristiania.controllers;

import no.kristiania.database.ProjectTaskDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.QueryString;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateTaskController implements HttpController{
    private ProjectTaskDao projectTaskDao;
    public static final String CONNECTION_CLOSE = "Connection: close\r\n";

    public UpdateTaskController(ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
            QueryString requestParameter = new QueryString(request.getBody());

            Integer taskId = Integer.parseInt(requestParameter.getParameter("taskId"));
            String newStatus = requestParameter.getParameter("taskStatus");

            projectTaskDao.updateTaskStatus(newStatus, taskId);

            String body = "Redirecting to main page...";
            String response = "HTTP/1.1 302 REDIRECT\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    CONNECTION_CLOSE +
                    "Location: /index.html" +
                    "\r\n" +
                    body;

            clientSocket.getOutputStream().write(response.getBytes());
            return;
        }
}
