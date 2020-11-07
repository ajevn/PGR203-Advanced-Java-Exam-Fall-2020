package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTaskDaoTest {

    private final Random random = new Random();
    private MemberTaskDao memberTaskDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:product-test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        memberTaskDao = new MemberTaskDao(dataSource);
    }


    @Test
    void shouldRetrieveTaskByMemberId() throws SQLException {
        MemberTask member1 = new MemberTask(exampleData(), exampleData());
        memberTaskDao.insert(member1);

        assertThat(memberTaskDao.retrieveByMemberId(member1.getMemberId())
                .contains(member1.getMemberId()));
    }

    @Test
    void shouldListInsertedMembers() throws SQLException {
        MemberTask member1 = new MemberTask(exampleData(), exampleData());
        MemberTask member2 = new MemberTask(exampleData(), exampleData());
        memberTaskDao.insert(member1);
        memberTaskDao.insert(member2);

        assertThat(memberTaskDao.list())
                .extracting(MemberTask::getMemberId)
                .contains(member1.getMemberId(), member2.getMemberId());
    }
    @Test
    void shouldRetrieveTaskByTaskId() throws SQLException {
        MemberTask member1 = new MemberTask(exampleData(), exampleData());
        memberTaskDao.insert(member1);

        assertThat(memberTaskDao.retrieveByMemberId(member1.getTaskId())
                .contains(member1.getTaskId()));
    }



    private Integer exampleData() {
        Integer[] memberId = {1, 2, 3, 4, 5, 6};
        return memberId[random.nextInt(memberId.length)];
    }

}