package tasks;

public class Subtask extends AbstractTask {

    private Epic epic;

    public Epic getEpic() {
        return epic;
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Tasks.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", epic='" + epic.getName() + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}
