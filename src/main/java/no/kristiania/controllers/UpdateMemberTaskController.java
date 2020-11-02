package no.kristiania.controllers;

import no.kristiania.database.MemberTask;
import no.kristiania.database.MemberTaskDao;
import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateMemberTaskController implements HttpController {
    private MemberTaskDao memberTaskDao;
    private ProjectMemberDao projectMemberDao;

    public UpdateMemberTaskController(MemberTaskDao memberTaskDao, ProjectMemberDao projectMemberDao) {
        this.memberTaskDao = memberTaskDao;
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        int memberId = Integer.parseInt(requestParameter.getParameter("memberId"));
        int taskId = Integer.parseInt(requestParameter.getParameter("taskId"));
        ProjectMember projectMember = projectMemberDao.retrieve(memberId);

        memberId = projectMember.getId();

        MemberTask memberTask = new MemberTask(memberId, taskId);
        memberTaskDao.insert(memberTask);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/index.html");
        return redirect;
    }
}
