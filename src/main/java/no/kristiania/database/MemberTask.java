package no.kristiania.database;

public class MemberTask {
    private int memberId;
    private int taskId;

    public MemberTask() {
    }

    public MemberTask(int memberId, int taskId) {
        this.memberId = memberId;
        this.taskId = taskId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
