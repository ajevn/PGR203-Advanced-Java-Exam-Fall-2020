package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectTaskDao extends AbstractDao<ProjectTask> {

    public ProjectTaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(ProjectTask projectTask) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO project_tasks (task_name, task_description, task_status)"
                            + " VALUES(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, projectTask.getName());
                statement.setString(2, projectTask.getDescription());
                statement.setString(3, projectTask.getStatus());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    projectTask.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public void updateTaskStatus(String newStatus, int id) throws SQLException {
        super.update("UPDATE project_tasks SET task_status = '" + newStatus + "' WHERE id = ?;", id);
    }

    public ProjectTask retrieve(int id) throws SQLException {
        return super.retrieve(id, "SELECT * FROM project_tasks WHERE id = ?");
    }

    // Creating new retrieve method to allow for String parameter
    public List<ProjectTask> retrieveTaskByStatus(String status) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM project_tasks WHERE task_status = ?")) {
                statement.setString(1, status);
                try (ResultSet rs = statement.executeQuery()) {
                    List<ProjectTask> projectTasks = new ArrayList<>();
                    while (rs.next()) {
                        projectTasks.add(mapRow(rs));
                    }
                    return projectTasks;
                }
            }
        }
    }


    @Override
    protected ProjectTask mapRow(ResultSet rs) throws SQLException {
        ProjectTask task = new ProjectTask();
        task.setId(rs.getInt("id"));
        task.setName(rs.getString("task_name"));
        task.setDescription(rs.getString("task_description"));
        task.setStatus(rs.getString("task_status"));
        return task;
    }

    public List<ProjectTask> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM project_tasks")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<ProjectTask> projectTasks = new ArrayList<>();
                    while (rs.next()) {
                        projectTasks.add(mapRow(rs));
                    }
                    return projectTasks;
                }
            }
        }
    }
}
