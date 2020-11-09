package no.kristiania.controllers;

import no.kristiania.database.*;
import no.kristiania.httpserver.messages.HttpErrorMessage;
import no.kristiania.httpserver.messages.HttpMessage;
import no.kristiania.httpserver.messages.HttpResponse;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class MemberTaskController implements HttpController {
    private static final Logger logger = LoggerFactory.getLogger(MemberTaskController.class);
    private final MemberTaskDao memberTaskDao;
    private final ProjectMemberDao projectMemberDao;

    public MemberTaskController(MemberTaskDao memberTaskDao, ProjectMemberDao projectMemberDao) {
        this.memberTaskDao = memberTaskDao;
        this.projectMemberDao = projectMemberDao;
    }


    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        //Handles connecting entity insert consisting of MemberId and memberTask
        int memberId = Integer.parseInt(requestParameter.getParameter("memberId"));
        int taskId = Integer.parseInt(requestParameter.getParameter("taskId"));
        ProjectMember projectMember = projectMemberDao.retrieve(memberId);

        memberId = projectMember.getId();

        //Validates input and checks if member is already assigned to this taskId
        if (checkIfAssignmentExists(memberId, taskId)) {
            logger.warn("Member" + memberId + " already assigned to task " + taskId + ".");
        } else {
            MemberTask memberTask = new MemberTask(memberId, taskId);
            memberTaskDao.insert(memberTask);

            HttpResponse response = new HttpResponse("302 Redirect");
            response.redirect("index.html");
            response.write(clientSocket);

            return;
        }
        // If validation fails and user is already assigned - Displays a 422 Error message informing the user of this.
        HttpErrorMessage errorMessage = new HttpErrorMessage(422, "Unprocessable Entity", "Member Already Assigned to this Task");
        String body = errorMessage.getInfoMessage();
        HttpResponse response = new HttpResponse("422 Unprocessable Entity", body);
        response.write(clientSocket);
    }

    public boolean checkIfAssignmentExists(Integer memberId, Integer taskId) throws SQLException {
        List<MemberTask> taskList = memberTaskDao.list();

        for (MemberTask task : taskList) {
            if (task.getTaskId() == taskId && task.getMemberId() == memberId) {
                return true;
            }
        }
        return false;
    }
}
