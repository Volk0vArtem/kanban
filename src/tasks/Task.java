package tasks;

public class Task extends AbstractTask {

    public Task(String id, String name, String status, String description){
        this.id = Integer.parseInt(id);
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(status);
        this.description = description;
        this.taskType = TaskType.TASK;
    }

    public Task(String name, String description) {
        super(name, description);
        this.taskType = TaskType.TASK;
    }

    public String toString() {
        return "Tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}
