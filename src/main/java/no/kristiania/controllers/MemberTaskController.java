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

public class MemberTaskController implements HttpController{
    private MemberTaskDao memberTaskDao;
    public static final String CONNECTION_CLOSE = "Connection: close\r\n";
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

            String body = "Redirecting to main page...";
            String response = "HTTP/1.1 302 REDIRECT\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    CONNECTION_CLOSE +
                    "Location: /index.html" +
                    "\r\n" +
                    body;

            clientSocket.getOutputStream().write(response.getBytes());
            return;
        }

        // Else if request method is of type GET - Run get method
        List<MemberTask> memberTaskList = memberTaskDao.list();
        String body = "<option>";
        for (MemberTask memberTask : memberTaskList) {
            body += "Member Id: <Strong>" +  memberTask.getMemberId() + " - Task Id: " + memberTask.getTaskId() + "</Strong>";
        }
        body += "</option>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                CONNECTION_CLOSE +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());

    }
}
