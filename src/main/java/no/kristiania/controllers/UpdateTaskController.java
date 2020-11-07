package no.kristiania.controllers;

import no.kristiania.database.ProjectTaskDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.HttpResponse;
import no.kristiania.httpserver.QueryString;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateTaskController implements HttpController{
    private ProjectTaskDao projectTaskDao;

    public UpdateTaskController(ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
            QueryString requestParameter = new QueryString(request.getBody());

            Integer taskId = Integer.parseInt(requestParameter.getParameter("taskId"));
            String newStatus = requestParameter.getParameter("taskStatus");

            projectTaskDao.updateTaskStatus(newStatus, taskId);

            HttpResponse response = new HttpResponse("302 Redirect");
            response.redirect("index.html");
            response.write(clientSocket);

            return;
        }
}
