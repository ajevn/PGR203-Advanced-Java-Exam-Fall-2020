package no.kristiania.httpserver;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {
    private ProjectTaskDao taskDao;
    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() {
        ProjectTaskDao projectTaskDao;
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        taskDao = new ProjectTaskDao(dataSource);
    }
    @Test
    void checkFilterTaskByStatus() throws IOException, SQLException {
        HttpServer server = new HttpServer(10009, dataSource);
        ProjectTask task1 = new ProjectTask("Clean", "Something", "Completed");
        ProjectTask task2 = new ProjectTask("Clean", "Something", "Cancelled");
        taskDao.insert(task1);
        taskDao.insert(task2);

        //Checks for no return on status "Todo" - Previosly added
        HttpClient client = new HttpClient("localhost", 10009, "/api/tasks?taskStatus=Todo" );

        //<ul><li><Strong>Task: </Strong>Clean <br> <Strong>Description:</Strong> Something <br> <Strong>Status:</Strong> Completed<br> <Strong>Assigned to:<br></Strong> </li></ul>
        assertEquals("<ul><h3> No tasks matching status: Todo</ul>", client.getResponseBody());
    }

    @Test
    void shouldReturnSuccessfulStatusCode() throws IOException {
        new HttpServer(10001, dataSource);
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulStatusCode() throws IOException {
        new HttpServer(10002, dataSource);
        HttpClient client = new HttpClient("localhost", 10002, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldHandleNonexistentRequestTarget() throws IOException {
        new HttpServer(10003, dataSource);
        HttpClient client = new HttpClient("localhost", 10003, "/index.ht");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnContentLength() throws IOException {
        new HttpServer(10004, dataSource);
        HttpClient client = new HttpClient("localhost", 10004, "/echo?body=HelloWorld");
        assertEquals("34", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        new HttpServer(10005, dataSource);
        HttpClient client = new HttpClient("localhost", 10005, "/echo?body=HelloWorld");
        assertEquals("<h2>Echo:</h2><h4> HelloWorld</h4>", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        HttpServer server = new HttpServer(10006, dataSource);
        File contentRoot = new File("target/test-classes");

        String fileContent = "Hello World " + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", 10006, "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10007, dataSource);
        File contentRoot = new File("target/test-classes");

        Files.writeString(new File(contentRoot, "index.html").toPath(), "<h2>Hello World</h2>");

        HttpClient client = new HttpClient("localhost", 10007, "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }


    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpServer server = new HttpServer(10008, dataSource);
        File contentRoot = new File("target/");

        HttpClient client = new HttpClient("localhost", 10008, "/notFound.txt");
        assertEquals(404, client.getStatusCode());
    }
}