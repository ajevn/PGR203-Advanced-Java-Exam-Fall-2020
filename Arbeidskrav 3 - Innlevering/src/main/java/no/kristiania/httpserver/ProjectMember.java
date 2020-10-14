package no.kristiania.httpserver;

public class ProjectMember {
    private String firstName;
    private String lastName;
    private String email;

    public ProjectMember() {
    }

    public ProjectMember(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String newName) {
        this.firstName = newName;
    }

    public void setLastName(String newName) {
        this.lastName = newName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
