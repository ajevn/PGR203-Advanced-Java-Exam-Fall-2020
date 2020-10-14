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
import java.util.Scanner;

public class ProjectMemberDao {

    private DataSource dataSource;

    public ProjectMemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("kristianiashopuser");
        dataSource.setPassword("asdqwe123");

        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);

        System.out.println("Please enter name:");
        Scanner scanner = new Scanner(System.in);
        String memberName = scanner.nextLine();
        System.out.println("Please enter email:");
        String memberEmail = scanner.nextLine();

        ProjectMember member = new ProjectMember(memberName, memberEmail);
        projectMemberDao.insert(member);
    }

    public void insert(ProjectMember projectMember) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO project_members (member_name, member_email) "
                    + "VALUES(?,?)")) {
                statement.setString(1, projectMember.getName());
                statement.setString(2, projectMember.getEmail());
                statement.executeUpdate();
            }
        }
    }

    public List<ProjectMember> list() throws SQLException {
        List<ProjectMember> members = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        ProjectMember member = new ProjectMember(
                                (rs.getString("member_name")),
                                (rs.getString("member_email")));
                        members.add(member);
                    }
                }
            }
        }
        return members;
    }

    public ProjectMember retrieve(Long id) {
        return null;
    }
}