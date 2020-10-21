package no.kristiania.database;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectTaskDao {

    private DataSource dataSource;

    public ProjectTaskDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        ProjectMemberDao projectTaskDao = new ProjectMemberDao(dataSource);

    }

    public void insert(ProjectTask projectTask) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO project_tasks (task_name," +
                    " task_status) "
                    + "VALUES(?,?)")) {
                statement.setString(1, projectTask.getName());
                statement.setString(2, projectTask.getStatus());
                statement.executeUpdate();
            }
        }
    }

    private ProjectTask mapRowToTask(ResultSet rs) throws SQLException {
        ProjectTask projectTask = new ProjectTask();
        projectTask.setName(rs.getString("task_name"));
        projectTask.setStatus(rs.getString("task_status"));
        return projectTask;
    }

    public List<ProjectTask> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM project_tasks")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<ProjectTask> projectTasks = new ArrayList<>();
                    while (rs.next()) {
                        projectTasks.add(mapRowToTask(rs));
                    }
                    return projectTasks;
                }
            }
        }
    }
}
