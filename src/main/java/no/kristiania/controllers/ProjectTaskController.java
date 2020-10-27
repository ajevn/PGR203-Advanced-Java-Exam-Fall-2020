package no.kristiania.controllers;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskController implements HttpController {
    private ProjectTaskDao projectTaskDao;
    public static final String CONNECTION_CLOSE = "Connection: close\r\n";
    private static final Logger logger = LoggerFactory.getLogger(ProjectTaskController.class);

    public ProjectTaskController(ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        //REQUEST POST /api/newTask HTTP/1.1
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);
        //POST
        String requestMethod = requestLine.split(" ")[0];
        //If request is of method POST - run post method
            if (requestMethod.equals("POST")) {
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
                        CONNECTION_CLOSE +
                        "Location: /index.html" +
                        "\r\n" +
                        body;
                clientSocket.getOutputStream().write(response.getBytes());
                return;
            }

        // Else if request method is of type GET - Run get method
        String body = "<ul>";
        for (ProjectTask task : projectTaskDao.list()) {
            body += "<li>" + task.getName() + " - " + task.getDescription() + " - " + "Status: " + task.getStatus() + "</li>";
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