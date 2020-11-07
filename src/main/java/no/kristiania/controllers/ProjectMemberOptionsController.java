package no.kristiania.controllers;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.HttpResponse;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectMemberOptionsController implements HttpController {
    private ProjectMemberDao memberDao;

    public ProjectMemberOptionsController(ProjectMemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpResponse response = new HttpResponse("200 Ok", getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "";
        for (ProjectMember member : memberDao.list()) {
            body += "<option value=" + member.getId() + ">" + member.getFirstName() + ", " + member.getLastName() + "</option>";
        }
        return body;
    }
}
