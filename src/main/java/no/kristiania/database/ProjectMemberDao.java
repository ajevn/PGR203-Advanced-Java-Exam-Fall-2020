package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectMemberDao extends AbstractDao<ProjectMember>{

    public ProjectMemberDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(ProjectMember projectMember) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO project_members (member_firstName, member_lastName, member_email)"
                    + " VALUES(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, projectMember.getFirstName());
                statement.setString(2, projectMember.getLastName());
                statement.setString(3, projectMember.getEmail());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    projectMember.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public ProjectMember retrieve(int id) throws SQLException {
        return super.retrieve(id, "SELECT * FROM project_members WHERE id = ?");
    }

    @Override
    protected ProjectMember mapRow(ResultSet rs) throws SQLException {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setId(rs.getInt("id"));
        projectMember.setFirstName(rs.getString("member_firstname"));
        projectMember.setLastName(rs.getString("member_lastname"));
        projectMember.setEmail(rs.getString("member_email"));
        return projectMember;
    }

    public List<ProjectMember> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM project_members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<ProjectMember> projectMembers = new ArrayList<>();
                    while (rs.next()) {
                        projectMembers.add(mapRow(rs));
                    }
                    return projectMembers;
                }
            }
        }
    }
}