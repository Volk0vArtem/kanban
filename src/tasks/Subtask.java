package tasks;

public class Subtask extends AbstractTask {

    private Epic epic;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String id, String name, String status, String description, Epic epic) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.status = Status.valueOf(status);
        this.description = description;
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
    }

    public Epic getEpic() {
        return epic;
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

    @Override
    public String toCSV() {
        return id + "," + taskType + "," + name + "," + status + "," + description + "," + epic.getId();
    }
}
