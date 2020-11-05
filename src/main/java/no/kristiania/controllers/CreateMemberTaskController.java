package no.kristiania.controllers;

import no.kristiania.database.MemberTask;
import no.kristiania.database.MemberTaskDao;
import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class CreateMemberTaskController implements HttpController {
    private static final Logger logger = LoggerFactory.getLogger(CreateMemberTaskController.class);
    private static final String CONNECTION_CLOSE = "Connection: close\r\n";;
    private MemberTaskDao memberTaskDao;
    private ProjectMemberDao projectMemberDao;

    public CreateMemberTaskController(MemberTaskDao memberTaskDao, ProjectMemberDao projectMemberDao) {
        this.memberTaskDao = memberTaskDao;
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        int memberId = Integer.parseInt(requestParameter.getParameter("memberId"));
        int taskId = Integer.parseInt(requestParameter.getParameter("taskId"));
        ProjectMember projectMember = projectMemberDao.retrieve(memberId);

        memberId = projectMember.getId();

        if (checkIfAssignmentExists(memberId, taskId)){
            logger.warn("Member" + memberId + " already assigned to task " + taskId + ".");
        } else {
            MemberTask memberTask = new MemberTask(memberId, taskId);
            memberTaskDao.insert(memberTask);

            String response = "HTTP/1.1 302 REDIRECT\r\n" +
                    CONNECTION_CLOSE +
                    "Location: /index.html" +
                    "\r\n";

            clientSocket.getOutputStream().write(response.getBytes());
            return;
        }

        String body = "ERROR 422 Unprocessable Entity - Member Already Assigned to Task";
        String response = "HTTP/1.1 422 Unprocessable Entity\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                CONNECTION_CLOSE +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
        return;
    }
    private boolean checkIfAssignmentExists(Integer memberId, Integer taskId) throws SQLException {
        List<MemberTask> taskList = memberTaskDao.list();

        for (MemberTask task : taskList){
            if(task.getTaskId() == taskId && task.getMemberId() == memberId) {
                return true;
            }
        }
        return false;
    }
}
