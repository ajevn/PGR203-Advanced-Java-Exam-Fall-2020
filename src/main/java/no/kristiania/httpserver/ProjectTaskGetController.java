package no.kristiania.httpserver;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskGetController implements HttpController {
    private ProjectTaskDao projectTaskDao;

    public ProjectTaskGetController (ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (ProjectTask task : projectTaskDao.list()) {
            body += "<li>" + task.getName() + " - Status: " + task.getStatus() + "</li>";
        }

        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }
}