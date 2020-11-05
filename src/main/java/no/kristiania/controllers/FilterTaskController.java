package no.kristiania.controllers;

import no.kristiania.database.*;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class FilterTaskController implements HttpController {
    private ProjectTaskDao projectTaskDao;
    private ProjectMemberDao projectMemberDao;
    private MemberTaskDao memberTaskDao;
    private static final Logger logger = LoggerFactory.getLogger(no.kristiania.controllers.ProjectTaskController.class);

    public FilterTaskController(ProjectTaskDao projectTaskDao, MemberTaskDao memberTaskDao, ProjectMemberDao projectMemberDao) {
        this.projectTaskDao = projectTaskDao;
        this.memberTaskDao = memberTaskDao;
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        //REQUEST POST /api/tasks HTTP/1.1
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);
        //POST
        String requestMethod = requestLine.split(" ")[0];

        //If request is of method POST - run post method
        if (requestMethod.equals("POST")) {
            QueryString requestParameter = new QueryString(request.getBody());
            String filterStatus = requestParameter.getParameter("taskStatus");

            //Retrieving tasks by status from ProjectTaskDAO
            List<ProjectTask> filteredTasks = projectTaskDao.retrieveTaskByStatus(filterStatus);

            String body = "<ul>";
            for (ProjectTask task: filteredTasks){
                    body += "<li>" + "<Strong>Task: </Strong>" + task.getName() +
                            " <br> <Strong>Description:</Strong> " + task.getDescription() +
                            " <br> " + "<Strong>Status:</Strong> " + task.getStatus() +
                            "</li>";
                }

            body += "</ul>";

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "\r\n" +
                    body;

            clientSocket.getOutputStream().write(response.getBytes());
        }
    }
}


