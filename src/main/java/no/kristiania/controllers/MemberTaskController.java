package no.kristiania.controllers;

import no.kristiania.database.MemberTask;
import no.kristiania.database.MemberTaskDao;
import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.HttpResponse;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class MemberTaskController implements HttpController{
    private MemberTaskDao memberTaskDao;
    private static final Logger logger = LoggerFactory.getLogger(ProjectMemberController.class);

    public MemberTaskController(MemberTaskDao memberTaskDao) {
        this.memberTaskDao = memberTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {

        //REQUEST POST /api/newMemberTask HTTP/1.1
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);
        //POST
        String requestMethod = requestLine.split(" ")[0];
        //If request is of method POST - run post method
        if (requestMethod.equals("POST")) {
            QueryString requestParameter = new QueryString(request.getBody());

            MemberTask memberTask = new MemberTask();
            memberTask.setTaskId(Integer.parseInt(requestParameter.getParameter("memberId")));
            memberTask.setMemberId(Integer.parseInt(requestParameter.getParameter("taskId")));
            memberTaskDao.insert(memberTask);
            logger.info("Member: " + memberTask.getMemberId() +  " - " + memberTask.getTaskId() + " added successfully");

            HttpResponse response = new HttpResponse("302 Redirect");
            response.redirect("index.html");
            response.write(clientSocket);

            return;
        }

        // Else if request method is of type GET - Run get method
        List<MemberTask> memberTaskList = memberTaskDao.list();
        String body = "<option>";
        for (MemberTask memberTask : memberTaskList) {
            body += "Member Id: <Strong>" +  memberTask.getMemberId() + " - Task Id: " + memberTask.getTaskId() + "</Strong>";
        }
        body += "</option>";

        HttpResponse response = new HttpResponse("200 Ok", body);
        response.write(clientSocket);
    }
}
