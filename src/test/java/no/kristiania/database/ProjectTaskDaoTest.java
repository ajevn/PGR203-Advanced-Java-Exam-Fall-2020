package no.kristiania.database;

import no.kristiania.controllers.ProjectTaskOptionsController;
import no.kristiania.controllers.UpdateTaskController;
import no.kristiania.httpserver.HttpResponse;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.registerCustomDateFormat;

class ProjectTaskDaoTest {

    private ProjectTaskDao taskDao;
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:product-test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new ProjectTaskDao(dataSource);
    }

    @Test
    void shouldListInsertedTasks() throws SQLException {
        ProjectTask task1 = new ProjectTask(exampleTaskName(),"description1", "Green");
        ProjectTask task2 = new ProjectTask(exampleTaskName(),"description2", "Red");
        taskDao.insert(task1);
        taskDao.insert(task2);

        assertThat(taskDao.list())
                .extracting(ProjectTask::getName)
                .contains(task1.getName(), task2.getName());
    }
    @Test
    void shouldRetrieveTaskById() throws SQLException {
        taskDao.insert(exampleTask());
        taskDao.insert(exampleTask());
        ProjectTask projectTask = exampleTask();
        taskDao.insert(projectTask);

        assertThat(projectTask).hasNoNullFieldsOrProperties();

        assertThat(taskDao.retrieve(projectTask.getId()))
                .usingRecursiveComparison()
                .isEqualTo(projectTask);
    }

    @Test
    void shouldReturnTasksAsOptions() throws SQLException {
        ProjectTaskOptionsController controller = new ProjectTaskOptionsController(taskDao);
        ProjectTask task = exampleTask();
        taskDao.insert(task);

        assertThat(controller.getBody())

                .contains("<option value=" + task.getId() + ">" + task.getName() + "</option>");
    }

    @Test
    void shouldUpdateTaskStatus() throws IOException, SQLException {
        UpdateTaskController controller = new UpdateTaskController(taskDao);

        ProjectTask task = exampleTask();
        taskDao.insert(task);

        String body = "taskStatus=" + task.getStatus() + "&memberId=" + task.getId();

        HttpResponse response = new HttpResponse("302 Redirect", body);
        response.redirect("index.html");

        assertThat(taskDao.retrieve(task.getId()).getStatus())
                .isEqualTo(task.getStatus());
        assertThat(response.getStartLine())
                .isEqualTo("HTTP/1.1 302 Redirect");
        assertThat(response.getHeaders().get("Location"))
                .isEqualTo("/index.html");
    }

    @Test
    void shouldFilterTasksByStatus() throws SQLException {
        ProjectTask task = exampleTask();
        taskDao.insert(task);
        ProjectTask otherTask = exampleTask();
        taskDao.insert(otherTask);


        ProjectTask matchingTask = new ProjectTask("task1", "description 1", "Completed");
        taskDao.insert(matchingTask);
        ProjectTask nonMatchingTask = new ProjectTask("task2", "description2", "Todo");
        taskDao.insert(nonMatchingTask);

        assertThat(taskDao.retrieveTaskByStatus("Completed"))
                .extracting(ProjectTask::getStatus)
                .contains(matchingTask.getStatus())
                .doesNotContain(nonMatchingTask.getStatus());
    }

    // Test data generators
    private ProjectTask exampleTask() {
        ProjectTask task = new ProjectTask();
        task.setName(exampleTaskName());
        task.setDescription("Example Desc");
        task.setStatus(exampleTaskStatus());
        return task;
    }


    private String exampleTaskName() {
        String[] options = {"Clean", "Scrub", "Fix Broken", "Something else"};
        return options[random.nextInt(options.length)];
    }
    private String exampleTaskStatus() {
        String[] options = {"Todo", "In-Progress", "Completed", "Cancelled"};
        return options[random.nextInt(options.length)];
    }

}