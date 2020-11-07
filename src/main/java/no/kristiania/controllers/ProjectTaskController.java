package no.kristiania.controllers;

import no.kristiania.database.*;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.HttpResponse;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectTaskController implements HttpController{
    private ProjectTaskDao projectTaskDao;
    private ProjectMemberDao projectMemberDao;
    private MemberTaskDao memberTaskDao;
    private static final Logger logger = LoggerFactory.getLogger(ProjectTaskController.class);

    public ProjectTaskController(ProjectTaskDao projectTaskDao, MemberTaskDao memberTaskDao, ProjectMemberDao projectMemberDao) {
        this.projectTaskDao = projectTaskDao;
        this.memberTaskDao = memberTaskDao;
        this.projectMemberDao = projectMemberDao;
    }

    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String requestLine = request.getStartLine();
        String requestMethod = requestLine.split(" ")[0];
        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');

        //If request is of method POST - Create new task
            if (requestMethod.equals("POST")) {
                QueryString requestParameter = new QueryString(request.getBody());

                ProjectTask task = new ProjectTask();
                task.setName(requestParameter.getParameter("taskName"));
                task.setDescription(requestParameter.getParameter("taskDescription"));
                task.setStatus(requestParameter.getParameter("taskStatus"));
                projectTaskDao.insert(task);
                logger.info("Task: " + task.getName() + " added successfully");

                HttpResponse response = new HttpResponse("302 Redirect");
                response.redirect("index.html");
                response.write(clientSocket);

                return;
            }

        // Else if request method is of type GET - Get tasks and list assigned members

        //In case both taskStatus and memberId is empty - render list with all tasks
        List<ProjectTask> taskList = projectTaskDao.list();
        String body = "<ul>";

            if (questionPos != -1) {
                QueryString requestParameter = new QueryString(requestTarget.substring(questionPos + 1));
            //If taskStatus is specified - Retrieves tasks by status - If no tasks are returned information message is displayed
                if (requestTarget.contains("taskStatus")) {
                    String filterStatus = requestParameter.getParameter("taskStatus");
                    if (!filterStatus.isEmpty()) {
                        if (filterStatus.contains("-")) {
                            filterStatus = filterStatus.replace("-", " ");
                        }
                        taskList = projectTaskDao.retrieveTaskByStatus(filterStatus);
                        if (taskList.size() < 1) {
                            body += "<h3> No tasks matching status: " + filterStatus;
                            }
                        }

                //If memberId is specified - Retrieves tasks by member - If no tasks are returned information message is displayed
                    } else if (requestTarget.contains("memberId")) {
                        String memberId = requestParameter.getParameter("memberId");
                        if (!memberId.isEmpty()) {
                            int memberIdParsed = Integer.parseInt(memberId);

                        //Accesses connecting entity MemberTaskDao to filter tasks assigned to this specific member
                            List<MemberTask> memberTasks = memberTaskDao.retrieveByMemberId(memberIdParsed);
                            taskList = new ArrayList<>();
                                for (MemberTask tasks : memberTasks){
                                    taskList.add(projectTaskDao.retrieve(tasks.getTaskId()));
                                }
                            if (taskList.size() < 1) {
                                body += "<h3> No tasks assigned to member: " + projectMemberDao.retrieve(memberIdParsed).getFirstName() + ", " + projectMemberDao.retrieve(memberIdParsed).getLastName();
                            }
                        }
                    }
                }

            //Runs retrieved tasks in method for listing assigned members to this task
        for (ProjectTask projectTask : taskList) {
            body = listAssignedMembers( projectTask, body);
        }
        body += "</ul>";

        HttpResponse response = new HttpResponse("200 Ok", body);
        response.write(clientSocket);
    }

    public String listAssignedMembers(ProjectTask projectTask, String body) throws SQLException {
                int taskId = projectTask.getId();
                // Retrieves list of member-task associations relevant to this taskId
                List<MemberTask> taskList = memberTaskDao.retrieveByTaskId(taskId);
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