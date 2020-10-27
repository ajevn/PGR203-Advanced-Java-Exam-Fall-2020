package no.kristiania.controllers;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.httpserver.HttpMessage;
import no.kristiania.httpserver.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectMemberPostController implements HttpController {
    public static final String CONNECTION_CLOSE = "Connection: close\r\n";
    private static final Logger logger = LoggerFactory.getLogger(ProjectMemberPostController.class);

    private ProjectMemberDao projectMemberDao;

    public ProjectMemberPostController (ProjectMemberDao projectMemberDao) {
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        ProjectMember member = new ProjectMember();
        member.setFirstName(requestParameter.getParameter("firstName"));
        member.setLastName(requestParameter.getParameter("lastName"));
        member.setEmail(requestParameter.getParameter("email"));
        projectMemberDao.insert(member);
        logger.info("Member: " + member.getFirstName() + ", " + member.getLastName() + " - " + member.getEmail() + " added successfully");

        String body = "Redirecting to main page...";
        String response = "HTTP/1.1 302 REDIRECT\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                CONNECTION_CLOSE +
                "Location: /index.html" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }
}
