package no.kristiania.httpserver;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.database.ProjectTaskDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private Map<String, HttpController> controllers;

    public static final String CONNECTION_CLOSE = "Connection: close\r\n";
    private ProjectMemberDao projectMemberDao;
    private ServerSocket serverSocket;

    public HttpServer(int port, DataSource dataSource) throws IOException {
        projectMemberDao = new ProjectMemberDao(dataSource);
        ProjectTaskDao projectTaskDao = new ProjectTaskDao(dataSource);

        controllers = Map.of(
                "/api/newCategory", new ProjectTaskGetController(projectTaskDao),
                "/api/categories", new ProjectTaskGetController(projectTaskDao)
        );


        ServerSocket serverSocket = new ServerSocket(port);
        new Thread(() -> {
            while (true) {
                try (Socket clientSocket = serverSocket.accept()){
                    handleRequest(clientSocket);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
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
            if(requestPath.equals("/api/newWorker")) {
                handlePostRequest(clientSocket, request);
            } else {
                getController(requestPath).handle(request, clientSocket);
            }
        } else {
            if (requestPath.equals("/echo")) {
                handleEchoRequest(clientSocket, requestTarget, questionPos);
            } else if (requestPath.equals("/api/products")) {
                handleGetProducts(clientSocket);
            } else {
                HttpController controller = controllers.get(requestPath);
                if(controller != null){
                    controller.handle(request, clientSocket);
                } else {
                    handleFileRequest(clientSocket, requestPath);
                }
            }
        }
    }

    private HttpController getController(String requestPath) {
        return controllers.get(requestPath);
    }

    private void handleFileRequest(Socket clientSocket, String requestPath) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(requestPath)) {
            if(inputStream == null){
                String body = requestPath + " does not exist";
                String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        CONNECTION_CLOSE +
                        "\r\n" +
                        body;

                clientSocket.getOutputStream().write(response.getBytes());
                return;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            } else if(requestPath.endsWith(".css")){
                contentType = "text/css";
            }

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    CONNECTION_CLOSE +
                    "\r\n";

            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
        }
    }

    public void handlePostRequest(Socket clientSocket, HttpMessage request) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        String firstName = requestParameter.getParameter("firstName");
        String lastName = requestParameter.getParameter("lastName");
        String email = requestParameter.getParameter("email");

        // Decoding to UTF-8 to allow "æøå, @" and other characters
        String decodedEmail = URLDecoder.decode(email, "UTF-8");
        String decodedFirstName = URLDecoder.decode(firstName, "UTF-8");
        String decodedLastName = URLDecoder.decode(lastName, "UTF-8");


        ProjectMember member = createMember(decodedFirstName, decodedLastName, decodedEmail);
        projectMemberDao.insert(member);
        System.out.println("Member: " + member.getFirstName() + ", " + member.getLastName() + " - " + member.getEmail() + " added successfully");


        String body = "Okay";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                CONNECTION_CLOSE +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    private ProjectMember createMember(String firstName, String lastName, String email){
        ProjectMember newMember = new ProjectMember(firstName, lastName, email);
        return newMember;
    }
    private void handleGetProducts(Socket clientSocket) throws IOException, SQLException {
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
                CONNECTION_CLOSE +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        //Loading pgr203.properties file and ensuring that file exists and all properties are submitted
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            if (properties.getProperty("dataSource.url") == null){
                logger.warn("Missing URL property in properties file.");
                } else if (properties.getProperty("dataSource.username") == null){
                    logger.warn("Missing USERNAME property in properties file.");
                    } else if (properties.getProperty("dataSource.password") == null){
                        logger.warn("Missing PASSWORD property in properties file.");
                    }
            properties.load(fileReader);
        } catch (Exception e){
            logger.warn("Properties file does not exist - " + e.getMessage());
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        logger.info("Using database {}", dataSource.getUrl());
        Flyway.configure().dataSource(dataSource).load().migrate();

        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Started on http://localhost:{}/index.html", 8080);
    }

    public List<ProjectMember> getProjectMembers() throws SQLException {
        return projectMemberDao.list();
    }
}