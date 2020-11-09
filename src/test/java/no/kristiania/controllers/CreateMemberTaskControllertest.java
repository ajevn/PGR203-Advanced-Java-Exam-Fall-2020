package no.kristiania.controllers;

import no.kristiania.database.MemberTask;
import no.kristiania.database.MemberTaskDao;
import no.kristiania.database.ProjectMemberDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateMemberTaskControllertest {
    MemberTaskDao memberTaskDao;
    ProjectMemberDao projectMemberDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:product-test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        memberTaskDao = new MemberTaskDao(dataSource);
        projectMemberDao = new ProjectMemberDao(dataSource);
    }


    @Test
    void checkMultipleAssignmentsForUser() throws SQLException, IOException {
        MemberTask member1 = new MemberTask(1, 2);
        MemberTask member2 = new MemberTask(1, 2);
        memberTaskDao.insert(member1);

        CreateMemberTaskController createMemberTaskController = new CreateMemberTaskController(memberTaskDao, projectMemberDao);

        boolean exists = createMemberTaskController.checkIfAssignmentExists(member2.getMemberId(), member2.getTaskId());
        assertThat(exists).isTrue();

    }
}
