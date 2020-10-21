package no.kristiania.database;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AbstractDao {

    private DataSource dataSource;
    private ProjectMember projectMember;
    private ProjectTask projectTask;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);

    }

    public void insert(Object object) throws SQLException {
        if (object.getClass() == ProjectMember) {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO project_members (member_firstName," +
                        " member_lastName," +
                        " member_email) "
                        + "VALUES(?,?,?)")) {
                    statement.setString(1, object.getFirstName());
                    statement.setString(2, object.getLastName());
                    statement.setString(3, projectMember.getEmail());
                    statement.executeUpdate();
                }
            }
        }
    }
}
