import java.util.Objects;

public abstract class Objective {

    protected String name;
    protected String description;
    protected Status status;
    protected TaskType taskType;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Objective(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Objective changeStatus(Status status) {
        Objective newTask = this;
        newTask.setStatus(status);
        return newTask;
    }

    @Override
    public String toString() {
        return "Objective{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Objective objective = (Objective) o;
        return Objects.equals(name, objective.name) && Objects.equals(description, objective.description) && status == objective.status && taskType == objective.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, taskType);
    }
}
