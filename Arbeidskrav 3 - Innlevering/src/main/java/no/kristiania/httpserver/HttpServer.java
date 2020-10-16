package no.kristiania.httpserver;

import no.kristiania.database.ProjectMemberDao;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {

    private File contentRoot;
    public List<ProjectMember> projectMembers = new ArrayList<>();

    public HttpServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started at URL: https://localhost:8080/index.html");
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void handleRequest(Socket clientSocket) throws IOException, SQLException {

        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);

        String requestMethod = requestLine.split(" ")[0];

        String requestTarget = requestLine.split(" ")[1];


        int questionPos = requestTarget.indexOf('?');

        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        if (requestMethod.equals("POST")) {
            handlePostRequest(clientSocket, request);
        } else {
            if (requestPath.equals("/echo")) {
                handleEchoRequest(clientSocket, requestTarget, questionPos);
            } else if (requestPath.equals("/api/products")) {
                handleGetProducts(clientSocket);
            } else {
                File file = new File(contentRoot, requestPath);
                if (!file.exists()) {
                    String body = file + " does not exist";
                    String response = "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: " + body.length() + "\r\n" +
                            "\r\n" +
                            body;

                    clientSocket.getOutputStream().write(response.getBytes());
                    return;
                }
                String statusCode = "200";
                String contentType = "text/plain";
                if (file.getName().endsWith(".html")) {
                    contentType = "text/html";
                } else if(file.getName().endsWith(".css")){
                    contentType = "text/css";
                }
                String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                        "Content-Length: " + file.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "\r\n";

                clientSocket.getOutputStream().write(response.getBytes());

                new FileInputStream(file).transferTo(clientSocket.getOutputStream());
            }
        }
    }

    private void handlePostRequest(Socket clientSocket, HttpMessage request) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        String email = requestParameter.getParameter("email");
        if(email.contains("%40")){
            // Splitting submitted email string to replace %40 with @
            int indexPos = email.indexOf("%");
            String sub1 = email.substring(0, indexPos);
            String sub2 = email.substring(indexPos + 2, email.length());
            String emailParsed = sub1 + "@" + sub2;

            String firstName = requestParameter.getParameter("firstName");
            String lastName = requestParameter.getParameter("lastName");
            ProjectMember member = createMember(firstName, lastName, emailParsed);
            //ProjectMemberDao.insert(firstName, lastName, emailParsed);
            System.out.println("Member: " + member.getFirstName() + ", " + member.getLastName() + " - " + member.getEmail() + " added successfully");
        }


        String body = "Okay";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    private ProjectMember createMember(String firstName, String lastName, String email){
        ProjectMember newMember = new ProjectMember(firstName, lastName, email);
        return newMember;
    }
    private void handleGetProducts(Socket clientSocket) throws IOException {
        String body = "<ul>";
        for (ProjectMember member : projectMembers) {
            body += "<li>" + "Name: " +  member.getFirstName() + ", " + member.getLastName() + " Email: " + member.getEmail() + "</li>";
            System.out.println(member);
        }
        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    private void handleEchoRequest(Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String statusCode = "200";
        String body = "Hello <strong>World</strong>!";
        if (questionPos != -1) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                body = queryString.getParameter("body");
            }
        }
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("user_db");
        // TODO: database passwords should never be checked in!
        dataSource.setPassword("asdqwe123");

        HttpServer server = new HttpServer(8080);
        server.setContentRoot(new File("src/main/resources"));

    }

    public void setContentRoot(File contentRoot) {
        this.contentRoot = contentRoot;
    }

    public List<ProjectMember> getProjectMembers() {
        return projectMembers;
    }
}