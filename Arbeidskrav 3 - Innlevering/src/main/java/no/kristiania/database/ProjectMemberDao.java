package no.kristiania.database;
import no.kristiania.httpserver.ProjectMember;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectMemberDao {

    private DataSource dataSource;

    public ProjectMemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("user_db");
        dataSource.setPassword("asdqwe123");

        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);

        System.out.println(projectMemberDao.list());
    }

    public void insert(ProjectMember projectMember) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO project_members (member_firstName," +
                    " member_lastName," +
                    " member_email) "
                    + "VALUES(?,?,?)")) {
                statement.setString(1, projectMember.getFirstName());
                statement.setString(2, projectMember.getLastName());
                statement.setString(3, projectMember.getEmail());
                statement.executeUpdate();
            }
        }
    }

    private ProjectMember mapRowToMember(ResultSet rs) throws SQLException {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setFirstName(rs.getString("member_firstname"));
        projectMember.setLastName(rs.getString("member_lastname"));
        projectMember.setEmail(rs.getString("member_email"));
        return projectMember;
    }

    public List<ProjectMember> list() throws SQLException {
        List<ProjectMember> projectMembers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM project_members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        ProjectMember member = new ProjectMember(
                                (rs.getString("member_firstname")),
                                (rs.getString("member_lastname")),
                                (rs.getString("member_email")));
                        projectMembers.add(member);
                    }
                }
            }
        }
        return projectMembers;
    }
    public ProjectMember retrieve(Long id) {
        return null;
    }
}