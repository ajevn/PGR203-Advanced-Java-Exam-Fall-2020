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
import java.util.List;

public class ProjectMemberController implements HttpController {
    private ProjectMemberDao projectMemberDao;
    public static final String CONNECTION_CLOSE = "Connection: close\r\n";
    private static final Logger logger = LoggerFactory.getLogger(ProjectMemberController.class);

    public ProjectMemberController(ProjectMemberDao projectMemberDao) {
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {

        //REQUEST POST /api/newMember HTTP/1.1
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);
        //POST
        String requestMethod = requestLine.split(" ")[0];
            //If request is of method POST - run post method
            if (requestMethod.equals("POST")) {
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
                return;
            }

        // Else if request method is of type GET - Run get method
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
