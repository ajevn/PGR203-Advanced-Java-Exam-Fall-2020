package no.kristiania.database;

import no.kristiania.httpserver.ProjectMember;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMemberDaoTest {

    private ProjectMemberDao memberDao;

    // This method is executed before each test method
    @BeforeEach
    void setUp() {
        // A DataSource specifies HOW TO connect to a db: hostname, port, database name, password
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:product-test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();

        memberDao = new ProjectMemberDao(dataSource);
    }

    @Test
    void shouldListAllProduct() throws SQLException {
        ProjectMember member1 =  new ProjectMember("Andreas", "jevn", "test@email");
        memberDao.insert(member1);

        // List back all the objects in the database
        assertThat(memberDao.list())
                // to make it easy, just consider the name property of the products
                .extracting(ProjectMember::getFirstName)
                // ensure that they are both there
                .contains(member1.getFirstName());
    }

}