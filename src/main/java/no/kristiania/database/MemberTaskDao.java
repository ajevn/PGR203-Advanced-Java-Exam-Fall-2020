package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberTaskDao extends AbstractDao<MemberTask>{
    public MemberTaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(MemberTask memberTask) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO member_task (member_id, task_id)"
                            + " VALUES(?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, memberTask.getMemberId());
                statement.setInt(2, memberTask.getTaskId());
                statement.executeUpdate();
            }
        }
    }

    public MemberTask retrieve(int id) throws SQLException {
        return super.retrieve(id, "SELECT * FROM member_task WHERE id = ?");
    }

    public List<MemberTask> retrieveByMemberId(int memberId) throws SQLException {
        return super.retrieveList(memberId, "SELECT * FROM member_task WHERE member_id = ?");
    }

    @Override
    protected MemberTask mapRow(ResultSet rs) throws SQLException {
        MemberTask memberTask = new MemberTask();
        memberTask.setMemberId(rs.getInt("member_id"));
        memberTask.setTaskId(rs.getInt("task_id"));
        return memberTask;
    }

    public List<MemberTask> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM member_task")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<MemberTask> memberTasks = new ArrayList<>();
                    while (rs.next()) {
                        memberTasks.add(mapRow(rs));
                    }
                    return memberTasks;
                }
            }
        }
    }
}
