package no.kristiania.database;

public class ProjectTask {

    private String name;
    private String status;
    private long id;

    public ProjectTask() {
    }

    public ProjectTask(String name, String description) {
        this.name = name;
        this.status = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}