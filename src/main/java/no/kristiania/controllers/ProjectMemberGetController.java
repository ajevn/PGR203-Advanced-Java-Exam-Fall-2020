package no.kristiania.controllers;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.httpserver.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ProjectMemberGetController implements HttpController {
    public static final String CONNECTION_CLOSE = "Connection: close\r\n";
    private ProjectMemberDao projectMemberDao;

    public ProjectMemberGetController (ProjectMemberDao projectMemberDao) {
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        List<ProjectMember> memberList = projectMemberDao.list();
        String body = "<ul>";
        for (ProjectMember member : memberList) {
            body += "<li>" + "Name: <Strong>" +  member.getFirstName() + ", " + member.getLastName() + "</Strong> Email: <Strong>" + member.getEmail() + "</Strong></li>";
        }
        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                CONNECTION_CLOSE +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }
}
