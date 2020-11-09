package no.kristiania.httpserver;

import no.kristiania.controllers.*;
import no.kristiania.database.MemberTaskDao;
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
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final Map<String, HttpController> controllers;
    public static final String CONNECTION_CLOSE = "Connection: close\r\n";

    public HttpServer(int port, DataSource dataSource) throws IOException {
        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);
        ProjectTaskDao projectTaskDao = new ProjectTaskDao(dataSource);
        MemberTaskDao memberTaskDao = new MemberTaskDao(dataSource);

        // Map of controllers serving request the appropriate controller.
        controllers = Map.of(
                "/api/tasks", new ProjectTaskController(projectTaskDao, memberTaskDao, projectMemberDao),
                "/api/newTask", new ProjectTaskController(projectTaskDao, memberTaskDao, projectMemberDao),
                "/api/updateTask", new UpdateTaskController(projectTaskDao),
                "/api/members", new ProjectMemberController(projectMemberDao),
                "/api/newMember", new ProjectMemberController(projectMemberDao),
                "/api/newMemberTask", new MemberTaskController(memberTaskDao, projectMemberDao),
                "/api/taskOptions", new ProjectTaskOptionsController(projectTaskDao),
                "/api/memberOptions", new ProjectMemberOptionsController(projectMemberDao),
                "/echo", new OtherRequestPathController(),
                "/", new OtherRequestPathController()
        );

        ServerSocket serverSocket = new ServerSocket(port);
        new Thread(() -> {
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleRequest(clientSocket);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Method for handling the request from clientSocket.
    private void handleRequest(Socket clientSocket) throws IOException, SQLException {
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        logger.info("Request {} - Port {}", requestLine, clientSocket.getPort());

        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf('?');
        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        // Serves request the appropriate controller - If requestPath is not included in controllers Map it is directed to handleFileRequest
        HttpController controller = controllers.get(requestPath);
        if (controller != null) {
            controller.handle(request, clientSocket);
        } else {
            handleFileRequest(clientSocket, requestPath);
        }
    }

    // Serves file depending on inputStream. Serves custom 404 error page if no file is found.
    private void handleFileRequest(Socket clientSocket, String requestPath) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(requestPath)) {
            if (inputStream == null) {
                HttpErrorMessage errorMessage = new HttpErrorMessage(requestPath, 404, "Not Found");
                String body = errorMessage.getErrorMessage();

                HttpResponse response = new HttpResponse("404 Not Found", body);
                response.write(clientSocket);
                return;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            } else if (requestPath.endsWith(".css")) {
                contentType = "text/css";
            }

            //Manually writing response for this method.
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    CONNECTION_CLOSE +
                    "\r\n";

            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
        }
    }

    //Main method responsible for initially running program.
    // Validating properties file so it works with user's PostgreSQL database.
    public static void main(String[] args) throws IOException {
        //Loading pgr203.properties file and ensuring that file exists and that required property keys exists and has a value
        Properties properties = new Properties();
        String[] propKeys = {"dataSource.url", "dataSource.username", "dataSource.password"};
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
            for (String key : propKeys) {
                if (!properties.containsKey(key)) {
                    logger.warn("Properties file missing key: " + key);
                } else if (properties.getProperty(key).length() == 0) {
                    logger.warn("Missing value for property: " + key);
                }
            }
        } catch (Exception e) {
            logger.warn("Properties file does not exist - " + e.getMessage());
        }

        //Configuring and setting up database and creates SQL tables using Flyway
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        logger.info("Using database {}", dataSource.getUrl());
        Flyway.configure().dataSource(dataSource).load().migrate();

        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Started on http://localhost:{}/index.html", 8080);
    }
}