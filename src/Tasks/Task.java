package Tasks;

public class Task extends AbstractTask {

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
