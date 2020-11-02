package no.kristiania.controllers;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;
import no.kristiania.httpserver.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskOptionsController implements HttpController{
    private ProjectTaskDao taskDao;

    public ProjectTaskOptionsController(ProjectTaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "";
        for (ProjectTask task : taskDao.list()) {
            body += "<option value=" + task.getId() + ">" + task.getName() + "</option>";
        }
        return body;
    }
}