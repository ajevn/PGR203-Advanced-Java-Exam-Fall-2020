package no.kristiania.httpserver;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskPostController implements HttpController {
    private ProjectTaskDao projectTaskDao;

    public ProjectTaskPostController (ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        ProjectTask task = new ProjectTask();
        task.setName(requestParameter.getParameter("taskName"));
        task.setStatus(requestParameter.getParameter("taskStatus"));
        projectTaskDao.insert(task);

        String body = "Okay";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        clientSocket.getOutputStream().write(response.getBytes());
    }
}