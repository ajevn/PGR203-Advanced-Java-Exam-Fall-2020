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

public class FilterTaskStatusController implements HttpController {
    private ProjectTaskDao projectTaskDao;
    private ProjectMemberDao projectMemberDao;
    private MemberTaskDao memberTaskDao;
    private static final Logger logger = LoggerFactory.getLogger(no.kristiania.controllers.ProjectTaskController.class);

    public FilterTaskStatusController(ProjectTaskDao projectTaskDao, MemberTaskDao memberTaskDao, ProjectMemberDao projectMemberDao) {
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

        String filterStatus = requestParameter.getParameter("taskStatus");

        List<ProjectTask> filteredTasks;
        filteredTasks = projectTaskDao.retrieveTaskByStatus(filterStatus);
        //Retrieving tasks by status from ProjectTaskDAO
        String body = "<ul>";

            for (ProjectTask task: filteredTasks){
                body += listAssignedMembers(task, body);
            }

        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public String listAssignedMembers(ProjectTask projectTask, String body) throws SQLException {
        int taskId = projectTask.getId();
        // Retrieves list of member-task associations relevant to this taskId
        List<MemberTask> taskList = memberTaskDao.list();
        // Filtering out tasks with ID similar to current iteration of outer for-loop
        ArrayList<ProjectMember> filteredMembersByTask = new ArrayList<>();

        for (MemberTask task : taskList){
            if(task.getTaskId() == (taskId)) {
                int memberId = task.getMemberId();
                filteredMembersByTask.add(projectMemberDao.retrieve(memberId));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (ProjectMember member : filteredMembersByTask){
            sb.append(member.getFirstName() + ", " + member.getLastName() + " <br> ");
        }

        body += "<li>" + "<Strong>Task: </Strong>" + projectTask.getName()+ " <br> <Strong>Description:</Strong> " + projectTask.getDescription() + " <br> " + "<Strong>Status:</Strong> " + projectTask.getStatus() +
                "<br> <Strong>Assigned to:<br></Strong> " +
                sb +
                "</li>";
        return body;
    }
}


