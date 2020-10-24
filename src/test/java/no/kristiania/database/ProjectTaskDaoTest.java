package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTaskDaoTest {

    private ProjectTaskDao taskDao;
    private Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:product-test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new ProjectTaskDao(dataSource);
    }

    @Test
    void shouldListInsertedTasks() throws SQLException {
        ProjectTask task1 = new ProjectTask(exampleTaskName(), "Green");
        ProjectTask task2 = new ProjectTask(exampleTaskName(), "Red");
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

    private ProjectTask exampleTask() {
        ProjectTask task = new ProjectTask();
        task.setName(exampleTaskName());
        task.setStatus("Green");
        return task;
    }


    private String exampleTaskName() {
        String[] options = {"Clean", "Scrub", "Fix Broken", "Something else"};
        return options[random.nextInt(options.length)];
    }

}