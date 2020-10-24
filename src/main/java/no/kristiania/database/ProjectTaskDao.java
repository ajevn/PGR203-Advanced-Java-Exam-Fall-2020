package no.kristiania.database;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectTaskDao extends AbstractDao<ProjectTask>{

    public ProjectTaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(ProjectTask projectTask) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO project_tasks (task_name, task_status)"
                    + " VALUES(?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, projectTask.getName());
                statement.setString(2, projectTask.getStatus());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    projectTask.setId(generatedKeys.getLong("id"));
                }
            }
        }
    }

    public ProjectTask retrieve(Long id) throws SQLException {
        return super.retrieve(id, "SELECT * FROM project_tasks WHERE id = ?");
    }

    @Override
    protected ProjectTask mapRow(ResultSet rs) throws SQLException {
        ProjectTask task = new ProjectTask();
        task.setId(rs.getLong("id"));
        task.setName(rs.getString("task_name"));
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
