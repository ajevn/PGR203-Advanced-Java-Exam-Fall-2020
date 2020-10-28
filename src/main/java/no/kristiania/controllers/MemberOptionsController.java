package no.kristiania.controllers;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.httpserver.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class MemberOptionsController implements HttpController{
    private ProjectMemberDao memberDao;

    public MemberOptionsController(ProjectMemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
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
