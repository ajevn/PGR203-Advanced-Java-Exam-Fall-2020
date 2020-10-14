package no.kristiania.database;

import no.kristiania.httpserver.ProjectMember;
import no.kristiania.httpserver.ProjectTask;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProjectTaskDao {

    private DataSource dataSource;

    public ProjectTaskDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("kristianiashopuser");
        dataSource.setPassword("asdqwe123");

        ProjectTaskDao projectTaskDao = new ProjectTaskDao(dataSource);

        System.out.println("Please enter task:");
        Scanner scanner = new Scanner(System.in);
        String taskName = scanner.nextLine();
        System.out.println("Please enter description:");
        String taskDescription = scanner.nextLine();

        ProjectTask task = new ProjectTask(taskName, taskDescription);
        projectTaskDao.insert(task);
    }

    public void insert(ProjectTask projectTask) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO project_tasks(task_name, task_description) "
                    + "VALUES(?,?)")) {
                statement.setString(1, projectTask.getName());
                statement.setString(2, projectTask.getDescription());
                statement.executeUpdate();
            }
        }
    }

    public List<ProjectTask> list() throws SQLException {
        List<ProjectTask> tasks = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_tasks")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        ProjectTask task = new ProjectTask(
                                (rs.getString("task_name")),
                                (rs.getString("task_description")));
                        tasks.add(task);
                    }
                }
            }
        }
        return tasks;
    }

    public ProjectMember retrieve(Long id) {
        return null;
    }

}
