package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMemberDaoTest {

    private ProjectMemberDao memberDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:product-test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        memberDao = new ProjectMemberDao(dataSource);
    }

    @Test
    void shouldListInsertedMembers() throws SQLException {
        ProjectMember member1 = new ProjectMember("member1", "jevn", "test@email");
        ProjectMember member2 = new ProjectMember("member2", "jevn", "test@email");
        memberDao.insert(member1);
        memberDao.insert(member2);

        assertThat(memberDao.list())
                .extracting(ProjectMember::getFirstName)
                .contains(member1.getFirstName(), member2.getFirstName());
    }
}