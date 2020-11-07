package no.kristiania.controllers;

import no.kristiania.database.*;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilterTaskMemberController implements HttpController {
    private ProjectTaskDao projectTaskDao;
    private ProjectMemberDao projectMemberDao;
    private MemberTaskDao memberTaskDao;
    private static final Logger logger = LoggerFactory.getLogger(ProjectTaskController.class);

    public FilterTaskMemberController(ProjectTaskDao projectTaskDao, MemberTaskDao memberTaskDao, ProjectMemberDao projectMemberDao) {
        this.projectTaskDao = projectTaskDao;
        this.projectMemberDao = projectMemberDao;
        this.memberTaskDao = memberTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        //REQUEST POST /api/tasks HTTP/1.1
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);
        //POST
        String requestMethod = requestLine.split(" ")[0];
        QueryString requestParameter = new QueryString(request.getBody());

        String memberId = requestParameter.getParameter("memberId");

        //Retrieving tasks by status from ProjectTaskDAO
        String body = "<ul>";

        Integer parsedMemberId = Integer.parseInt(memberId);
        List<MemberTask> filteredMembers = memberTaskDao.retrieveByMemberId(parsedMemberId);
        List<ProjectTask> filteredTaskByMemberId = new ArrayList<>();

            for (MemberTask member : filteredMembers) {
                filteredTaskByMemberId.add(projectTaskDao.retrieve(member.getTaskId()));
            }

            for (ProjectTask task: filteredTaskByMemberId) {
                body += "<li>" + "<Strong>Task: </Strong>" + task.getName() +
                        " <br> <Strong>Description:</Strong> " + task.getDescription() +
                        " <br> " + "<Strong>Status:</Strong> " + task.getStatus() +
                        " <br> " + "<Strong>Assigned to: </Strong>" +
                        projectMemberDao.retrieve(parsedMemberId).getFirstName() +
                        ", " + projectMemberDao.retrieve(parsedMemberId).getLastName() +
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


