package no.kristiania.controllers;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.HttpServer;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskPostController implements HttpController {
    private ProjectTaskDao projectTaskDao;
    private static final Logger logger = LoggerFactory.getLogger(ProjectTaskPostController.class);

    public ProjectTaskPostController (ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        ProjectTask task = new ProjectTask();
        task.setName(requestParameter.getParameter("taskName"));
        task.setDescription(requestParameter.getParameter("taskDescription"));
        task.setStatus(requestParameter.getParameter("taskStatus"));
        projectTaskDao.insert(task);
        logger.info("Task: " + task.getName() + " added successfully");

        String body = "Okay";
        String response = "HTTP/1.1 302 REDIRECT\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Location: /index.html" +
                "\r\n" +
                body;
        clientSocket.getOutputStream().write(response.getBytes());
    }
}